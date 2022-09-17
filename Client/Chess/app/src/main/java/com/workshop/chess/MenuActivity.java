package com.workshop.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class MenuActivity extends AppCompatActivity {

    Button btn_startGame, btn_viewRules, btn_viewProgress, btn_signOut;
    TextView logged, firstPlaceUsername, firstPlaceWins, secondPlaceUsername, secondPlaceWins, thirdPlaceUsername, thirdPlaceWins, txt_wins, txt_loses, txt_draws;
    Dialog dialog;
    String player; //current player username
    boolean turn = false;   //the player's turn
    RequestQueue requestQueue;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn_startGame = findViewById(R.id.btn_startGame);
        btn_viewRules = findViewById(R.id.btn_viewRules);
        btn_viewProgress = findViewById(R.id.btn_viewProgress);
        btn_signOut = findViewById(R.id.btn_sign_out);
        logged = findViewById(R.id.txt_logged);

        dialog = new Dialog(this);

        Singleton.disconnectStomp();

        player = getIntent().getStringExtra("username");
        logged.setText("Logged in as: " + player);

        btn_startGame.setOnClickListener(view -> registerToPlay());

        btn_viewRules.setOnClickListener(view -> {
            dialog.setContentView(R.layout.rules_popup);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        btn_viewProgress.setOnClickListener(view -> showProgressPopup());

        btn_signOut.setOnClickListener(v -> {
            Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginScreen);
            finish();
        });
    }

    /** sends a request to the server that this player want to start a game - register him as a participant. */
    private void registerToPlay(){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:8080/participants/" + player, null, response -> {
            try {
                if (response.getString("first_player").equals(""))  //this means there is no other player waiting to play
                                                                        // - this player is waiting - so he is first to play when another player connects.
                    turn = true;
                Intent loadScreen = new Intent(getApplicationContext(), LoadActivity.class);
                loadScreen.putExtra("turn", turn);
                loadScreen.putExtra("username", player);
                startActivity(loadScreen);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(MenuActivity.this, "Connection error. Try again.", Toast.LENGTH_SHORT).show());
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    /** sends a request to the server to get the player's progress in games. */
    private void playerProgress(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://10.0.2.2:8080/participants/" + player;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            try {
                txt_wins.setText(response.getString("wins"));
                txt_loses.setText(response.getString("loses"));
                txt_draws.setText(response.getString("draws"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> { Log.e("VOLLEY", error.toString()); });
        requestQueue.add(request);
    }

    /** sends a request to the server to get the top3 players. */
    private void top3Players(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://10.0.2.2:8080/participants";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null, response -> {
            try {
                JSONArray winners = new JSONArray(response.toString());
                firstPlaceUsername.setText(response.getJSONObject(0).getString("userName"));
                firstPlaceWins.setText(response.getJSONObject(0).getString("wins"));
                secondPlaceUsername.setText(response.getJSONObject(1).getString("userName"));
                secondPlaceWins.setText(response.getJSONObject(1).getString("wins"));
                thirdPlaceUsername.setText(response.getJSONObject(2).getString("userName"));
                thirdPlaceWins.setText(response.getJSONObject(2).getString("wins"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> { Log.e("VOLLEY", error.toString()); });
        requestQueue.add(request);
    }

    /** shows the progress popup window with all info needed. */
    private void showProgressPopup(){
        dialog.setContentView(R.layout.progress_popup);
        firstPlaceUsername = dialog.findViewById(R.id.first_place_username);
        firstPlaceWins = dialog.findViewById(R.id.first_place_wins);
        secondPlaceUsername = dialog.findViewById(R.id.second_place_username);
        secondPlaceWins = dialog.findViewById(R.id.second_place_wins);
        thirdPlaceUsername = dialog.findViewById(R.id.third_place_username);
        thirdPlaceWins = dialog.findViewById(R.id.third_place_wins);
        txt_wins = dialog.findViewById(R.id.wins_cnt);
        txt_loses = dialog.findViewById(R.id.loses_cnt);
        txt_draws = dialog.findViewById(R.id.draw_cnt);

        playerProgress();
        top3Players();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}