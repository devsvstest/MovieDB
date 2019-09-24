package com.udacityproject.svs.moviedb.DatabaseUtils;

import java.util.Date;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class MovieEntry {

    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    private int movieId;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    private String title;
    private String overview;
    @ColumnInfo(name = "release_date")
    private Long releaseDate;
    private Double rating;
    private Double popularity;
    private boolean favourite;

    public MovieEntry( int movieId, String posterPath, String title, String overview,
                       Long releaseDate, Double rating, Double popularity, boolean favourite){
        this.movieId = movieId;
        this.posterPath = posterPath;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.popularity = popularity;
        this.favourite = favourite;
    }

    public int getMovieId(){
        return movieId;
    }

    public void setMovie_id(int movieId){
        this.movieId = movieId;
    }

    public String getPosterPath(){
        return posterPath;
    }

    public void setPosterPath(String posterPath){
        this.posterPath = posterPath;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getOverview(){
        return overview;
    }

    public void setOverview(String overview){
        this.overview = overview;
    }

    public Long getReleaseDate(){
        return releaseDate;
    }

    public void setReleaseDate(Long date){
        releaseDate = date;
    }

    public Double getRating(){
        return rating;
    }

    public void setRating(Double rating){
        this.rating = rating;
    }

    public Double getPopularity(){
        return popularity;
    }

    public void setPopularity(Double popularity){
        this.popularity = popularity;
    }

    public boolean getFavourite(){
        return favourite;
    }

    public void setFavourite(boolean favourite){
        this.favourite = favourite;
    }

}
