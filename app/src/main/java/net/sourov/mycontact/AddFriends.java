package net.sourov.mycontact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriends extends AppCompatActivity {

    String imageUrl;
    FirebaseAuth mAuth;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    EditText etNameOnAddFD,etNumberOnAddFD,etDOBOnAddFD,etAddressOnAddFD;
    String name,number,dateOfBirth,address;
    CircleImageView imageOnAddFD;



    String uniqueID;


    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        mAuth = FirebaseAuth.getInstance();

        uniqueID=  UUID.randomUUID().toString();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //hooks for menu layout



        etNameOnAddFD = findViewById(R.id.etUsernameOnAddFD);
        etNumberOnAddFD = findViewById(R.id.etNumberOnAddFD);
        etDOBOnAddFD = findViewById(R.id.etDOBOnAddFD);
        etAddressOnAddFD = findViewById(R.id.etAddressOnAddFD);
        imageOnAddFD = findViewById(R.id.imageOnAddFD);

        etDOBOnAddFD.setFocusable(false);
        etDOBOnAddFD.setClickable(true);
        etDOBOnAddFD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddFriends.this,
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
                etDOBOnAddFD.setText(date);
            }
        };
        findViewById(R.id.openGalleryOnAddFd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        findViewById(R.id.openCameraOnAddFD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });


        findViewById(R.id.AddFDOnAddFD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etNameOnAddFD.getText().toString().trim();
                number = etNumberOnAddFD.getText().toString().trim();
                dateOfBirth = etDOBOnAddFD.getText().toString().trim();
                address = etAddressOnAddFD.getText().toString().trim();

                uploadImage();

            }
        });
    }

    private void uploadImage() {

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

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
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
                                    Toast.makeText(AddFriends.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();


                                }
                            }).addOnFailureListener(e -> {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(AddFriends.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(
                    taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }


    private void openGallery() {
        ImagePicker.with(this)
                .cropSquare()
                .galleryOnly()
                .compress(500)
                .start();
    }
    private void openCamera() {
        ImagePicker.with(this)
                .cropSquare()
                .cameraOnly()
                .compress(500)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);


        filePath = data.getData();
        imageOnAddFD.setImageURI(filePath);


    }

    private void sendUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("number", number);
        hashMap.put("dateOfBirth", dateOfBirth);
        hashMap.put("address", address);
        hashMap.put("imageUrl", imageUrl);
        hashMap.put("uniqueID", uniqueID);


        reference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Friends").child(uniqueID).setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {


                        Toast.makeText(AddFriends.this, "data sent to database", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddFriends.this, FriendList.class));
                        finish();
                    }
                });
    }

}