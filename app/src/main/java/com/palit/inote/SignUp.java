package com.palit.inote;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private Spinner classSpinner;
    private EditText eEmail,eName,ePassword,eConfPassword,ePhone;
    private String mEmail,mPassword,mName,mConfPassword,mClass,mPhone;
    private ProgressBar mProgressBar;

    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mProfileDatabaseReference;
    private String muserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseApp.initializeApp(this);

        classSpinner = findViewById(R.id.class_spinner);
        ArrayAdapter<CharSequence> classes = ArrayAdapter.createFromResource(this,R.array.class_adapter,
                android.R.layout.simple_spinner_dropdown_item);
        classes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(classes);


        eName = findViewById(R.id.name);
        eEmail = findViewById(R.id.email);
        ePhone = findViewById(R.id.phone);
        ePassword = findViewById(R.id.password);
        eConfPassword = findViewById(R.id.conf_password);
        Button mSignUp = findViewById(R.id.signup);
        mProgressBar = findViewById(R.id.progress);



        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mProfileDatabaseReference = mFirebaseDatabse.getReference();

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();

            }
        });



    }

    private void signUp(){

        //Get the form field values
        mName = eName.getText().toString().trim();
        mClass = classSpinner.getSelectedItem().toString();
        mPhone = ePhone.getText().toString().trim();
        mEmail = eEmail.getText().toString().trim();
        mPassword = ePassword.getText().toString().trim();
        mConfPassword = eConfPassword.getText().toString().trim();

        if(mEmail.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter Email", Toast.LENGTH_SHORT).show();
            eEmail.requestFocus();
        }
        else if(mPassword.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter Password", Toast.LENGTH_SHORT).show();
            ePassword.requestFocus();
        }
        else if (!mConfPassword.equals(mPassword)){
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            eConfPassword.requestFocus();
        } else if (mClass.equals("")){
            Toast.makeText(getApplicationContext(), "Please select Class", Toast.LENGTH_SHORT).show();
            classSpinner.requestFocus();
        }  else if (mPhone.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter phone number", Toast.LENGTH_SHORT).show();
            ePhone.requestFocus();
        }  else if (mName.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter your Name", Toast.LENGTH_SHORT).show();
            eName.requestFocus();
        }
        else{
            mProgressBar.setVisibility(View.VISIBLE);

            //Get the auth and create the user
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        mProgressBar.setVisibility(View.GONE);
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        FirebaseUser user = auth.getCurrentUser();

                        if(user != null)
                            //Get newly created user's id
                            muserId = user.getUid();

                        //Get the user details and push it to the database
                        UserDetails details = new UserDetails(mName,mPhone,mClass,mEmail,null);
                        mProfileDatabaseReference.child("Users/"+muserId).setValue(details);
                        startActivity(new Intent(getApplicationContext(),HomeScreen.class));

                    }
                    else{
                        Log.d("FireBase Login",task.getException().getLocalizedMessage());
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}
