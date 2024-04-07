package com.example.pr_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class Registration extends AppCompatActivity {

    EditText txtpass, txtname, txtemail;
    Button btnRegister;
    Spinner role;
    Button btnmylogin;

    Dbhelper helper;
    DbManager db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        helper = new Dbhelper(this);
        db = new DbManager(this);

        db.open();

        initialize();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onregister();
            }
        });

        btnmylogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotologin();
            }
        });
    }

    private void  gotologin(){
        Intent i = new Intent(Registration.this, MainActivity.class);
        startActivity(i);
    }
    private void viewallmyuser() {
        Intent i = new Intent(Registration.this, EmployeeDashboard.class);
        startActivity(i);
    }


    private void onregister() {
        String username = txtname.getText().toString().trim();
        String pass = txtpass.getText().toString().trim();
        String email = txtemail.getText().toString().trim();
        String userrole = role.getSelectedItem().toString().trim();

        if (username.isEmpty() || pass.isEmpty() || email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fill up all required Fields.", Toast.LENGTH_LONG).show();
            return;
        }

        if (db.isUsernameAvailable(username)){
        long i = db.add_UserDetails(username, email, pass, userrole);
        if (i > 0) {
            Toast.makeText(getApplicationContext(), "User Registered", Toast.LENGTH_LONG).show();
            gotologin();
        } else {
            Toast.makeText(getApplicationContext(), "Something Wents Wrong! ", Toast.LENGTH_LONG).show();
        }}else{
            Toast.makeText(getApplicationContext(), "Username Already Exits!", Toast.LENGTH_LONG).show();
        }

    }


    private void initialize() {
        txtpass = findViewById(R.id.etPassword);
        txtname = findViewById(R.id.etName);
        txtemail = findViewById(R.id.etEmail);

        btnRegister = findViewById(R.id.btnRegister);
        btnmylogin = findViewById(R.id.btnLoginRegister);

        role = findViewById(R.id.spinnerRole);

    }
}