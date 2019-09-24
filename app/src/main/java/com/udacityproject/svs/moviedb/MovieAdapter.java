package com.udacityproject.svs.moviedb;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.udacityproject.svs.moviedb.DatabaseUtils.MovieEntry;
import android.view.View.OnClickListener;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.PosterHolder> {

    private List<MovieEntry> mMovieEntries;
    private Context holderContext;
    private final MovieOnClickHandler movieOnClickHandler;

    public MovieAdapter(  MovieOnClickHandler context){
        movieOnClickHandler = context;
    }

    public interface MovieOnClickHandler{
        void onClickMovie( MovieEntry movieEntry );
    }

    @NonNull
    @Override
    public PosterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        holderContext = parent.getContext();

        int layoutId = R.layout.activity_grid_item;
        LayoutInflater inflater = LayoutInflater.from( holderContext );

        View view = inflater.inflate(layoutId, parent, false);

        return new PosterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterHolder holder, int position) {

        MovieEntry movieEntry = null;

        movieEntry =  mMovieEntries.get(position);

        String posterPath = "http://image.tmdb.org/t/p/w185/" + movieEntry.getPosterPath();

        Log.d("MovieAdapter", posterPath + "\n" );

        Picasso.with( holderContext )
               .load( posterPath )
                .into( holder.imageView );
    }

    @Override
    public int getItemCount(){
        if( mMovieEntries != null )   return mMovieEntries.size();
        else return 0;
    }

    public class PosterHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private ImageView imageView;

        public PosterHolder(View view){
            super(view);
            imageView = view.findViewById( R.id.iv_thumbnail );
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            MovieEntry movieEntry = null;
            movieEntry = mMovieEntries.get( getAdapterPosition() );

            movieOnClickHandler.onClickMovie( movieEntry );

        }
    }

    public void setAdapterList( List<MovieEntry> movieEntries ){
        mMovieEntries = movieEntries;
        notifyDataSetChanged();
    }

}
