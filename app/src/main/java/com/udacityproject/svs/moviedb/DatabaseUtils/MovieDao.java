package com.udacityproject.svs.moviedb.DatabaseUtils;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY movies.popularity DESC")
    LiveData<List<MovieEntry>> loadPopular();

    @Query("SELECT * FROM movies ORDER BY movies.rating DESC")
    LiveData<List<MovieEntry>> loadTopRated();

    @Query("SELECT * FROM movies WHERE favourite = 1")
    LiveData<List<MovieEntry>> loadFavourites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie( MovieEntry movieEntry);

    @Update
    void updateMovie(MovieEntry movieEntry);

}
