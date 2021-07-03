package net.sourov.mycontact.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.sourov.mycontact.FriendsProfile;
import net.sourov.mycontact.Model.Contacts;
import net.sourov.mycontact.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeleteFriendAdapter extends RecyclerView.Adapter<DeleteFriendAdapter.ContactHolder> {

    Context context;
    List<Contacts> contactsList;

    public DeleteFriendAdapter(Context context, List<Contacts> contactsList) {
        this.context = context;
        this.contactsList = contactsList;
    }


    @NonNull
    @NotNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_delet_item, parent,false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DeleteFriendAdapter.ContactHolder holder, int position) {


        Contacts contacts = contactsList.get(position);
        holder.contactName.setText(contacts.getName());
        holder.addressOnContactItem.setText(contacts.getAddress());
        Glide.with(context).load(contacts.getImageUrl()).placeholder(R.drawable.user).into(holder.contactImg);
        holder.DeleteContactItem.setOnClickListener(v -> {

            FirebaseAuth mAuth;
            DatabaseReference reference;
            mAuth = FirebaseAuth.getInstance();
            reference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(mAuth.getCurrentUser().getUid()).child("Friends").child(contacts.getUniqueID());

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Are you sure you want to delete " + contacts.getName() + " from your friend list");
            builder.setPositiveButton("Yes", (dialog, which) -> reference.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }));
            builder.setNegativeButton("No", (dialog, which) -> {

            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });
        holder.myFriendViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goTOFriendProfile = new Intent(context, FriendsProfile.class);
                goTOFriendProfile.putExtra("name", contacts.getName());
                goTOFriendProfile.putExtra("number", contacts.getNumber());
                goTOFriendProfile.putExtra("dateOfBirth", contacts.getDateOfBirth());
                goTOFriendProfile.putExtra("address", contacts.getAddress());
                goTOFriendProfile.putExtra("imageurl", contacts.getImageUrl());
                goTOFriendProfile.putExtra("uniqueid", contacts.getUniqueID());
                context.startActivity(goTOFriendProfile);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    class ContactHolder extends RecyclerView.ViewHolder{

        CircleImageView contactImg;
        TextView contactName,addressOnContactItem;
        ImageView DeleteContactItem;
        ConstraintLayout myFriendViewItem;

        public ContactHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.ContactNameOnContactDeleteItem);
            contactImg = itemView.findViewById(R.id.imageViewOnContactDeleteItem);
            DeleteContactItem = itemView.findViewById(R.id.deleteOnContactDeleteItem);
            myFriendViewItem = itemView.findViewById(R.id.myFriendDeleteItem);
            addressOnContactItem = itemView.findViewById(R.id.addressOnContactDeleteItem);
        }
    }
}
