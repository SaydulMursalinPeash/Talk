package com.example.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private EditText userName,email,password;
    private TextView have_account;
    private Button submit;
    private boolean isu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        userName=(EditText) findViewById(R.id.editTextTextPersonName);
        email=(EditText) findViewById(R.id.editTextTextEmailAddress);
        password=(EditText) findViewById(R.id.editTextTextPassword);
        have_account=(TextView) findViewById(R.id.textView);
        submit=(Button) findViewById(R.id.button);
        isu=true;
        have_account.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if(isu){
                    isu=false;
                    submit.setText("Log In");
                    have_account.setText("Haven't an account? Sign up.");
                    userName.setVisibility(View.GONE);
                }
                else{
                    isu=true;
                    submit.setText("Sign Up");
                    have_account.setText("Already have an account? Log in.");
                    userName.setVisibility(View.VISIBLE);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty()||password.getText().toString().isEmpty()){
                    if(isu&&userName.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(),"Please input valid name..",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(isu){
                    handle_sign_up();
                }else{
                    handle_log_in();
                }
            }
        });
    }

    private void handle_log_in(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));
                    Toast.makeText(getApplicationContext(),"Successfully logged in.",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void handle_sign_up(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("user/"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(new User(userName.getText().toString(),email.getText().toString(),""));
                    Toast.makeText(getApplicationContext(),"Create account successfull..",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
}