package com.acpc.mobilepatienttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PatientForm extends AppCompatActivity  {
    private EditText pname, psurname, pid, pcell, pNationality, pAddress, pemname, pemcell, Allergies;
    private RadioGroup radioRace;  //// race
    private RadioGroup radioMarital;  /// marital status
    private CheckBox chkFemale, chkMale;
    private CheckBox chkHIV;
    private CheckBox chkTB;
    private CheckBox chKDiabetes;
    private CheckBox chkHyp;
    private CheckBox chkMedaid;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FirebaseFirestore database= FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_form);
        btnSubmit= findViewById(R.id.btnSubmit);

        radioRace= findViewById(R.id.radioGroup);
        radioMarital= findViewById(R.id.radioGroup2);

        pname= findViewById(R.id.pname);
        psurname= findViewById(R.id.psurname);
        pid= findViewById(R.id.pid);
        pcell= findViewById(R.id.pCell);
        pNationality= findViewById(R.id.pNationality);
        pAddress= findViewById(R.id.pAddress);
        pemname= findViewById(R.id.pemname);
        pemcell= findViewById(R.id.pemcell);
        Allergies= findViewById(R.id.Allergies);


        btnSubmit.setOnClickListener(new View.OnClickListener() { //when you click the submit button it adds to the patients data in the database
            @Override
            public void onClick(View v) {
                final String p_name= pname.getText().toString();
                final String p_surname= psurname.getText().toString();
                final String p_id= pid.getText().toString();
                final String p_cell= pcell.getText().toString();
                final String p_Nationality= pNationality.getText().toString();
                final String p_Address= pAddress.getText().toString();
                final String p_emname=pemname.getText().toString();
                final String p_emcell= pemcell.getText().toString();
                final String allergies= Allergies.getText().toString();
                final String m_status= getMarriage_Status(v);
                final String p_race= getRace(v);
                final ArrayList<String> cissues= ailments(v);
                final String gender= checkGender(v);
                final String medaid= checkAid(v);

                Patient patient = new Patient(p_name, p_surname, p_id, p_cell, p_Nationality,gender, p_Address, p_emname, p_emcell, p_race, m_status, cissues, medaid,  allergies);

                database.collection("patient-data") // data gets added to a collection called patient-data
                        .add(patient)
                        // Add a success listener so we can be notified if the operation was successfuly.

                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // If we are here, the app successfully connected to Firestore and added a new entry
                                Toast.makeText(PatientForm.this,"Data successfully added", Toast.LENGTH_LONG).show();
                            }
                        })
                        // Add a failure listener so we can be notified if something does wrong
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // If we are here, the entry could not be added for some reason (e.g no internet connection)
                                Toast.makeText(PatientForm.this,"Data was unable added", Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });

    }


    public String getMarriage_Status (View v){            ////function to help convert radiogroup selections to string, this allows us to store it as a string in the DB

        int radioID = radioMarital.getCheckedRadioButtonId();

        RadioButton singleButton = (RadioButton) findViewById(radioID);
        String out = singleButton.getText().toString();
        return out;

    }


    public String getRace (View v){    ////function to help convert radiogroup selections to string, this allows us to store it as a string in the DB


        int radioID = radioRace.getCheckedRadioButtonId();

        RadioButton singleButton = (RadioButton) findViewById(radioID);
        String out = singleButton.getText().toString();
        return out;

    }

    public ArrayList<String> ailments(View v){         ////function to collect options selected from the check box, stores in a string arrayList, this is stored in the DB
        ArrayList<String> sickness= new ArrayList<String>();
        chkHIV= findViewById(R.id.chkHIV);
        chkTB= findViewById(R.id.chkTB);
        chKDiabetes= findViewById(R.id.chkDiabetes);
        chkHyp= findViewById(R.id.chkHyp);
        if(chkHIV.isChecked()){
            sickness.add("HIV/AIDS");
        }
        if(chkTB.isChecked()){
            sickness.add("TB");
        }

        if(chKDiabetes.isChecked()){
            sickness.add("Diabetes");
        }

        if(chkHyp.isChecked()){
            sickness.add("Hypertension");
        }

        return sickness;


    }

    public String checkGender(View v){      ///// function that checks check box, stores option as a string, this can be stored in the DB
        String gen="";
        chkFemale= findViewById(R.id.chkFemale);
        chkMale= findViewById(R.id.chkMale);
        if(chkFemale.isChecked()){
            gen="Female";
        }
        else{
            gen="Male";
        }
        return gen;
    }

    public String checkAid(View v){    ///// function that checks check box, stores option as a string, this can be stored in the DB
        String aid= "";
        chkMedaid= findViewById(R.id.chkMedaid);

        if(chkMedaid.isChecked()){
            aid="Yes";
        }
        else{
            aid="No";
        }
        return aid;

    }




}