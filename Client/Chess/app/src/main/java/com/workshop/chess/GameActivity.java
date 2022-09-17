package com.workshop.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.StompClient;

/** The Controller */
public class GameActivity extends AppCompatActivity implements ChessService {
    private ChessView chessView;
    private final ChessGame chessModel = new ChessGame();
    Button btn_dropOut;
    String player, userID;
    boolean turn = false;
    String playerColor;
    boolean gameEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        btn_dropOut = findViewById(R.id.btn_drop_out);
        btn_dropOut.setOnClickListener(v -> {
            Singleton.disconnectStomp();
            UpdateScore(-1);
            finish();
        });

        chessView = findViewById(R.id.chess_view);
        chessView.chessService = this;

        //getting the player username
        player = getIntent().getStringExtra("username");
        userID = getIntent().getStringExtra("userID");

        //getting the player initial turn (if he is the first player or not)
        turn = getIntent().getBooleanExtra("turn", false);


        //deciding the players colors
        if (turn) {
            playerColor = "WHITE";
            Toast.makeText(getApplicationContext(), "You are the White Player!", Toast.LENGTH_SHORT).show();
        }
        else {
            playerColor = "BLACK";
            Toast.makeText(getApplicationContext(), "You are the Black Player!", Toast.LENGTH_SHORT).show();
        }

        OpponentMove(Singleton.getmStompClient());
    }

    @Override
    public ChessPiece pieceAt(int col, int row) {
        return chessModel.pieceAt(col, row);
    }

    @Override
    public void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        if (chessModel.checkPiece(fromCol, fromRow, toCol, toRow, playerColor) && turn) {
            moveValidation(fromCol, fromRow, toCol, toRow);
        }
    }

    @Override
    public void moveOpponentPiece(int fromCol, int fromRow, int toCol, int toRow) {
        chessModel.movePiece(fromCol, fromRow, toCol, toRow);
        chessView.invalidate();
    }

    /** Convert the move from string to four ints and moves the piece. */
    public void ConvertAndMovePiece(String move){
        String from = move.substring(0, 2);
        String to = move.substring(2, 4);
        int fromCol = from.charAt(1) - 'a'; //getting the number of the column to move from
        int fromRow = Integer.parseInt(from.substring(0, 1)) - 1;   //getting the number of the row to move from
        int toCol = to.charAt(1) - 'a'; //getting the number of the column to move to
        int toRow = Integer.parseInt(to.substring(0, 1)) - 1; //getting the number of the column to move to

        moveOpponentPiece(fromCol, fromRow, toCol, toRow);
    }

    /** Sends the move to the server to know if the move made is valid or not. */
    private void moveValidation(int fromCol, int fromRow, int toCol, int toRow) {
        //converting the positions to the right format
        String from = Integer.toString(fromRow + 1) + (char) ('a' + fromCol);
        String to = Integer.toString(toRow + 1) + (char) ('a' + toCol);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://10.0.2.2:8080/moves/" + from + to + "?user_name=" + player;
        JSONObject jsonObject = chessModel.getCurrPieces();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, response -> {
            String body = null;
            try {
                // getting the server message
                body = response.getString("response");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert body != null;
            switch (body) {
                case "VALID":
                    chessModel.movePiece(fromCol, fromRow, toCol, toRow);
                    chessView.invalidate();
                    turn = false;
                    break;
                case "50MOVES":
                    chessModel.movePiece(fromCol, fromRow, toCol, toRow);
                    chessView.invalidate();
                    Toast.makeText(getApplicationContext(), "Draw! Fifty-Move Rule", Toast.LENGTH_SHORT).show();
                    UpdateScore(2);
                    gameEnded = true;
                    break;
                case "CHECKMATE":
                    chessModel.movePiece(fromCol, fromRow, toCol, toRow);
                    chessView.invalidate();
                    Toast.makeText(getApplicationContext(), "Checkmate! You Won", Toast.LENGTH_SHORT).show();
                    gameEnded = true;
                    UpdateScore(1);
                    break;
            }
        }, error -> { Log.e("VOLLEY", error.toString()); });
        requestQueue.add(request);
    }

    /** deals with a move made by the opponent - which gets through websockets. */
    public void OpponentMove(StompClient mStompClient){

        Disposable dispTopic =  mStompClient.topic("/players/" + userID + "/topic/chess").subscribe(topicMessage -> {
            String message, userName, move, mType;
            JSONObject response;

            try{
                response = new JSONObject(topicMessage.getPayload());
                mType = response.getString("type");
                message = response.getString("message");
                move = response.getString("move");
                //a move has been made
                if(mType.equals("MOVE")){
                    //the move was valid
                    switch (message) {
                        case "VALID":
                            ConvertAndMovePiece(move);
                            turn = true;
                            break;
                        case "CHECKMATE":
                            ConvertAndMovePiece(move);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Checkmate! Opponent Won", Toast.LENGTH_SHORT).show();                                    }
                            });
                            UpdateScore(0);
                            gameEnded = true;
                            break;
                        case "50MOVES":
                            ConvertAndMovePiece(move);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Draw! Fifty-Move Rule", Toast.LENGTH_SHORT).show();
                                }
                            });
                            UpdateScore(2);
                            gameEnded = true;
                            break;
                    }
                } //the opponent disconnected
                else if (mType.equals("DISCONNECT")){
                    runOnUiThread(() -> {
                        if(!gameEnded) {    //if player was disconnected before the game ended
                            Toast.makeText(getApplicationContext(), "Opponent Disconnected!", Toast.LENGTH_SHORT).show();
                            UpdateScore(-1);
                        }
                    });
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        },throwable -> {
            Log.e("Chess", "Error on subscribe topic", throwable);
        } );
    }

    /** sends a request to the server to update the score for the player. */
    public void UpdateScore(int score) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://10.0.2.2:8080/participants?user_name=" + player + "&score=" + score;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Intent menuScreen = new Intent(getApplicationContext(), MenuActivity.class);
            menuScreen.putExtra("username", player);
            startActivity(menuScreen);
            finish();
        }, error -> {
            Toast.makeText(getApplicationContext(), "Connection error.", Toast.LENGTH_SHORT).show();
        });
        requestQueue.add(stringRequest);
    }
}