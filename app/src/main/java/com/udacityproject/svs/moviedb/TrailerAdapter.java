package com.udacityproject.svs.moviedb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {

    private JSONArray mTrailers;
    private Context trailerContext;
    private final TrailerOnClickHandler trailerOnClickHandler;

    public TrailerAdapter(TrailerOnClickHandler context){
        trailerOnClickHandler = context;
    }

    public interface TrailerOnClickHandler{
        void onClickTrailer(JSONObject trailer);
    }

    @NonNull
    @Override
    public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        trailerContext = parent.getContext();

        int layoutId = R.layout.trailers_grid_item;
        LayoutInflater inflater = LayoutInflater.from( trailerContext );

        View view = inflater.inflate(layoutId, parent, false);

        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.TrailerHolder holder, int position) {
        holder.button.setText("Trailer" + Integer.toString(position+1));
    }

    @Override
    public int getItemCount(){
        if( mTrailers != null )   return mTrailers.length();
        else return 0;
    }

    public class TrailerHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private Button button;

        public TrailerHolder(View view){
            super(view);
            button = view.findViewById(R.id.bt_trailer_link);
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            JSONObject jsonObject = null;
            try{
                jsonObject = (JSONObject) mTrailers.get( getAdapterPosition() );
            }catch (JSONException e){
                e.printStackTrace();
            }
            trailerOnClickHandler.onClickTrailer(jsonObject);
        }
    }

    public void setJSONArray( JSONArray trailers ){
        mTrailers = trailers;
        notifyDataSetChanged();
    }

}
