package net.sourov.mycontact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.sourov.mycontact.Model.Contacts;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {

    Context context;
    List<Contacts> contactsList;

    public ContactAdapter(Context context, List<Contacts> contactsList) {
        this.context = context;
        this.contactsList = contactsList;
    }


    @NonNull
    @NotNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent,false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ContactAdapter.ContactHolder holder, int position) {


        Contacts contacts = contactsList.get(position);
        holder.contactName.setText(contacts.getName());
        holder.addressOnContactItem.setText(contacts.getAddress());
        Glide.with(context).load(contacts.getImageUrl()).into(holder.contactImg);
        holder.callOnContactItem.setOnClickListener(v -> {

            String phone = contacts.getNumber();
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                    "tel", phone, null));
            context.startActivity(phoneIntent);

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
        ImageView callOnContactItem;
        ConstraintLayout myFriendViewItem;

        public ContactHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.contactNameOnContactItem);
            contactImg = itemView.findViewById(R.id.imageViewOnContactItem);
            callOnContactItem = itemView.findViewById(R.id.callOnContactItem);
            myFriendViewItem = itemView.findViewById(R.id.myFriendViewItem);
            addressOnContactItem = itemView.findViewById(R.id.addressOnContactItem);
        }
    }
}
