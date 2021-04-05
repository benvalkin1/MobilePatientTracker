package com.acpc.mobilepatienttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class PRegistration extends AppCompatActivity implements View.OnClickListener{
    private EditText inname, inemail, inpassword;
    private Button reg;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_registration);
        mAuth = FirebaseAuth.getInstance();
        inname= findViewById(R.id.inname);
        inemail= findViewById(R.id.inemail);
        inpassword= findViewById(R.id.inpassword);
        reg = findViewById(R.id.reg);
        reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reg:
                registerUser();
                break;
        }
    }
    private void registerUser(){
        //taking in input for email,name,password and converting to string for database to understand
        final String name= inname.getText().toString().trim();
        final String email= inemail.getText().toString().trim();
        String password= inpassword.getText().toString().trim();

        //these if statements ensure that none of the inputs fields(name,email,password) is left empty when registering
        if(name.isEmpty()){
            inname.setError("Name is Required");
            inname.requestFocus(); //this allows the error message to be viewed for a long enough time to read
            return;
        }

        if(email.isEmpty()){
            inemail.setError("Email is Required");
            inemail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){ //ensures that authentic email is entered (with @ sign etc)
            inemail.setError("Provide Valid Email");
            inemail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            inpassword.setError("Password is Required");
            inpassword.requestFocus();
            return;
        }

        if(password.length()<6){  //firebase requires password with min 6 characters
            inpassword.setError("Min Length = 6");
            inpassword.requestFocus();
            return;
        }

        //code below allows us to add users with an email and password to the realtime database (not the cloud firestore database that we can view the more in depth data from )
        //this ensures that the log in credentials are stored safely and we can not view the sensitive password information that the users input
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user= new User(name,email);
                            FirebaseDatabase.getInstance().getReference("Users")  //this adds all new users to a collection in the database called "Users"

                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())  //inside brackets will return ID for registered user

                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() { //To check if data has been inserted into database
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(PRegistration.this, "User Has Successfully Been Registered", Toast.LENGTH_LONG).show(); //shows message to tell user registration has been successful


                                        ///////Redirect to login now !
                                    }
                                    else{
                                        Toast.makeText(PRegistration.this, "Registration Unsuccessful, Try Again", Toast.LENGTH_LONG).show(); //unsuccessful registration message

                                    }
                                }
                            });

                        }
                        else{
                            Toast.makeText(PRegistration.this, "Registration Unsuccessful, Try Again", Toast.LENGTH_LONG).show();

                        }//else
                    }
                });

    }
}