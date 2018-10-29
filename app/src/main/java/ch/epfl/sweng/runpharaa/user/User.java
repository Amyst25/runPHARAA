package ch.epfl.sweng.runpharaa.user;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.epfl.sweng.runpharaa.CustLatLng;
import ch.epfl.sweng.runpharaa.tracks.Track;
import java.util.List;

public final class User {
    private int preferredRadius = 2000;
    private final String name;
    //TODO: put default picture
    private final Uri picture;

    //Of type String because we only need the key reference of the track in the database
    private List<String> createdTracksKeys;
    private List<String> favoritesTracksKeys;
    private List<String> likedTracksKeys;

    private LatLng location;
    private final boolean admin;
    private final String uId;

    public static User instance;
    //public static User FAKE_USER = new User("Toto", new LatLng(46.518510, 6.563199), 2000);

    public User(String name, int preferredRadius, Uri picture, List<String> createdTracksKeys, List<String> favoritesTracksKeys, LatLng location, Boolean admin, String uId) {
        this.preferredRadius = preferredRadius;
        this.name = name;
        this.picture = picture;
        this.createdTracksKeys = createdTracksKeys;
        this.favoritesTracksKeys = favoritesTracksKeys;
        this.likedTracksKeys = new ArrayList<>();
        this.location = location;
        this.admin = admin;
        this.uId = uId;
    }

    public User(String name, LatLng location, int preferredRadius) {
        //TODO must be changed later when the user's login and the database are on
        this(name, preferredRadius, null, new ArrayList<String>(), new ArrayList<String>(), location, false, name);
    }

    public int getPreferredRadius() {
        return preferredRadius;
    }

    /**
     * Make an ordered list of all the tracks that are in a RADIUS of 2km.
     *
     * @return ordered list of tracks
     */
    @TargetApi(Build.VERSION_CODES.N)
    public ArrayList<Track> tracksNearMe() {
        ArrayList<Track> nm = new ArrayList<>();
        ArrayList<Track> allTracks = Track.allTracks; //Todo muste be changed when the database is done -> Can actually be deleted?

        //filter the tracks that start too far from the location
        for (Track tr : allTracks) {
            if (tr.getStartingPoint().distance(CustLatLng.LatLngToCustLatLng(location)) <= preferredRadius) {
                nm.add(tr);
            }
        }

        //order them from the nearest to the furthest
        Collections.sort(nm, new Comparator<Track>() {
            @Override
            public int compare(Track o1, Track o2) {
                double d1 = o1.getStartingPoint().distance(CustLatLng.LatLngToCustLatLng(location));
                double d2 = o2.getStartingPoint().distance(CustLatLng.LatLngToCustLatLng(location));
                return Double.compare(d1, d2);
            }
        });
        return nm;
    }

    public List<String> getFavoritesTracksKeys() {
        return favoritesTracksKeys;
    }

    public List<String> getCreatedTracksKeys() {
        return createdTracksKeys;
    }

    /**
     * Check if the user already liked a particular track
     * @param trackId the track's id
     * @return true if the user already liked the track
     */
    public boolean alreadyLiked(String trackId) {
        return likedTracksKeys.contains(trackId);
    }

    /**
     * Add a track id in the set of liked tracks if it is not already there
     * @param trackId the track's id
     */
    public void like(String trackId) {
        if (!alreadyLiked(trackId)) {
            likedTracksKeys.add(trackId);
        }
    }

    /**
     * Check if the track is already in user's favorites
     *
     * @param trackId the track's id
     * @return true if the track is in the favorites
     */
    public void unlike(String trackId) {
        likedTracksKeys.remove(trackId);
    }

    /**
     *Check if Track is already in favourites.
     *
     * @param trackId
     * @return
     */
    public boolean alreadyInFavorites(String trackId) {
        return favoritesTracksKeys.contains(trackId);
    }

    /**
     * Add a Track id in the set of favourite tracks if it is not already there.
     *
     * @param trackId the track's id
     */
    public void addToFavorites(String trackId) {
        Log.i("WALLOU", "adding");
        if (!alreadyInFavorites(trackId)) {
            favoritesTracksKeys.add(trackId);
            Log.i("hahaha", "Adding track: " + trackId);
        }
    }

    /**
     * Add a Track id in the set of created tracks.
     *
     * @param trackId
     */
    public void addToCreatedTracks(String trackId) {
        createdTracksKeys.add(trackId);
    }

    /**
     * Remove a track id from the set of favorite tracks if it is present.
     *
     * @param trackId the track's id
     */
    public void removeFromFavorites(String trackId) {
        favoritesTracksKeys.remove(trackId);
    }

    /**
     * Getter for the user's location
     *
     * @return location
     */
    public LatLng getLocation() { return location; }

    /**
     * Return the name of the user
     *
     * @return name
     */
    public String getName() { return name; }

    public String getID() { return uId; }

    public Uri getPicture(){ return picture; }

    public List<String> getCreatedTracks(){ return createdTracksKeys; }

    public List<String> getFavoriteTracks(){ return favoritesTracksKeys; }

    public static void set(String name, int preferredRadius, Uri picture, List<String> createdTracks, List<String> favorites, LatLng location, Boolean admin, String uId){
        instance = new User(name, preferredRadius, picture, createdTracks, favorites, location, admin, uId);
    }

    /**
     * Update the user's location.
     *
     * @param newLocation
     */
    public void setLocation(LatLng newLocation) { this.location = newLocation; }

    public void setPreferredRadius(int newRadius) {
        this.preferredRadius = newRadius;
    }

}