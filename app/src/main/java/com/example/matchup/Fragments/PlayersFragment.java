package com.example.matchup.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matchup.Adapter.ChatsAdapter;
import com.example.matchup.Adapter.PlayersAdapter;
import com.example.matchup.Model.User;
import com.example.matchup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//Displays other registered players
//TODO:
// (a) Display registers players from closest distance to farthest distance
// (b) onShortClick display user profile --> open another activity
public class PlayersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private PlayersAdapter playersAdapter;
    private ArrayList<DataSnapshot> mUsers;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_players, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);

        mUsers = new ArrayList<>();

        readUsers();

        return view;
    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                double lat = 0.0, lon = 0.0;
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    if(snap.getKey()!=firebaseUser.getUid())
                        mUsers.add(snap);
                    else {
                            Object objLat = snap.child("location").child("lat").getValue();
                            Object objLon = snap.child("location").child("lon").getValue();
                            if(objLat != null && objLon != null){
                                lat = (double) objLat;
                                lon = (double) objLon;
                            }
                    }
                }

                playersAdapter = new PlayersAdapter(getContext(), mUsers, false, lat, lon);
                recyclerView.setAdapter(playersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRefresh() {
        readUsers();
        swipeRefreshLayout.setRefreshing(false);
    }
}
