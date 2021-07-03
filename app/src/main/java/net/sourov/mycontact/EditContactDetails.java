package net.sourov.mycontact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditContactDetails extends AppCompatActivity {

    CircleImageView imageOnEditFD;
    EditText nameOnAddFD, numberOnAddFD, dateOnAddFD, addressOnAddFD;
    String name, number, dateOfBirth, address, imageUrl, uniqueID;

    FirebaseAuth mAuth;
    private Uri filePath;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_details);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        imageOnEditFD = findViewById(R.id.imageOnEditFD);
        nameOnAddFD = findViewById(R.id.nameOnAddFD);
        numberOnAddFD = findViewById(R.id.numberOnAddFD);
        dateOnAddFD = findViewById(R.id.dateOnAddFD);
        addressOnAddFD = findViewById(R.id.addressOnAddFD);

        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");
        dateOfBirth = getIntent().getStringExtra("dateOfBirth");
        address = getIntent().getStringExtra("address");
        imageUrl = getIntent().getStringExtra("imageurl");
        uniqueID = getIntent().getStringExtra("uniqueid");

        Glide.with(EditContactDetails.this).load(imageUrl).placeholder(R.drawable.user).into(imageOnEditFD);
        nameOnAddFD.setText(name);
        numberOnAddFD.setText(number);
        dateOnAddFD.setText(dateOfBirth);
        addressOnAddFD.setText(address);


        dateOnAddFD.setFocusable(false);
        dateOnAddFD.setClickable(true);
        dateOnAddFD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        EditContactDetails.this,
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
                dateOnAddFD.setText(date);
            }
        };

        findViewById(R.id.openGalleryOnEditFD).setOnClickListener(v -> ImagePicker.with(EditContactDetails.this)
                .cropSquare()
                .galleryOnly()
                .compress(500)
                .start());

        findViewById(R.id.openCameraOnEditFD).setOnClickListener(v -> ImagePicker.with(EditContactDetails.this)
                .cropSquare()
                .cameraOnly()
                .compress(500)
                .start());

        findViewById(R.id.updateButtonOnAddFD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameOnAddFD.getText().toString().trim();
                number = numberOnAddFD.getText().toString().trim();
                dateOfBirth = dateOnAddFD.getText().toString().trim();
                address = addressOnAddFD.getText().toString().trim();
                uploadadContactImage();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);


        filePath = data.getData();
        imageOnEditFD.setImageURI(filePath);


    }
    private void uploadadContactImage() {

        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child("users/" ).child(mAuth.getCurrentUser().getUid()).child("friends").child(uniqueID);


            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Task<Uri> task1 = taskSnapshot.getStorage().getDownloadUrl();
                                    task1.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageUrl = uri.toString();
                                            sendUserData();


                                        }
                                    });

                                    // Image uploaded successfully
                                    // Dismiss dialog

                                    progressDialog.dismiss();
                                    Toast.makeText(EditContactDetails.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();


                                }
                            }).addOnFailureListener(e -> {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(EditContactDetails.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(
                    taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }else {
            sendUserData();
        }
    }

    private void sendUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();



        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("number", number);
        hashMap.put("dateOfBirth", dateOfBirth);
        hashMap.put("address", address);
        if (imageUrl!=null){
            hashMap.put("imageUrl", imageUrl);
        }



        reference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Friends").child(uniqueID).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {


                        Toast.makeText(EditContactDetails.this, "data sent to database", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}