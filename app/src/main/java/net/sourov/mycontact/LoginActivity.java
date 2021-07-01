package net.sourov.mycontact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    EditText inputEmailLI, inputPassLI;
    String email, pass;
    ProgressBar progressBarOnLI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        inputEmailLI = findViewById(R.id.inputEmailLI);
        inputPassLI = findViewById(R.id.inputPassLI);
        progressBarOnLI = findViewById(R.id.progressBarOnLI);


        findViewById(R.id.btnLogInOnLI).setOnClickListener(v -> {
            email = inputEmailLI.getText().toString().trim();
            pass = inputPassLI.getText().toString().trim();
            if (email.isEmpty()) {
                inputEmailLI.setError("email is empty");
                inputEmailLI.requestFocus();
            } else if (pass.isEmpty()) {
                inputPassLI.setError("password is empty");
                inputPassLI.requestFocus();
            }else {
                progressBarOnLI.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            progressBarOnLI.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                finish();
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    inputPassLI.setError("Password didn't match");
                                    inputPassLI.requestFocus();
                                } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    inputEmailLI.setError("Email wasn't found on server");
                                    inputEmailLI.requestFocus();
                                } else {
                                    Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
            }


        });


        findViewById(R.id.gotoSignUpLI).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, Dashboard.class));
            finish();
        }
    }
}