package com.udacityproject.svs.moviedb;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacityproject.svs.moviedb.DatabaseUtils.AppDatabase;
import com.udacityproject.svs.moviedb.DatabaseUtils.MovieEntry;
import com.udacityproject.svs.moviedb.NetworkUtils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MovieDetails extends AppCompatActivity
        implements ReviewAdapter.ReviewOnClickHandler, TrailerAdapter.TrailerOnClickHandler{

    private static ImageView mPoster;
    private static ImageView favouriteIcon;
    private static TextView mTitle;
    private static TextView mOverview;
    private static TextView mReleaseDate;
    private static TextView mUserReviews;
    private static RecyclerView mReviewRecyclerView;
    private static RecyclerView mTrailerRecyclerView;
    private static TrailerAdapter mTrailerAdapter;
    private static ReviewAdapter mReviewAdapter;
    private static Intent mIntent;
    private static AppDatabase mDb;
    private static MovieEntry movieEntry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_detail);

        mPoster = findViewById(R.id.iv_movie_poster);
        mTitle = findViewById(R.id.tv_movie_title);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mUserReviews = findViewById(R.id.tv_rating);
        mOverview = findViewById(R.id.tv_overview);
        favouriteIcon = findViewById(R.id.iv_favourite);

        mTitle.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        mIntent = getIntent();
        mDb = AppDatabase.getInstance(getApplicationContext());

        if( mIntent.hasExtra("movieBundle" ) ) {

            Bundle movieBundle = mIntent.getBundleExtra("movieBundle");

            movieEntry = new MovieEntry(movieBundle.getInt("movieId"),
                    movieBundle.getString("posterPath"),
                    movieBundle.getString("title"),
                    movieBundle.getString("overview"),
                    movieBundle.getLong("releaseDate"),
                    movieBundle.getDouble("rating"),
                    movieBundle.getDouble("popularity"),
                    movieBundle.getBoolean("favourite"));

            Picasso.with( this )
                    .load("http://image.tmdb.org/t/p/w185/" + movieEntry.getPosterPath())
                    .into( mPoster );

            mTitle.setText( movieEntry.getTitle() );

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(movieEntry.getReleaseDate());
            String year = Integer.toString( calendar.get(Calendar.YEAR) );

            mReleaseDate.setText( year );
            mUserReviews.setText( Double.toString(movieEntry.getRating()) + "/10");
            mOverview.setText( movieEntry.getOverview() );

            if(movieEntry.getFavourite()){
                favouriteIcon.setImageResource(R.drawable.baseline_favorite_black_48);
                movieEntry.setFavourite(true);
            }else{
                favouriteIcon.setImageResource(R.drawable.baseline_favorite_border_black_48);
                movieEntry.setFavourite(false);
            }
        }

        mReviewRecyclerView = findViewById(R.id.rv_reviews_grid);
        mTrailerRecyclerView = findViewById(R.id.rv_trailers_grid);
        mReviewAdapter = new ReviewAdapter(this);
        mTrailerAdapter = new TrailerAdapter(this);

        LinearLayoutManager linearLayoutManagerReview, linearLayoutManagerTrailer;
        linearLayoutManagerReview = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL,
                false);
        linearLayoutManagerTrailer = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL,
                false);
        mReviewRecyclerView.setLayoutManager(linearLayoutManagerReview);
        mTrailerRecyclerView.setLayoutManager(linearLayoutManagerTrailer);
        mReviewRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setHasFixedSize(true);

        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        URL urlReviews = NetworkUtils
                .buildReviewURL(Integer.toString(movieEntry.getMovieId()), 1);
        URL urlTrailers = NetworkUtils
                .buildTrailerURL(Integer.toString(movieEntry.getMovieId()), 1);
        new LoadReviews().execute(urlReviews);
        new LoadTrailers().execute(urlTrailers);
    }

    public void onClickFavourite(View view){

        if( movieEntry.getFavourite() ){
            favouriteIcon.setImageResource(R.drawable.baseline_favorite_border_black_48);
            movieEntry.setFavourite(false);
        } else{
            favouriteIcon.setImageResource(R.drawable.baseline_favorite_black_48);
            movieEntry.setFavourite(true);
        }

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().updateMovie(movieEntry);
            }
        });

    }

    @Override
    public void onClickReview(JSONObject review) {

        Intent intent = new Intent( this, ReviewText.class );

        String reviewText = null;

        try{
            reviewText = review.getString("content");
        }catch (JSONException e){
            e.printStackTrace();
        }

        intent.putExtra("reviewText", reviewText);
        startActivity(intent);
    }

    @Override
    public void onClickTrailer(JSONObject trailer) {

        String key = null;
        try {
            key = trailer.getString("key");
        }catch (JSONException e){
            e.printStackTrace();
        }
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }

    }

    public class LoadReviews extends AsyncTask<URL, Void, JSONArray>{

        @Override
        protected JSONArray doInBackground(URL... urls) {
            if(urls == null) return null;
            URL url = urls[0];

            String rawReviews = null;
            try {
                rawReviews = NetworkUtils.loadDataFromNetwork(url);
            }catch (IOException e){
                e.printStackTrace();
            }
            JSONArray jsonArray = null;
            try {
                jsonArray = (JSONArray) new JSONObject(rawReviews).get("results");
            }catch (JSONException e){
                e.printStackTrace();
            }

            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray reviews) {
            super.onPostExecute(reviews);
            mReviewAdapter.setJSONArray(reviews);
        }

    }

    public class LoadTrailers extends AsyncTask<URL, Void, JSONArray>{

        @Override
        protected JSONArray doInBackground(URL... urls) {
            if(urls == null) return null;
            URL url = urls[0];

            String rawTrailers = null;
            try {
                rawTrailers = NetworkUtils.loadDataFromNetwork(url);
            }catch (IOException e){
                e.printStackTrace();
            }
            JSONArray jsonArray = null;
            try {
                jsonArray = (JSONArray) new JSONObject(rawTrailers).get("results");
            }catch (JSONException e){
                e.printStackTrace();
            }

            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray trailers) {
            super.onPostExecute(trailers);
            mTrailerAdapter.setJSONArray(trailers);
        }

    }
}
