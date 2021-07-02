package net.sourov.mycontact;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.sourov.mycontact.Model.Users;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Dashboard extends AppCompatActivity {

    ImageView circleImageViewOnDash;
    TextView nameTextOnDash;
    String dateOFBirth, email, name, imageUrl, number;
    Button showDetailsOnDash;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();


        circleImageViewOnDash = findViewById(R.id.circleImageViewOnDash);
        showDetailsOnDash = findViewById(R.id.showDetailsOnDash);
        nameTextOnDash = findViewById(R.id.nameTextOnDash);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("selfInfo");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


                Users users = snapshot.getValue(Users.class);

                assert users != null;
                dateOFBirth = users.getDateOFBirth();
                email = users.getEmail();
                imageUrl = users.getImageUrl();
                name = users.getName();
                number = users.getNumber();




                nameTextOnDash.setText(name);
                Glide.with(Dashboard.this).load(imageUrl).placeholder(R.drawable.user).error(R.drawable.image_not_found).into(circleImageViewOnDash);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        showDetailsOnDash.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, UserDetailsShowEdit.class)));

        findViewById(R.id.editUDS1).setOnClickListener(v -> startActivity(new Intent(Dashboard.this, UserDetailsShowEdit.class)));

        findViewById(R.id.AddFDOnDash).setOnClickListener(v -> startActivity(new Intent(Dashboard.this, AddFriends.class)));

        findViewById(R.id.updateFriendsOnDash).setOnClickListener(v -> startActivity(new Intent(Dashboard.this, FriendList.class)));

        findViewById(R.id.removeFdOnDash).setOnClickListener(v -> startActivity(new Intent(Dashboard.this, FriendList.class)));

        findViewById(R.id.friendListOnDash).setOnClickListener(v -> startActivity(new Intent(Dashboard.this, FriendList.class)));


    }


}