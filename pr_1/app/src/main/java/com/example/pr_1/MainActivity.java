package com.example.pr_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton,btnRegi;
    private DbManager dbManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        btnRegi = findViewById(R.id.loginRegister);
        dbManager = new DbManager(this);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        btnRegi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Registration.class);
                startActivity(i);
            }
        });
        // Check if user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // Redirect the user to their respective dashboard
            GotoDashboard(sharedPreferences.getString("userRole", ""));
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Check if username and password are valid
                if (isValidCredentials(username, password)) {
                    dbManager.open();

                    String userRole = dbManager.getUserRole(username, password);
                    String empname = username;
                    if (userRole != null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("userRole", userRole);
                        editor.putString("ename",empname);
                        editor.apply();

                        // Redirect user to respective dashboard
                        GotoDashboard(userRole);
                    }
                    // Close the database connection
                    dbManager.close();
                } else {
                    Toast.makeText(MainActivity.this, "Invaild Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidCredentials(String username, String password) {
        return dbManager.isValidUser(username, password);
    }

    private void GotoDashboard(String userRole) {
        if (userRole.equalsIgnoreCase("Admin")) {
            startActivity(new Intent(MainActivity.this, AdminDashboard.class));
        }else if (userRole.equalsIgnoreCase("Employee")){
            startActivity(new Intent(MainActivity.this, EmployeeDashboard.class));
        }else{
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Not found role :: " + userRole, Toast.LENGTH_SHORT).show();
        }
        finish(); // Finish the login activity
    }
}
