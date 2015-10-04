package com.androidnano.sophialu.popularmovies;

        import android.os.Parcel;
        import android.os.Parcelable;

/**
 * Created by sophia.lu on 9/20/15.
 */
public class MovieTrailerData implements Parcelable {

    String trailerName;
    String trailerSource;

    public MovieTrailerData(String mTrailerName, String mTrailerSource){
        this.trailerName = mTrailerName;
        this.trailerSource = mTrailerSource;
    }


    private MovieTrailerData(Parcel in){
        trailerName = in.readString();
        trailerSource = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return trailerName + "--" + trailerSource ;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailerName);
        dest.writeString(trailerSource);
    }


    public static final Parcelable.Creator<MovieTrailerData> CREATOR = new Parcelable.Creator<MovieTrailerData>() {
        @Override
        public MovieTrailerData createFromParcel(Parcel parcel) {
            return new MovieTrailerData(parcel);
        }

        @Override
        public MovieTrailerData[] newArray(int i) {
            return new MovieTrailerData[i];
        }

    };
}
