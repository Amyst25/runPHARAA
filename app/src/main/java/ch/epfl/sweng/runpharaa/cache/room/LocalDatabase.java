package ch.epfl.sweng.runpharaa.cache.room;
import android.arch.persistence.room.*;

import ch.epfl.sweng.runpharaa.user.User;

@Database(entities = {User.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract UserDao UserDao();
}
