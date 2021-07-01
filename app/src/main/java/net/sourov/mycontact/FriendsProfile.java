package net.sourov.mycontact;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsProfile extends AppCompatActivity {

    CircleImageView circleImageViewOnFP;
    TextView nameTextOnFP;
    EditText etNameOnFP, etNumberOnFP, etAddressOnFP, etDateOnFP;
    String name, number, dateOfBirth, address, imageUrl, uniqueid;

    ProgressBar spinnerOnFP;
    Button saveInfoOnFP;
    FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_profile);
        mAuth = FirebaseAuth.getInstance();


        circleImageViewOnFP = findViewById(R.id.circleImageViewOnFP);
        nameTextOnFP = findViewById(R.id.nameTextOnFP);
        etNameOnFP = findViewById(R.id.etNameOnFP);
        etNumberOnFP = findViewById(R.id.etNumberOnFP);
        etAddressOnFP = findViewById(R.id.etAddressOnFP);
        etDateOnFP = findViewById(R.id.etDateOnFP);

        spinnerOnFP = findViewById(R.id.spinnerOnFP);
        saveInfoOnFP = findViewById(R.id.saveInfoOnFP);

        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");
        dateOfBirth = getIntent().getStringExtra("dateOfBirth");
        address = getIntent().getStringExtra("address");
        imageUrl = getIntent().getStringExtra("imageurl");
        uniqueid = getIntent().getStringExtra("uniqueid");

        Glide.with(FriendsProfile.this).load(getIntent().getStringExtra("imageurl")).into(circleImageViewOnFP);
        nameTextOnFP.setText(name);
        etNameOnFP.setText(name);
        etNumberOnFP.setText(number);
        etAddressOnFP.setText(address);
        etDateOnFP.setText(dateOfBirth);

        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid()).child("Friends").child(uniqueid);


        findViewById(R.id.deleteOnFP).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(FriendsProfile.this);
            builder.setTitle("Are you sure you want to delete " + name + " from your friend list");
            builder.setPositiveButton("Yes", (dialog, which) -> reference.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(FriendsProfile.this, "deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FriendsProfile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }));
            builder.setNegativeButton("No", (dialog, which) -> {

            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        findViewById(R.id.FPedit1).setOnClickListener(v -> {
            etNameOnFP.setEnabled(true);
            etNameOnFP.setSelection(etNameOnFP.getText().length());
            etNameOnFP.requestFocus();
            etNameOnFP.setTextColor(Color.parseColor("#FF03C0"));
            saveInfoOnFP.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.FPedit2).setOnClickListener(v -> {
            etNumberOnFP.setEnabled(true);
            etNumberOnFP.setSelection(etNumberOnFP.getText().length());
            etNumberOnFP.requestFocus();
            etNumberOnFP.setTextColor(Color.parseColor("#FF03C0"));
            saveInfoOnFP.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.FPedit3).setOnClickListener(v -> {
            etAddressOnFP.setEnabled(true);
            etAddressOnFP.setSelection(etAddressOnFP.getText().length());
            etAddressOnFP.requestFocus();
            etAddressOnFP.setTextColor(Color.parseColor("#FF03C0"));
            saveInfoOnFP.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.FPedit4).setOnClickListener(v -> {
            etDateOnFP.setEnabled(true);
            etDateOnFP.setSelection(etDateOnFP.getText().length());
            etDateOnFP.requestFocus();
            etDateOnFP.setTextColor(Color.parseColor("#FF03C0"));
            saveInfoOnFP.setVisibility(View.VISIBLE);
        });
        saveInfoOnFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerOnFP.setVisibility(View.VISIBLE);
                name=etNameOnFP.getText().toString().trim();
                number=etNumberOnFP.getText().toString().trim();
                address=etAddressOnFP.getText().toString().trim();
                dateOfBirth=etDateOnFP.getText().toString().trim();


                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", name);
                hashMap.put("number", number);
                hashMap.put("dateOfBirth", dateOfBirth);
                hashMap.put("address", address);

                reference.updateChildren(hashMap).addOnCompleteListener(task -> {

                    spinnerOnFP.setVisibility(View.GONE);
                    Toast.makeText(FriendsProfile.this, "data sent to database", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


}