package ch.epfl.sweng.runpharaa.cache.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import ch.epfl.sweng.runpharaa.user.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(User user);

    @Delete
    void remove(User user);
}
