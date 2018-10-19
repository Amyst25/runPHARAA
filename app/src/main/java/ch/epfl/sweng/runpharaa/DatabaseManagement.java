package ch.epfl.sweng.runpharaa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

public class DatabaseManagement {

    public final static String TRACKS_PATH = "tracks";
    public final static String TRACK_IMAGE_PATH = "TrackImages";

    public static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    public static DatabaseReference mDataBaseRef = mFirebaseDatabase.getReference();
    public static FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    public static StorageReference mStorageRef = mFirebaseStorage.getReference();

    public DatabaseManagement() { }

    /**
     * Track a {@link Track} and add it to the database
     * @param track
     */
    public static void writeNewTrack(final Track track) {
        //Generate a new key in the database
        final String key = mDataBaseRef.child(TRACKS_PATH).push().getKey();

        //Upload image
        Bitmap bitmap = track.getImageBitMap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mStorageRef.child(TRACK_IMAGE_PATH).child(key).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Storage", "Failed to upload image to storage :" + e.getMessage());
            }
        });
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mStorageRef.child(TRACK_IMAGE_PATH).child(key).getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Storage", "Failed to download image url :"+ e.getMessage());
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                track.setImageStorageUri(task.getResult().toString());
                                track.setTrackUid(key);
                                mDataBaseRef.child(TRACKS_PATH).child(key).setValue(track).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Database", "Failed to upload new track :" + e.getMessage());
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public static void updateTrack(Track track){
        //TODO check if it work
        mDataBaseRef.child(TRACKS_PATH).child(track.getTrackUid()).setValue(track);
    }

    public static List<Track> initTracksNearMe(DataSnapshot dataSnapshot){
        List<Track> tracksNearMe = new ArrayList<>();
        for(DataSnapshot c : dataSnapshot.getChildren()){
            CustLatLng userLocation = new CustLatLng(User.instance.getLocation().latitude, User.instance.getLocation().longitude);
            int userPreferredRadius = User.instance.getPreferredRadius();
            if(c.child("startingPoint").getValue(CustLatLng.class).distance(userLocation) <= userPreferredRadius){
                tracksNearMe.add(c.getValue(Track.class));
                Log.d("Client : size ", Integer.toString(tracksNearMe.size()));
            }
        }
        return tracksNearMe;
    }

    public static List<Track> initCreatedTracks(DataSnapshot dataSnapshot){
        List<Track> createdTracks = new ArrayList<>();
        for(DataSnapshot c : dataSnapshot.getChildren()){
            if(User.instance.getCreatedTracksKeys() != null){
                if(User.instance.getCreatedTracksKeys().contains(c.getKey())){
                    createdTracks.add(c.getValue(Track.class));
                }
            }
        }
        return createdTracks;
    }

    public static List<Track> initFavouritesTracks(DataSnapshot dataSnapshot){
        List<Track> favouriteTracks = new ArrayList<>();
        for(DataSnapshot c : dataSnapshot.getChildren()) {
            if (User.instance.getFavoritesTracksKeys() != null) {
                if (User.instance.getFavoritesTracksKeys().contains(c.getKey())) {
                    favouriteTracks.add(c.getValue(Track.class));
                }
            }
        }
        return favouriteTracks;
    }

    public interface OnGetDataListener {
        public void onSuccess(DataSnapshot data);
        public void onFailed(DatabaseError databaseError);
    }

    public static void mReadDataOnce(String child, final OnGetDataListener listener) {
        mDataBaseRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
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
}
