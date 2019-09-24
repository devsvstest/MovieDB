package com.udacityproject.svs.moviedb.NetworkUtils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "9adb3a295bb4b0a0d4bf598771477e37";
    private static final String QUERY_KEY_API = "api_key";
    private static final String QUERY_KEY_PAGE = "page";

    public static URL buildURL( String sort_by, int page ){

        Uri uri = Uri.parse(BASE_URL + sort_by )
                .buildUpon().appendQueryParameter(QUERY_KEY_API, API_KEY)
                .appendQueryParameter(QUERY_KEY_PAGE, Integer.toString(page) )
                .build();

        URL url = null;
        try{
            url = new URL( uri.toString() );
        }catch ( MalformedURLException e ){
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildReviewURL( String movieId, int page ){

        Uri uri = Uri.parse(BASE_URL + movieId )
                .buildUpon().appendEncodedPath("reviews")
                .appendQueryParameter(QUERY_KEY_API, API_KEY)
                .appendQueryParameter(QUERY_KEY_PAGE, Integer.toString(page) )
                .build();

        URL url = null;
        try{
            url = new URL( uri.toString() );
        }catch ( MalformedURLException e ){
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildTrailerURL( String movieId, int page ){

        Uri uri = Uri.parse(BASE_URL + movieId )
                .buildUpon().appendEncodedPath("videos")
                .appendQueryParameter(QUERY_KEY_API, API_KEY)
                .appendQueryParameter(QUERY_KEY_PAGE, Integer.toString(page) )
                .build();

        URL url = null;
        try{
            url = new URL( uri.toString() );
        }catch ( MalformedURLException e ){
            e.printStackTrace();
        }

        return url;
    }

    public static String loadDataFromNetwork( URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try{
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasNext = scanner.hasNext();

            if( hasNext ){
                return scanner.next();
            }else return  null;

        }finally {
            urlConnection.disconnect();
        }

    }

}
