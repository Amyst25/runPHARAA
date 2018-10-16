package ch.epfl.sweng.runpharaa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static ch.epfl.sweng.runpharaa.User.FAKE_USER;

public class TrackPropertiesActivity extends AppCompatActivity {

    //TODO: Check if ScrollView is working!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_properties);
        Intent intent = getIntent();
        String trackID = intent.getStringExtra("TrackID");
        Track track = getTrackByID(FAKE_USER.tracksNearMe(), trackID); //TODO ERASE

        ImageView trackBackground = findViewById(R.id.trackBackgroundID);
        trackBackground.setImageResource(track.getImage());

        TextView trackTitle = findViewById(R.id.trackTitleID);
        trackTitle.setText(track.getLocation());

        TextView trackCreator = findViewById(R.id.trackCreatorID);
        trackCreator.setText(/*track.getCreator_id()*/"Creator: Test User");

        TextView trackDuration = findViewById(R.id.trackDurationID);
        trackDuration.setText("Duration: " + track.getAverageTimeLength() + " minutes");

        TextView trackLength = findViewById(R.id.trackLengthID);
        trackLength.setText("Length: " + Double.toString(track.getTrackLength()) + "m");

        /*
        TextView trackHeightDifference = findViewById(R.id.trackHeightDiffID);
        trackHeightDifference.setText("Height Difference: " + Double.toString(track.getHeight_diff())); //TODO: Figure out height difference.
        */

        //TODO: Add Like and Favourite buttons.

        TextView trackLikes = findViewById(R.id.trackLikesID);
        trackLikes.setText("Likes: " + track.getLikes());

        TextView trackFavourites = findViewById(R.id.trackFavouritesID);
        trackFavourites.setText("Favourites: " + track.getFavourites());

        /*
        TextView Tags = findViewById(R.id.trackTagsID);
        Tags.setText();
        */
    }

    private Track getTrackByID(ArrayList<Track> tracks, String trackID) {
        for (Track t: tracks) {
            if (t.getTrackUid().equals(trackID)) {
                return t;
            }
        }
        return null;
    }
}
