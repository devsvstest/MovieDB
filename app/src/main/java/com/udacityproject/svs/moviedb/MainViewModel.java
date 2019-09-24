package com.udacityproject.svs.moviedb;

import android.app.Application;

import com.udacityproject.svs.moviedb.DatabaseUtils.AppDatabase;
import com.udacityproject.svs.moviedb.DatabaseUtils.MovieEntry;
import org.json.JSONArray;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<MovieEntry>> topRated;
    private LiveData<List<MovieEntry>> favourites;
    private LiveData<List<MovieEntry>> popular;
    private final AppDatabase database;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
        popular = database.movieDao().loadPopular();
        topRated = database.movieDao().loadTopRated();
        favourites = database.movieDao().loadFavourites();
    }

    public LiveData<List<MovieEntry>> getPopular(){
        return popular;
    }

    public LiveData<List<MovieEntry>> getTopRated(){
        return topRated;
    }

    public LiveData<List<MovieEntry>> getFavourites(){
        return favourites;
    }

}
