package ch.epfl.sweng.runpharaa;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.runpharaa.Initializer.TestInitLocation;
import ch.epfl.sweng.runpharaa.firebase.Database;
import ch.epfl.sweng.runpharaa.user.User;

import static android.os.SystemClock.sleep;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AAA extends TestInitLocation {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void initUser() {
        User.set(Database.getUser());
        mActivityRule.launchActivity(new Intent());
    }

    @Test
    public void heeeeeyBrotheeeeer() {
        sleep(2000);
        assertTrue(true);
    }

}
