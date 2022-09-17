package com.workshop.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    EditText et_firstName, et_lastName, et_username, et_password, et_email;
    Button btn_signUp;
    TextView txt_login;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_email = findViewById(R.id.et_email);

        btn_signUp = findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(v -> usernameValidation("http://10.0.2.2:8080/players/" + et_username.getText() + ""));

        txt_login = findViewById(R.id.txt_login);
        txt_login.setOnClickListener(v -> {
            Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginScreen);
            finish();
        });
    }

    /** sends a request to the server to get the users info for the given username. */
    private void usernameValidation(String URL) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            try {
                String username = response.getString("user_name");
                String input = et_username.getText().toString();
                if (username.equals(input)) {
                    et_username.setError("User already exists.");
                    et_username.requestFocus();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> validation());
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    /** checks if the given info is legal. if they are legal then registers the player. */
    private void validation() {
        if (et_username.getText().toString().trim().equalsIgnoreCase("")) {
            et_username.setError("This field can not be blank");
            et_username.requestFocus();
        } else if (et_password.getText().toString().trim().equalsIgnoreCase("")) {
            et_password.setError("This field can not be blank");
            et_password.requestFocus();
        } else if (et_firstName.getText().toString().trim().equalsIgnoreCase("")) {
            et_firstName.setError("This field can not be blank");
            et_firstName.requestFocus();
        } else if (et_lastName.getText().toString().trim().equalsIgnoreCase("")) {
            et_lastName.setError("This field can not be blank");
            et_lastName.requestFocus();
        } else if (et_email.getText().toString().trim().equalsIgnoreCase("")) {
            et_email.setError("This field can not be blank");
            et_email.requestFocus();
        }
        else if(!isEmailValid(et_email.getText().toString())) {
            et_email.setError("Please enter a valid E-mail");
            et_email.requestFocus();
        }
        else if (et_password.getText().toString().trim().length() < 7) {
            et_password.setError("Password should be longer than 7");
            et_password.requestFocus();
        } else {
            try {
                registerUser();
            } catch (AuthFailureError authFailureError) {
                authFailureError.printStackTrace();
            }
        }
    }

    /** checks if the given email is in the right format. */
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /** sends a request to the server to save the user info as a new user.*/
    private void registerUser() throws AuthFailureError {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JSONObject jsonBody = new JSONObject();
            String URL = "http://10.0.2.2:8080/players/";
            jsonBody.put("first_name", et_firstName.getText().toString());
            jsonBody.put("last_name", et_lastName.getText().toString());
            jsonBody.put("user_name", et_username.getText().toString());
            jsonBody.put("password", et_password.getText().toString());
            jsonBody.put("email", et_email.getText().toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, response -> {
                Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_SHORT).show();
            }, error -> {
                Toast.makeText(getApplicationContext(), "Connection error. Try Again", Toast.LENGTH_SHORT).show();
                Log.e("VOLLEY", error.toString());
            });
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
