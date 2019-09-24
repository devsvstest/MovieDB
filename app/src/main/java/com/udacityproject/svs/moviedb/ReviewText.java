package com.udacityproject.svs.moviedb;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacityproject.svs.moviedb.NetworkUtils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewText extends AppCompatActivity {
    private TextView mReviewText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.review_text);
        mReviewText = findViewById(R.id.tv_review_texts);

        Intent intent = getIntent();

        if( intent.hasExtra("reviewText" ) ) {

            String reviewText = intent.getStringExtra("reviewText");

            mReviewText.setText(reviewText);
        }

    }

}
