package net.sourov.mycontact;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadProfilePic extends AppCompatActivity {

    private Uri filePath;

    CircleImageView profileImageOnUPP;
    Button submitOnUpp;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        profileImageOnUPP = findViewById(R.id.profileImageOnUPP);
        submitOnUpp = findViewById(R.id.submitOnUpp);
        submitOnUpp.setVisibility(View.INVISIBLE);
        findViewById(R.id.choiceImageOnUPP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImg();
            }
        });
        findViewById(R.id.takeImageOnUPP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImg();
            }
        });


        submitOnUpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImg();
            }
        });

    }


    private void takeImg() {
        ImagePicker.with(this)
                .cameraOnly()
                .crop(1f, 1f)
                .compress(500)
                .start();
    }

    private void pickImg() {
        ImagePicker.with(this)
                .galleryOnly()
                .crop(1f, 1f)
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
        profileImageOnUPP.setImageURI(filePath);
        submitOnUpp.setVisibility(View.VISIBLE);


    }
    private void uploadImg() {

        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child("users/" + mAuth.getCurrentUser().getUid()).child("profilePic/").child(UUID.randomUUID().toString());

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

                                            String PATH = getExternalFilesDir(Environment.DIRECTORY_DCIM).toString() + "/userphoto/" + "profile.jpg";
                                            File file = new File(PATH);
                                            if (file.exists()){
                                                file.delete();
                                            }

                                            String imageUrl = uri.toString();
                                            uploadData(imageUrl);
                                            syncData(imageUrl);



                                        }
                                    });

                                    // Image uploaded successfully
                                    // Dismiss dialog

                                    progressDialog.dismiss();
                                    Toast.makeText(UploadProfilePic.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(UploadProfilePic.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(
                    taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }

    private void uploadData(String imageUrl) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("imageUrl", imageUrl);



        reference.child("Users").child(mAuth.getCurrentUser().getUid()).child("selfInfo").updateChildren(hashMap)
                .addOnCompleteListener(task -> {


                    Toast.makeText(UploadProfilePic.this, "data sent to database", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UploadProfilePic.this, UploadProfilePic.class));
                    finish();
                });
    }

    private void syncData(String imageUrl) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
        request.setDescription("Downloading file. please wait...");
        String cookie = CookieManager.getInstance().getCookie(imageUrl);
        request.addRequestHeader("cookie", cookie);

        request.setDestinationInExternalFilesDir(UploadProfilePic.this, Environment.DIRECTORY_DCIM + "/userphoto", "profile.jpg");

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        //set BroadcastReceiver
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                startActivity(new Intent(UploadProfilePic.this, FriendList.class));
                finish();


            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

}