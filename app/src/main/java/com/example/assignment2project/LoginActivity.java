package com.example.assignment2project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        // Check if the users detail match
        if (user.equals("admin@quiz.com")) {
            if (pass.equals("admin123")) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }
        }
        Toast.makeText(this, "Invalid credentials (Use: admin@quiz.com / admin123)", Toast.LENGTH_SHORT).show();
    }
}
