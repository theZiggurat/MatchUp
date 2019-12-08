package com.example.matchup.Fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.matchup.Adapter.SportsAdapter;
import com.example.matchup.Model.Sport;
import com.example.matchup.Model.User;
import com.example.matchup.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

//User profile
//TODO:
// (a) Retrieve user location and list it
// (b) Allow user to specify level of athleticism
// (c) Specify what sports user is interested in
public class ProfileFragment extends Fragment implements SportsAdapter.OnSportChange {

    CircleImageView image_profile;
    TextView username;

    DatabaseReference reference;
    DatabaseReference sportsReference;
    FirebaseUser fuser;
    RecyclerView recyclerView;
    SportsAdapter sportsAdapter;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        recyclerView = view.findViewById(R.id.recycler_view);
        fab = view.findViewById(R.id.fab);

        sportsAdapter = new SportsAdapter(getActivity(), this);
        recyclerView.setAdapter(sportsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        sportsReference = reference.child("sports");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                updateRecyclerView(dataSnapshot);

                if(user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getContext()).load(user.getImageURL()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        return view;
    }

    void updateRecyclerView(DataSnapshot dataSnapshot){
        ArrayList<Sport> sports = new ArrayList<>((int)dataSnapshot.getChildrenCount());
        for(DataSnapshot snapshot: dataSnapshot.child("sports").getChildren()){
            Sport sp = new Sport(
                snapshot.getKey(),
                ((Number)snapshot.getValue()).intValue()
            );

            sports.add(sp);
        }

        sportsAdapter.setSports(sports);
        sportsAdapter.notifyDataSetChanged();
    }

    void showDialog(){

        final ArrayList<String> addableSports = new ArrayList<>(Arrays.asList(Sport.SPORT_LIST));
        for(Sport sport: sportsAdapter.getSports())
            addableSports.remove(sport.sportName);

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_sport);
        dialog.show();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_list_item_1, addableSports);

        final ListView listView = dialog.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sportName = adapter.getItem(i);
                sportsAdapter.addSport(new Sport(sportName, 0));
                sportsReference.child(sportName).setValue(0);
                dialog.cancel();
            }
        });


    }

    @Override
    public void onDeleteSport(String sportName) {
        reference.child("sports").child(sportName).removeValue();
    }

    @Override
    public void onProficiencyChange(String sportName, int proficiency){
        reference.child("sports").child(sportName).setValue(proficiency);
    }
}
