package com.palit.inote;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button mSignIn;
    private TextView signUp;
    private EditText username,password;
    private String mUser,mPass;
    private ProgressBar mprogressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_login);

        // making notification bar transparent
        changeStatusBarColor();

        //Initiate the Views
        signUp = findViewById(R.id.signup_button);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        mprogressBar = findViewById(R.id.progressbar);
        mSignIn = findViewById(R.id.signin_button);

        //Get FireBaseAuth Instance
        mAuth = FirebaseAuth.getInstance();

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUser = username.getText().toString().trim();
                mPass = password.getText().toString().trim();

                //Validate the input and proceed to sign in
                if (inputValidate(mUser, mPass)) {
                    mprogressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(mUser, mPass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                                    } else {

                                        mprogressBar.setVisibility(View.GONE);
                                        // If sign in fails, display a message to the user.
                                        Log.d("Firebase",task.getException().getLocalizedMessage());
                                        Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start Activity Sign Up
                startActivity(new Intent(getApplicationContext(),SignUp.class));
            }
        });
    }

    private boolean inputValidate(String user, String pass){
        if(user.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter Email", Toast.LENGTH_SHORT).show();
            username.requestFocus();
            return false;
        }
        else if(pass.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter Password", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
        }
        else return true;
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

}