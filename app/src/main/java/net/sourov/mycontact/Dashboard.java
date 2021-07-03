package net.sourov.mycontact;

import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.sourov.mycontact.Model.Users;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView circleImageViewOnDash;
    TextView nameTextOnDash;
    String dateOFBirth, email, name, imageUrl, number;
    Button showDetailsOnDash;

    FirebaseAuth mAuth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();



        //hooks for menu layout
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar_dash);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout_dash);
        navigationView = findViewById(R.id.nav_view_dash);
        navigationView.setItemIconTintList(null);

        //navigation toggle
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_deawer_open, R.string.navigation_deawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);




        circleImageViewOnDash = findViewById(R.id.ImageViewOnDash);
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
                Glide
                        .with(Dashboard.this)
                        .load(imageUrl)
                        .placeholder(R.drawable.user)
                        .into(circleImageViewOnDash);

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




    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home_menu) {

            drawerLayout.closeDrawer(GravityCompat.START);

        } else if (itemId == R.id.add_fd_menu) {
            startActivity(new Intent(Dashboard.this, AddFriends.class));
            finish();
        }
        else if (itemId == R.id.log_out_menu) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Dashboard.this,LoginActivity.class));
            finish();
        }
        return true;
    }



}