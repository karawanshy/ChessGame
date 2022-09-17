package com.workshop.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText et_username, et_password;
    TextView txt_newAccount;
    Button btn_login;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // assign value to each control on the layout
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        txt_newAccount = findViewById((R.id.txt_cna));

        btn_login = findViewById(R.id.btn_login);

        // click listener for the login button
        btn_login.setOnClickListener(view -> loginValidation(response -> {
            try {
                String password = response.getString("password");
                String input = et_password.getText().toString();
                if (password.equals(input)) {
                    Intent nextScreen = new Intent(getApplicationContext(), MenuActivity.class);
                    nextScreen.putExtra("username", response.getString("user_name"));
                    startActivity(nextScreen);
                    finish();
                }
                else {
                    et_password.setError("Password is incorrect.");
                    et_password.requestFocus();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },"http://10.0.2.2:8080/players/" + et_username.getText() + ""));

        txt_newAccount.setOnClickListener(view -> {
            Intent signUpScreen = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(signUpScreen);
            finish();
        });
    }
    private interface VolleyCallback2{
        void onSuccess(JSONObject result);
    }

    private void loginValidation(final VolleyCallback2 callback, String URL){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, callback::onSuccess, error -> {
            et_username.setError("User not found.");
            et_username.requestFocus();
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}