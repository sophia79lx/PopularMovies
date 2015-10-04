package com.androidnano.sophialu.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sophia.lu on 9/26/15.
 */
public class MovieReviewData implements Parcelable {

    String reviewAuthor;
    String reviewContent;

    public MovieReviewData(String mReviewAuthor, String mReviewContent){
        this.reviewAuthor = mReviewAuthor;
        this.reviewContent = mReviewContent;
    }

    private MovieReviewData(Parcel in){
        reviewAuthor = in.readString();
        reviewContent = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return reviewAuthor + "--" + reviewContent ;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewAuthor);
        dest.writeString(reviewContent);
    }


    public final Parcelable.Creator<MovieReviewData> CREATOR = new Parcelable.Creator<MovieReviewData>() {
        @Override
        public MovieReviewData createFromParcel(Parcel parcel) {
            return new MovieReviewData(parcel);
        }

        @Override
        public MovieReviewData[] newArray(int i) {
            return new MovieReviewData[i];
        }

    };
}
