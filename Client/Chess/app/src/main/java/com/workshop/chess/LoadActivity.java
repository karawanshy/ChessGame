package com.workshop.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.StompClient;

public class LoadActivity extends AppCompatActivity {

    Singleton singleton;
    Button btn_go_back;
    TextView txt_logged;
    String player;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        btn_go_back = findViewById(R.id.btn_go_back);
        txt_logged = findViewById(R.id.txt_logged);


        //getting the player user name from menuActivity
        player = getIntent().getStringExtra("username");
        txt_logged.setText("Logged in as: " + player);

        singleton = Singleton.getInstance(player);

        WaitForOpponent(Singleton.getmStompClient());

        btn_go_back.setOnClickListener(view -> {
            deleteParticipant();
            Intent menuScreen = new Intent(getApplicationContext(), MenuActivity.class);
            menuScreen.putExtra("username", player);
            startActivity(menuScreen);
            finish();
        });
    }

    public void OpponentConnected(String userID){
        Intent gameScreen = new Intent(getApplicationContext(), GameActivity.class);

        //sending the players turn (if he is the first player or not) from the menu to the game
        gameScreen.putExtra("turn", getIntent().getBooleanExtra("turn", false));
        gameScreen.putExtra("username", player);
        gameScreen.putExtra("userID", userID);
        startActivity(gameScreen);
        finish();
    }

    /** waiting for a message from the server that an opponent has connected. */
    public void WaitForOpponent(StompClient mStompClient){
        AtomicReference<String> userID = new AtomicReference<>("");
        Disposable dispTopic =  mStompClient.topic("/players/topic/chess").subscribe(topicMessage -> {
            String mType, userName;
            JSONObject response;

            try{
                response = new JSONObject(topicMessage.getPayload());
                mType = response.getString("type");
                userName = response.getString("userName");

                if(mType.equals("CONNECT")){
                    // if the number of connected users is even that means no user is waiting for an opponent any more.
                   if (player.equals(userName))
                       userID.set(response.getString("message"));
                    if (Integer.parseInt(response.getString("players")) % 2 == 0){
                        OpponentConnected(userID.get());
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        });
    }

    /** sends a delete request to the server in case a player decided to give up on waiting for an opponent. */
    private void deleteParticipant(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://10.0.2.2:8080/participants/" + player;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, URL, response -> {}, error -> {
            Toast.makeText(getApplicationContext(), "Connection error.", Toast.LENGTH_SHORT).show();
        });
        requestQueue.add(stringRequest);
    }
}