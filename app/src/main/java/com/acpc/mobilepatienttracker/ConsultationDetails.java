package com.acpc.mobilepatienttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ConsultationDetails extends AppCompatActivity {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    private TextView txtPFirstName;
    private TextView txtPSurname;
    private TextView txtPatientID;
    private TextView txtPcell;
    private TextView txtDFirstName;
    private TextView txtDSurname;
    private TextView txtPracticeID;
    private TextView txtCaseInfo;
    private TextView txtSymptoms;
    private TextView txtDiagnosis;
    private TextView txtDate;
    private TextView txtConsultationDetailsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_details);

        final LoadingDialog loadingDialog = new LoadingDialog(ConsultationDetails.this);

        loadingDialog.startLoading();

        txtConsultationDetailsLabel = findViewById(R.id.txtConsultationDetailsLabel);
        txtConsultationDetailsLabel.setPaintFlags(txtConsultationDetailsLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);// to underline text
        txtPFirstName = findViewById(R.id.txtPFirstname);
        txtPSurname = findViewById(R.id.txtPSurname);
        txtPatientID = findViewById(R.id.txtID);
        txtPcell = findViewById(R.id.txtCell);
        txtDFirstName = findViewById(R.id.txtDFirstName);
        txtDSurname = findViewById(R.id.txtDSurname);
        txtPracticeID = findViewById(R.id.txtDPracticeID);
        txtCaseInfo = findViewById(R.id.txtCaseInfo);
        txtSymptoms = findViewById(R.id.txtSymptoms);
        txtDiagnosis = findViewById(R.id.txtDiagnosis);
        txtDate = findViewById(R.id.txtDate);

        Intent intent = getIntent();

        if(intent.getExtras() != null)
        {
            String patientID = intent.getExtras().getString("PATIENT_ID");
            String doctorID = intent.getExtras().getString("DOCTOR_ID");
            String dateTime = intent.getExtras().getString("DATETIME");

            Log.d("CONSULTATION-DETAILS", patientID);
            Log.d("CONSULTATION-DETAILS", doctorID);
            Log.d("CONSULTATION-DETAILS", dateTime);

            GetPastConsult(patientID, doctorID, dateTime,loadingDialog);

        }
        else
        {
            Log.w("CONSULTATION-DETAILS", "Bundle was null");
        }

        //  loadingDialog.dismiss();

    }

    public void GetPastConsult(final String patientID, final String doctorID, final String dateTime, final LoadingDialog loadingDialog)
    {
        database.collection("doctor-data").whereEqualTo("p_no", doctorID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    final Doctor doctor = task.getResult().toObjects(Doctor.class).get(0);

                    database.collection("patient-data").whereEqualTo("idno", patientID)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                final Patient patient = task.getResult().toObjects(Patient.class).get(0);

                                database.collection("consultation-data").whereEqualTo("pdoctorID", doctorID).whereEqualTo("ppatientID", patientID).whereEqualTo("pdate", dateTime)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful())
                                        {
                                            Consultation consultation = task.getResult().toObjects(Consultation.class).get(0);

                                            txtPFirstName.setText(patient.fname);
                                            txtPSurname.setText(patient.fsurname);
                                            txtPatientID.setText(patient.idno);
                                            txtPcell.setText(patient.cellno);
                                            txtDFirstName.setText(doctor.fname);
                                            txtDSurname.setText(doctor.lname);
                                            txtPracticeID.setText(doctor.p_no);
                                            txtCaseInfo.setText(consultation.pcase);
                                            txtSymptoms.setText(consultation.psymptoms);
                                            txtDiagnosis.setText(consultation.pdiagnosis);
                                            txtDate.setText(consultation.pdate);
                                            loadingDialog.dismiss();

                                        }

                                        else
                                        {
                                            Log.w("CONSULTATION-DETAILS", "Could not get past consults: ", task.getException());
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Log.w("CONSULTATION-DETAILS", "Could not get patient info: ", task.getException());
                            }
                        }
                    });
                }
                else
                {
                    Log.w("CONSULTATION-DETAILS", "Could not get doctor info: ", task.getException());
                }
            }
        });

    }
}