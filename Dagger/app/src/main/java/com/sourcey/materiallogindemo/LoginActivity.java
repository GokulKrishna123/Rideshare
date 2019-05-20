package com.sourcey.materiallogindemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


//defining views
private Button buttonSignIn;
private EditText editTextEmail;
private EditText editTextPassword;
private TextView textViewSignup;

//firebase auth object
private FirebaseAuth firebaseAuth;

//progress dialog
private ProgressDialog progressDialog;


     //   https://www.google.com/maps/dir/?api=1&origin=aruvikkara&destination=kottayam&travelmode=bicycling

     //   https://www.google.com/maps/dir/?api=1&origin=trivandrum&destination=ernakulam&travelmode=driving&waypoints=kottayam




@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if(firebaseAuth.getCurrentUser() != null){
        //close this activity
        finish();
        //opening profile activity
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewSignup  = (TextView) findViewById(R.id.textViewSignUp);

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        }

//method for user login
private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();


        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
        Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
        return;
        }

        if(TextUtils.isEmpty(password)){
        Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
        return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
@Override
public void onComplete(@NonNull Task<AuthResult> task) {
        progressDialog.dismiss();
        //if the task is successfull
        if(task.isSuccessful()){
        //start the profile activity
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        }
        });

        }

@Override
public void onClick(View view) {
        if(view == buttonSignIn){
        userLogin();
        }

        if(view == textViewSignup){
        finish();
        startActivity(new Intent(this, RegisterPage.class));
        }
        }
        }
