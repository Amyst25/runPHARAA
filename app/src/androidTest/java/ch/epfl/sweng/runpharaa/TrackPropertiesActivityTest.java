package ch.epfl.sweng.runpharaa;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import ch.epfl.sweng.runpharaa.tracks.Track;

import static android.os.SystemClock.sleep;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TrackPropertiesActivityTest {

    @Rule
    public ActivityTestRule<TrackPropertiesActivity> mActivityRule =
            new ActivityTestRule<>(TrackPropertiesActivity.class, true, false);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void initUserAndTracks() {
        User.instance = new User("FakeUser", 2000, Uri.parse(""), new HashSet<Integer>(), new HashSet<Integer>(), new LatLng(21.23, 12.112), false, "aa");
    }

    @Test
    public void correctValuesDisplayedForTrack1() {
        Track t1 = Track.allTracks.get(0);
        launchWithTrackId(0);
        withId(R.id.trackTitleID).matches(withText(t1.getName()));
        withId(R.id.trackLengthID).matches(withText("Length: " + Double.toString(t1.getProperties().getLength()) + "m"));
        withId(R.id.trackLikesID).matches(withText("Likes: " + t1.getProperties().getLikes()));
    }

    @Test
    public void testLike() {
        mActivityRule.launchActivity(null);
        onView(withId(R.id.buttonLikeID)).perform(click());
    }

    @Test
    public void testFavourite() {
        mActivityRule.launchActivity(null);
        onView(withId(R.id.buttonFavoriteID)).perform(click());
    }

    public void pressingLikeUpdatesValue() {
        int likesBefore = Track.allTracks.get(0).getProperties().getLikes();
        launchWithTrackId(0);
        onView(withId(R.id.buttonLikeID)).perform(click());
        withId(R.id.trackLikesID).matches(withText("Likes: " + likesBefore + 1));
        sleep(500);
        onView(withId(R.id.buttonLikeID)).perform(click());
        withId(R.id.trackLikesID).matches(withText("Likes: " + likesBefore));
    }

    @Test
    public void addingToFavoritesUpdatesValue() {
        int favsBefore = Track.allTracks.get(0).getProperties().getFavorites();
        launchWithTrackId(0);
        onView(withId(R.id.buttonFavoriteID)).perform(click());
        withId(R.id.trackFavouritesID).matches(withText("Likes: " + favsBefore + 1));
        sleep(500);
        onView(withId(R.id.buttonFavoriteID)).perform(click());
        withId(R.id.trackFavouritesID).matches(withText("Likes: " + favsBefore));
    }

    @Test
    public void addingToFavoritesUpdatesUser() {
        launchWithTrackId(0);
        onView(withId(R.id.buttonFavoriteID)).perform(click());
        sleep(500);
        assertTrue(User.instance.getFavoriteTracks().contains(0));
    }

    private void launchWithTrackId(int id) {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, TrackPropertiesActivity.class);
        intent.putExtra("TrackID", id);
        mActivityRule.launchActivity(intent);
    }

}



