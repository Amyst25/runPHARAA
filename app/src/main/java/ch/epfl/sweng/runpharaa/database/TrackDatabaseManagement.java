package ch.epfl.sweng.runpharaa.database;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import ch.epfl.sweng.runpharaa.Firebase.Database;
import ch.epfl.sweng.runpharaa.Firebase.Storage;
import ch.epfl.sweng.runpharaa.CustLatLng;
import ch.epfl.sweng.runpharaa.tracks.FirebaseTrackAdapter;
import ch.epfl.sweng.runpharaa.tracks.Track;
import ch.epfl.sweng.runpharaa.user.User;

public class TrackDatabaseManagement {

    public final static String TRACKS_PATH = "tracksRefractored";
    public final static String TRACK_IMAGE_PATH = "TrackImages";

    public static FirebaseDatabase mFirebaseDatabase = Database.getInstance();
    public static DatabaseReference mDataBaseRef = mFirebaseDatabase.getReference();
    public static FirebaseStorage mFirebaseStorage = Storage.getInstance();
    public static StorageReference mStorageRef = mFirebaseStorage.getReference();

    public TrackDatabaseManagement() { }

    /**
     * Track a {@link Track} and add it to the database
     *
     * @param track
     */
    public static void writeNewTrack(final FirebaseTrackAdapter track) {
        //Generate a new key in the database
        final String key = mDataBaseRef.child(TRACKS_PATH).push().getKey();

        //Upload image
        Bitmap bitmap = track.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mStorageRef.child(TRACK_IMAGE_PATH).child(key).putBytes(data);
        uploadTask.addOnFailureListener(e -> Log.e("Storage", "Failed to upload image to storage :" + e.getMessage()));
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mStorageRef.child(TRACK_IMAGE_PATH).child(key).getDownloadUrl().addOnFailureListener(e -> Log.e("Storage", "Failed to download image url :" + e.getMessage())).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        track.setImageStorageUri(task1.getResult().toString());
                        track.setTrackUid(key);
                        mDataBaseRef.child(TRACKS_PATH).child(key).setValue(track).addOnFailureListener(e -> Log.e("Database", "Failed to upload new track :" + e.getMessage())).addOnSuccessListener(aVoid -> {
                            User.instance.addToCreatedTracks(key);
                            UserDatabaseManagement.updateCreatedTracks(key);
                        });
                    }
                });
            }
        });
    }

    /**
     * Given a track, updates the corresponding entry in the Firebase Database..
     *
     * @param track
     */
    public static void updateTrack(Track track){
        FirebaseTrackAdapter adapter = new FirebaseTrackAdapter(track);
        mDataBaseRef.child(TRACKS_PATH).child(adapter.getTrackUid()).setValue(adapter);
    }

    /**
     * Given a DataSnapshot from the Firebase Database and a track key, return the corresponding track.
     *
     * @param dataSnapshot
     * @param key
     * @return
     */
    public static Track initTrack(DataSnapshot dataSnapshot, String key){
       return new Track(dataSnapshot.child(key).getValue(FirebaseTrackAdapter.class));
    }

    /**
     * Given a DataSnapshot from the Firebase Database, returns the list of tracks near location.
     *
     * @param dataSnapshot
     * @return
     */
    public static List<Track> initTracksNearLocation(DataSnapshot dataSnapshot, LatLng location) {
        List<Track> tracksNearMe = new ArrayList<>();
        for (DataSnapshot c : dataSnapshot.getChildren()) {
            CustLatLng requestedLocation = new CustLatLng(location.latitude, location.longitude);
            int userPreferredRadius = User.instance.getPreferredRadius();

            if(c.child("path").child("0").getValue(CustLatLng.class) != null) {
                Log.d("Database", "track near me");
                if (c.child("path").child("0").getValue(CustLatLng.class).distance(requestedLocation) <= userPreferredRadius) {
                    tracksNearMe.add(new Track(c.getValue(FirebaseTrackAdapter.class)));
                }
            }
        }

        Collections.sort(tracksNearMe, (o1, o2) -> {
            double d1 = o1.getStartingPoint().distance(CustLatLng.LatLngToCustLatLng(User.instance.getLocation()));
            double d2 = o2.getStartingPoint().distance(CustLatLng.LatLngToCustLatLng(User.instance.getLocation()));
            return Double.compare(d1, d2);
        });
        return tracksNearMe;
    }

    /**
     * Given a DataSnapshot from the Firebase Database, returns the list of created tracks.
     *
     * @param dataSnapshot
     * @return
     */
    /*public static List<Track> initCreatedTracks(DataSnapshot dataSnapshot){
        List<Track> createdTracks = new ArrayList<>();
        for(DataSnapshot c : dataSnapshot.getChildren()){
            if(User.instance.getCreatedTracks() != null){
                if(User.instance.getCreatedTracks().contains(c.getKey())){
                    createdTracks.add(new Track(c.getValue(FirebaseTrackAdapter.class)));
                }
            }
        }
        Collections.sort(createdTracks, (o1, o2) -> {
            double d1 = o1.getStartingPoint().distance(CustLatLng.LatLngToCustLatLng(User.instance.getLocation()));
            double d2 = o2.getStartingPoint().distance(CustLatLng.LatLngToCustLatLng(User.instance.getLocation()));
            return Double.compare(d1, d2);
        });
        return createdTracks;
    }*/

    /**
     * Given a DataSnapshot from the Firebase Database, returns the list of favourite tracks.
     *
     * @param dataSnapshot
     * @return
     */
    public static List<Track> initFavouritesTracks(DataSnapshot dataSnapshot) {
        List<Track> favouriteTracks = new ArrayList<>();
        for (DataSnapshot c : dataSnapshot.getChildren()) {
            if (User.instance.getFavoriteTracks() != null) {
                if (User.instance.getFavoriteTracks().contains(c.getKey())) {
                    favouriteTracks.add(new Track(c.getValue(FirebaseTrackAdapter.class)));
                }
            }
        }
        Collections.sort(favouriteTracks, (o1, o2) -> {
            double d1 = o1.getStartingPoint().distance(CustLatLng.LatLngToCustLatLng(User.instance.getLocation()));
            double d2 = o2.getStartingPoint().distance(CustLatLng.LatLngToCustLatLng(User.instance.getLocation()));
            return Double.compare(d1, d2);
        });
        return favouriteTracks;
    }

    /**
     * Read the data from the Firebase Database. Two methods to override.
     *
     * @param child
     * @param listener
     */
    public static void mReadDataOnce(String child, final OnGetDataListener listener) {
        DatabaseReference ref = mDataBaseRef.child(child);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    /**
     * Listener interface for the mReadDataOnce method.
     */
    public interface OnGetDataListener {
        void onSuccess(DataSnapshot data);

        void onFailed(DatabaseError databaseError);
    }
}