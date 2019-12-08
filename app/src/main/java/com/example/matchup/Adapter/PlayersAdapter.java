package com.example.matchup.Adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.matchup.MessageActivity;
import com.example.matchup.Model.Chat;
import com.example.matchup.Model.User;
import com.example.matchup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//Displays other registered players
public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.ViewHolder> {

    private Context mContext;
    private List<DataSnapshot> mUsers;
    private boolean isChat;
    private double lat, lon;
    DecimalFormat dm = new DecimalFormat("#.#");

    String lastMessage;

    public PlayersAdapter(Context mContext, ArrayList<DataSnapshot> users, boolean isChat, double lat, double lon){
        this.mUsers = users;
        this.mContext = mContext;
        this.isChat = isChat;
        this.lat = lat;
        this.lon = lon;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.player_card, parent, false);
        return new PlayersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final DataSnapshot user = mUsers.get(holder.getAdapterPosition());

        final String id = (String)user.child("id").getValue();
        final String username = (String)user.child("username").getValue();
        final String description = (String)user.child("description").getValue();
        final String imageURL = (String)user.child("imageURL").getValue();
        final String status = (String)user.child("status").getValue();

        double userLat = 0.0;
        double userLon = 0.0;

        Object objLat = user.child("location").child("lat").getValue();
        Object objLon = user.child("location").child("lon").getValue();
        if(objLat!=null&&objLon!=null){
            userLat = (double)objLat;
            userLon = (double)objLon;
        }

        if(userLat == 0.0 || userLon == 0.0){
            holder.distance.setText("Unknown distance");
        } else {
            Location userLocation = new Location("a");
            userLocation.setLatitude(userLat);
            userLocation.setLongitude(userLon);
            Location currLocation = new Location("b");
            currLocation.setLatitude(lat);
            currLocation.setLongitude(lon);
            double dist = currLocation.distanceTo(userLocation);
            holder.distance.setText("Distance: " + dm.format(dist/1000d) + " km");
        }

        holder.username.setText(username);

        if(description==null)
            holder.description.setText("No description");
        else
            holder.description.setText(description);


        if(imageURL == null || imageURL.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageURL).into(holder.profile_image);
        }

        if(status == null || status.equals("offline")){
            holder.img_online.setVisibility(View.GONE);
            holder.img_offline.setVisibility(View.VISIBLE);
        } else {
            holder.img_online.setVisibility(View.VISIBLE);
            holder.img_offline.setVisibility(View.GONE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", id);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username, description, distance;
        public ImageView profile_image;
        private ImageView img_online;
        private ImageView img_offline;
//        private TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            distance = itemView.findViewById(R.id.distance);
            username = itemView.findViewById(R.id.username);
            description = itemView.findViewById(R.id.player_description);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_online = itemView.findViewById(R.id.img_online);
            img_offline = itemView.findViewById(R.id.img_offline);
//            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

//    //Check for the last message exchanged
//    private void lastMessage(final String userid, final TextView last_msg){
//        lastMessage = "default";
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//
//                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
//                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
//
//                        lastMessage = chat.getMessage();
//
//                    }
//                }
//
//                switch (lastMessage){
//                    case "default":
//                        last_msg.setText("No Message");
//                        break;
//                    default:
//                        last_msg.setText(lastMessage);
//                        break;
//                }
//
//                lastMessage = "default";
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
