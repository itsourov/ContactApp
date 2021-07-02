package net.sourov.mycontact;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    EditText inputNameSU,inputEmailSU,inputPasswordSU,inputNumberSU,inputDateSU;
    String name,email,password,number,dateOFBirth;
    ProgressBar progressBarOnSU;

    private FirebaseAuth mAuth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        inputNameSU = findViewById(R.id.inputNameSU);
        inputEmailSU = findViewById(R.id.inputEmailSU);
        inputPasswordSU = findViewById(R.id.inputPasswordSU);
        inputNumberSU = findViewById(R.id.inputNumberSU);
        inputDateSU = findViewById(R.id.inputDateSU);
        progressBarOnSU = findViewById(R.id.progressBarOnSU);

        inputDateSU.setFocusable(false);
        inputDateSU.setClickable(true);
        inputDateSU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SignUpActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("TAG", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                inputDateSU.setText(date);
            }
        };


        findViewById(R.id.btnSignUpOnSU).setOnClickListener(v -> {

            name = inputNameSU.getText().toString().trim();
            email = inputEmailSU.getText().toString().trim();
            password = inputPasswordSU.getText().toString().trim();
            number = inputNumberSU.getText().toString().trim();
            dateOFBirth = inputDateSU.getText().toString().trim();

            if (name.isEmpty()){
                inputNameSU.setError("Name can't be empty");
                inputNameSU.requestFocus();
            } else if (email.isEmpty()) {
                inputEmailSU.setError("Email cant be Empty");
                inputEmailSU.requestFocus();
            }else if (password.length() < 6){
                inputPasswordSU.setError("password must be 6 character long");
            }else {
                progressBarOnSU.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        sendData();
                    } else {
                        progressBarOnSU.setVisibility(View.GONE);
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            inputEmailSU.setError("The email already exists");
                            inputEmailSU.requestFocus();
                        }   }
                });
            }




        });


        findViewById(R.id.gotoLoginSU).setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            finish();
        });
    }

    private void sendData() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("number", number);
        hashMap.put("dateOFBirth", dateOFBirth);
        hashMap.put("userID", mAuth.getCurrentUser().getUid());


        reference.child("Users").child(mAuth.getCurrentUser().getUid()).child("selfInfo").setValue(hashMap)
                .addOnCompleteListener(task -> {


                    Toast.makeText(SignUpActivity.this, "data sent to database", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, UploadProfilePic.class));
                    finish();
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        SaveState saveState;
        saveState = new SaveState(this);

        if (saveState.getState()) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}