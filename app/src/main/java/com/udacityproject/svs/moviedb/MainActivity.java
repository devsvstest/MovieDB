package com.udacityproject.svs.moviedb;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacityproject.svs.moviedb.DatabaseUtils.AppDatabase;
import com.udacityproject.svs.moviedb.DatabaseUtils.MovieEntry;
import com.udacityproject.svs.moviedb.NetworkUtils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickHandler {

    public static MovieAdapter movieAdapter;
    private RecyclerView mRecyclerView;
    private static Menu mMenu;
    private static TextView mErrorMessage;
    private static ProgressBar mProgressBar;
    private static MainViewModel viewModel;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if( savedInstanceState != null ){
            mMenu.findItem(R.id.popular)
                    .setEnabled(savedInstanceState.getBoolean("menu_popular"));
            mMenu.findItem(R.id.top_rated)
                    .setEnabled(savedInstanceState.getBoolean("menu_top_rated"));
            mMenu.findItem(R.id.favourite)
                    .setEnabled(savedInstanceState.getBoolean("favourite"));
        }

        mRecyclerView = findViewById(R.id.rv_movies_grid);
        mErrorMessage = findViewById(R.id.tv_error_message);
        mProgressBar = findViewById(R.id.pb_progress_bar);

        GridLayoutManager gridLayoutManager;
        gridLayoutManager = new GridLayoutManager(MainActivity.this, 2,
                GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager( gridLayoutManager );
        mRecyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter( this );
        mRecyclerView.setAdapter( movieAdapter );

        mDb = AppDatabase.getInstance(getApplicationContext());

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

    }


    public static void showError(){
        mErrorMessage.setVisibility( View.VISIBLE );
        mProgressBar.setVisibility( View.INVISIBLE );
    }

    public static void showLoadingIndicator(){
        mErrorMessage.setVisibility( View.INVISIBLE );
        mProgressBar.setVisibility( View.VISIBLE );
    }

    public static void hideProgressBar(){
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClickMovie( MovieEntry movieEntry ) {

        Bundle movieBundle = new Bundle();
        movieBundle.putInt("movieId", movieEntry.getMovieId());
        movieBundle.putString("posterPath", movieEntry.getPosterPath());
        movieBundle.putString("title", movieEntry.getTitle());
        movieBundle.putString("overview", movieEntry.getOverview());
        movieBundle.putLong("releaseDate", movieEntry.getReleaseDate());
        movieBundle.putDouble("rating", movieEntry.getRating());
        movieBundle.putDouble("popularity", movieEntry.getPopularity());
        movieBundle.putBoolean("favourite", movieEntry.getFavourite());

        Intent intent = new Intent(this, MovieDetails.class);
        intent.putExtra("movieBundle", movieBundle);
        startActivity(intent);

    }

    public class LoadData extends AsyncTask<String, Void, Void> {

        private boolean flag = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity.showLoadingIndicator();
        }

        @Override
        protected Void doInBackground(String... strings) {

            if ( strings.length == 0 )   return null;

            for( int i=1; i<=3; i++ ){

                URL url = NetworkUtils.buildURL( strings[0], i);
                //Log.d("LoadData", url.toString() + "\n" );

                String tempData;
                try{
                    tempData = NetworkUtils.loadDataFromNetwork(url);
                }catch ( IOException e ){
                    e.printStackTrace();
                    return null;
                }

                try {
                    JSONArray temp = (JSONArray) new JSONObject(tempData).get("results");

                    for( int j=0; j<temp.length(); j++ ) {
                        JSONObject jo = temp.getJSONObject(j);

                        int movieId = jo.getInt("id");
                        String posterPath = jo.getString("poster_path");
                        String title = jo.getString("original_title");
                        String overview = jo.getString("overview");
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date releaseDate = null;
                        try {
                            releaseDate = df.parse(jo.getString("release_date"));
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        Double rating = jo.getDouble("vote_average");
                        Double popularity = jo.getDouble("popularity");

                        MovieEntry movieEntry = new MovieEntry(movieId, posterPath, title, overview,
                                releaseDate.getTime(), rating, popularity, false);
                        mDb.movieDao().insertMovie(movieEntry);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute( Void data ) {
            Log.d("PostExecute", "done\n");
            MainActivity.hideProgressBar();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu, menu );

        if( mMenu == null ){
            mMenu = menu;
        }

        if ( !mMenu.findItem(R.id.popular).isEnabled()) {
            viewModel.getPopular().observe(this, new Observer<List<MovieEntry>>() {
                @Override
                public void onChanged(List<MovieEntry> movieEntries) {
                    if( movieEntries.size() == 0 ) {
                        new LoadData().execute("top_rated");
                    }
                    movieAdapter.setAdapterList( movieEntries );
                }
            });
            menu.findItem(R.id.popular).setEnabled(false);
            menu.findItem(R.id.top_rated).setEnabled(true);
            menu.findItem(R.id.favourite).setEnabled(true);
        } else if( !mMenu.findItem(R.id.top_rated).isEnabled()){
            viewModel.getTopRated().observe(this, new Observer<List<MovieEntry>>() {
                @Override
                public void onChanged(List<MovieEntry> movieEntries) {
                    if( movieEntries.size() == 0 ) {
                        new LoadData().execute("top_rated");
                    }
                    movieAdapter.setAdapterList( movieEntries );
                }
            });
            menu.findItem(R.id.top_rated).setEnabled(false);
            menu.findItem(R.id.popular).setEnabled(true);
            menu.findItem(R.id.favourite).setEnabled(true);
        }else if( !mMenu.findItem(R.id.favourite).isEnabled() ){
            viewModel.getFavourites().observe(this, new Observer<List<MovieEntry>>() {
                @Override
                public void onChanged(List<MovieEntry> movieEntries) {
                    movieAdapter.setAdapterList( movieEntries );
                }
            });
            menu.findItem(R.id.top_rated).setEnabled(true);
            menu.findItem(R.id.popular).setEnabled(true);
            menu.findItem(R.id.favourite).setEnabled(false);
        }

        mMenu = menu;
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.popular ){
            viewModel.getPopular().observe(this, new Observer<List<MovieEntry>>() {
                @Override
                public void onChanged(List<MovieEntry> movieEntries) {
                    if( movieEntries.size() == 0 ) {
                        new LoadData().execute("top_rated");
                    }
                    movieAdapter.setAdapterList( movieEntries );
                }
            });
            mMenu.findItem(R.id.popular).setEnabled(false);
            mMenu.findItem(R.id.top_rated).setEnabled(true);
            mMenu.findItem(R.id.favourite).setEnabled(true);
        }
        else if ( item.getItemId() == R.id.top_rated ){
            viewModel.getTopRated().observe(this, new Observer<List<MovieEntry>>() {
                @Override
                public void onChanged(List<MovieEntry> movieEntries) {
                    if( movieEntries.size() == 0 ) {
                        new LoadData().execute("top_rated");
                    }
                    movieAdapter.setAdapterList( movieEntries );
                }
            });
            mMenu.findItem(R.id.top_rated).setEnabled(false);
            mMenu.findItem(R.id.popular).setEnabled(true);
            mMenu.findItem(R.id.favourite).setEnabled(true);
        } else if(item.getItemId() == R.id.favourite){
            viewModel.getFavourites().observe(this, new Observer<List<MovieEntry>>() {
                @Override
                public void onChanged(List<MovieEntry> movieEntries) {
                    movieAdapter.setAdapterList( movieEntries );
                }
            });
            mMenu.findItem(R.id.top_rated).setEnabled(true);
            mMenu.findItem(R.id.popular).setEnabled(true);
            mMenu.findItem(R.id.favourite).setEnabled(false);
        }
        else return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("menu_popular", mMenu.findItem(R.id.popular).isEnabled());
        outState.putBoolean("menu_top_rated", mMenu.findItem(R.id.top_rated).isEnabled());
        outState.putBoolean("favourite", mMenu.findItem(R.id.favourite).isEnabled());
    }

}
