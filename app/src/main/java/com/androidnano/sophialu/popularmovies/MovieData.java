package com.androidnano.sophialu.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sophia.lu on 8/3/15.
 */
public class MovieData implements Parcelable{

    int movieId;
    String movieTitle;
    String moviePosterPath;
    String movieReleaseDate;
    String movieOverview;
    String movieVoteAverage;

    public MovieData(int mId, String mTitle, String mPosterPath, String mReleaseDate, String mOverview, String mVoteAverage)
    {
        this.movieId = mId;
        this.movieTitle = mTitle;
        this.moviePosterPath = mPosterPath;
        this.movieReleaseDate = mReleaseDate;
        this.movieOverview = mOverview;
        this.movieVoteAverage = mVoteAverage;
    }


    private MovieData(Parcel in){
        movieId = in.readInt();
        movieTitle = in.readString();
        moviePosterPath = in.readString();
        movieReleaseDate = in.readString();
        movieOverview = in.readString();
        movieVoteAverage = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return movieId + "--" + movieTitle + "--" + moviePosterPath + "--" + movieReleaseDate + "--" + movieOverview + "--" + movieVoteAverage ;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(movieTitle);
        dest.writeString(moviePosterPath);
        dest.writeString(movieReleaseDate);
        dest.writeString(movieOverview);
        dest.writeString(movieVoteAverage);
    }


    public final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel parcel) {
            return new MovieData(parcel);
        }

        @Override
        public MovieData[] newArray(int i) {
            return new MovieData[i];
        }

    };
}
