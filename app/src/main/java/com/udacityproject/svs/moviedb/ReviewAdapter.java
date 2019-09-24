package com.udacityproject.svs.moviedb;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacityproject.svs.moviedb.DatabaseUtils.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private JSONArray mReviews;
    private Context reviewContext;
    private final ReviewOnClickHandler reviewOnClickHandler;

    public ReviewAdapter(  ReviewOnClickHandler context){
        reviewOnClickHandler = context;
    }

    public interface ReviewOnClickHandler{
        void onClickReview( JSONObject review );
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        reviewContext = parent.getContext();

        int layoutId = R.layout.reviews_grid_item;
        LayoutInflater inflater = LayoutInflater.from( reviewContext );

        View view = inflater.inflate(layoutId, parent, false);

        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {

        JSONObject review = null;
        String author = null;
        String content = null;

        try{
            review = mReviews.getJSONObject(position);
            author = review.getString("author");
            content = review.getString("content");
        }catch (JSONException e){
            e.printStackTrace();
        }

        holder.author.setText(author);
        holder.review.setText(content);
    }

    @Override
    public int getItemCount(){
        if( mReviews != null )   return mReviews.length();
        else return 0;
    }

    public class ReviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView author;
        private TextView review;


        public ReviewHolder(View view){
            super(view);
            author = view.findViewById(R.id.tv_author);
            review = view.findViewById(R.id.tv_review);
            view.setOnClickListener(this);
            //review.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            JSONObject jsonObject = null;
            try{
                jsonObject = (JSONObject) mReviews.get( getAdapterPosition() );
            }catch (JSONException e){
                e.printStackTrace();
            }
            reviewOnClickHandler.onClickReview(jsonObject);
        }
    }

    public void setJSONArray( JSONArray reviews ){
        mReviews = reviews;
        notifyDataSetChanged();
    }

}
