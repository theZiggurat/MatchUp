package com.example.matchup.Model;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private HashMap<String, Integer> sports;
    private double latitude, longitude;


    public User(String id, String username, String imageURL) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.sports = new HashMap<>();
    }

    public User() {

    }

    public void addSport(String sport, int skill){
        if(sports.containsKey(sport))
            sports.remove(sport);
        sports.put(sport, skill);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public HashMap<String, Integer> getSports(){
        return this.sports;
    }
}
