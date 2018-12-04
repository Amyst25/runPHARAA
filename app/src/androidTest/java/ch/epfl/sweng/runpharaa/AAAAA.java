package ch.epfl.sweng.runpharaa;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.runpharaa.Initializer.TestInitLocation;
import ch.epfl.sweng.runpharaa.firebase.Database;
import ch.epfl.sweng.runpharaa.user.User;

import static android.os.SystemClock.sleep;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AAAAA extends TestInitLocation {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class, false, false);

    @BeforeClass
    public static void initUser() {
        User.set(Database.getUser());
    }

    @Before
    public void launchIntentFirst(){
        mActivityRule.launchActivity(new Intent());
        sleep(3000);
        mActivityRule.finishActivity();
    }

    @Test
    public void heeeeeySisteeeeeer() {
        mActivityRule.launchActivity(null);
        sleep(2000);
        assertTrue(true);
    }

}
