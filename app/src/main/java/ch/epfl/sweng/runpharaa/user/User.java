package ch.epfl.sweng.runpharaa.user;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import ch.epfl.sweng.runpharaa.CustLatLng;
import ch.epfl.sweng.runpharaa.tracks.Track;
import ch.epfl.sweng.runpharaa.utils.Required;

public final class User {

    public static User instance;
    private int preferredRadius = 2000;
    private String name;
    private Uri picture;
    private String uId;
    private Set<String> createdTracks;
    private Set<String> favoriteTracks;
    private Set<String> likedTracks;
    private LatLng location;
    //public static User FAKE_USER = new User("Toto", new LatLng(46.518510, 6.563199), 2000);

    public User(String name, int preferredRadius, Uri picture, LatLng location, String uId) {
        Required.nonNull(name, "The name of an user cannot be null");
        Required.nonNull(location, "The location of an user cannot be null");
        Required.nonNull(uId, "The uId of an user cannot be null");
        Required.nonNull(picture, "The picture of an user cannot be null");

        this.preferredRadius = preferredRadius;
        this.name = name;
        this.picture = picture;
        this.createdTracks = new HashSet<>();
        this.favoriteTracks = new HashSet<>();
        this.likedTracks = new HashSet<>();
        this.location = location;
        this.uId = uId;
    }

    public User(String name, LatLng location, int preferredRadius) {
        this(name, preferredRadius, null, location, name);
    }

    public static void set(String name, float preferredRadius, Uri picture, LatLng location, String uId) {
        instance = new User(name, (int)(preferredRadius*100), picture, location, uId);
    }

    public FirebaseUserAdapter getFirebaseAdapter() { return new FirebaseUserAdapter(this); }

    public int getPreferredRadius() {
        return preferredRadius;
    }

    /**
     *
     * @param newRadius in km
     */
    public void setPreferredRadius(float newRadius) {
        this.preferredRadius = (int)(newRadius*1000);
    }

    /**
     * Check if the user already liked a particular track
     *
     * @param trackId the track's id
     * @return true if the user already liked the track
     */
    public boolean alreadyLiked(String trackId) {
        return likedTracks.contains(trackId);
    }

    /**
     * Add a track id in the set of liked tracks if it is not already there
     *
     * @param trackId the track's id
     */
    public void like(String trackId) {
        likedTracks.add(trackId);
    }

    /**
     * Check if the track is already in user's favorites
     *
     * @param trackId the track's id
     * @return true if the track is in the favorites
     */
    public void unlike(String trackId) {
        likedTracks.remove(trackId);
    }

    /**
     * Check if Track is already in favourites.
     *
     * @param trackId
     * @return
     */
    public boolean alreadyInFavorites(String trackId) {
        Log.i("Favourites", "already in favotites" + favoriteTracks.contains(trackId));
        return favoriteTracks.contains(trackId);
    }

    /**
     * Add a Track id in the set of favourite tracks if it is not already there.
     *
     * @param trackId the track's id
     */
    public void addToFavorites(String trackId) { favoriteTracks.add(trackId); }

    /**
     * Add a Track id in the set of created tracks.
     *
     * @param trackId
     */
    public void addToCreatedTracks(String trackId) {
        createdTracks.add(trackId);
    }

    /**
     * Remove a track id from the set of favorite tracks if it is present.
     *
     * @param trackId the track's id
     */
    public void removeFromFavorites(String trackId) {
        favoriteTracks.remove(trackId);
    }

    /**
     * Getter for the user's location
     *
     * @return location
     */
    public LatLng getLocation() {
        return location;
    }

    /**
     * Update the user's location.
     *
     * @param newLocation
     */
    public void setLocation(LatLng newLocation) {
        this.location = newLocation;
    }

    /**
     * Return the name of the user
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    public String getID() {
        return uId;
    }

    public Uri getPicture() {
        return picture;
    }

    public Set<String> getCreatedTracks() {
        return createdTracks;
    }

    public void setCreatedTracks(Set<String> createdTracks) {
        this.createdTracks = createdTracks;
    }

    public Set<String> getFavoriteTracks() {
        return favoriteTracks;
    }

    public void setFavoriteTracks(Set<String> favoriteTracks) { this.favoriteTracks = favoriteTracks; }

    public Set<String> getLikedTracks() {
        return likedTracks;
    }

    public void setLikedTracks(Set<String> likedTracks) {
        this.likedTracks = likedTracks;
    }

}