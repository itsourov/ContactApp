package net.sourov.mycontact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.sourov.mycontact.Model.Users;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsShowEdit extends AppCompatActivity {

    EditText etNameOnUDS,etEmailOnUDS,etNumberOnUDS,etDOBOnUDS;
    TextView nameTextOnUDS;
    String name, number, dateOfBirth, email, imageUrl;
    CircleImageView circleImageView;
    ProgressBar spinnerOnUDS;
    Button saveInfoOnUDS;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details_show_edit);
        mAuth = FirebaseAuth.getInstance();

        etNameOnUDS =findViewById(R.id.etNameOnUDS);
        etEmailOnUDS =findViewById(R.id.etEmailOnUDS);
        etNumberOnUDS =findViewById(R.id.etNumberOnUDS);
        etDOBOnUDS =findViewById(R.id.etDOBOnUDS);


        spinnerOnUDS =findViewById(R.id.spinnerOnUDS);
        saveInfoOnUDS =findViewById(R.id.saveInfoOnUDS);

        circleImageView = findViewById(R.id.circleImageViewOnUDS);
        nameTextOnUDS = findViewById(R.id.nameTextOnUDS);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid()).child("selfInfo");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


                Users users = snapshot.getValue(Users.class);

                dateOfBirth = users.getDateOFBirth();
                email = users.getEmail();
                imageUrl = users.getImageUrl();
                name = users.getName();
                number = users.getNumber();




                etNameOnUDS.setText(name);
                etEmailOnUDS.setText(email);
                etNumberOnUDS.setText(number);
                etDOBOnUDS.setText(dateOfBirth);

                nameTextOnUDS.setText(name);
                Glide.with(UserDetailsShowEdit.this).load(imageUrl).placeholder(R.drawable.user).error(R.drawable.image_not_found).into(circleImageView);


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });











        findViewById(R.id.editUDS1).setOnClickListener(v -> {
            etNameOnUDS.setEnabled(true);
            etNameOnUDS.setSelection(etNameOnUDS.getText().length());
            etNameOnUDS.requestFocus();
            etNameOnUDS.setTextColor(Color.parseColor("#FF03C0"));
            saveInfoOnUDS.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.editUDS2).setOnClickListener(v -> {
            etEmailOnUDS.setEnabled(true);
            etEmailOnUDS.setSelection(etEmailOnUDS.getText().length());
            etEmailOnUDS.requestFocus();
            etEmailOnUDS.setTextColor(Color.parseColor("#FF03C0"));
            saveInfoOnUDS.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.editUDS3).setOnClickListener(v -> {
            etNumberOnUDS.setEnabled(true);
            etNumberOnUDS.setSelection(etNumberOnUDS.getText().length());
            etNumberOnUDS.requestFocus();
            etNumberOnUDS.setTextColor(Color.parseColor("#FF03C0"));
            saveInfoOnUDS.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.editUDS4).setOnClickListener(v -> {
            etDOBOnUDS.setEnabled(true);
            etDOBOnUDS.setSelection(etDOBOnUDS.getText().length());
            etDOBOnUDS.requestFocus();
            etDOBOnUDS.setTextColor(Color.parseColor("#FF03C0"));
            saveInfoOnUDS.setVisibility(View.VISIBLE);
        });

        saveInfoOnUDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerOnUDS.setVisibility(View.VISIBLE);
                updateData();
            }
        });

    }

    private void updateData() {
        name = etNameOnUDS.getText().toString().trim();
        email = etEmailOnUDS.getText().toString().trim();
        number = etNumberOnUDS.getText().toString().trim();
        dateOfBirth = etDOBOnUDS.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("number", number);
        hashMap.put("dateOFBirth", dateOfBirth);



        reference.child("Users").child(mAuth.getCurrentUser().getUid()).child("selfInfo").updateChildren(hashMap)
                .addOnCompleteListener(task -> {

                    spinnerOnUDS.setVisibility(View.GONE);
                    Toast.makeText(UserDetailsShowEdit.this, "data sent to database", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}