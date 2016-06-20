package com.chrisblackledge.popularmoviesthesequel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"total_results", "total_pages", "page"})
public class MovieParcel_List implements Parcelable {

    @JsonProperty("results")
    private List<MovieParcel_Content> mMovieParcels = new ArrayList<MovieParcel_Content>();;

    public MovieParcel_List() {
    }

    public MovieParcel_List(List<MovieParcel_Content> movieParcels) {
        this.mMovieParcels = movieParcels;
    }

    private MovieParcel_List(Parcel in) {
        in.readTypedList(mMovieParcels, MovieParcel_Content.CREATOR);
    }

    public List<MovieParcel_Content> getMovieParcels() {
        return mMovieParcels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return String.valueOf(mMovieParcels.size());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(mMovieParcels);
    }

    public static final Creator<MovieParcel_List> CREATOR = new Creator<MovieParcel_List>() {
        @Override
        public MovieParcel_List createFromParcel(Parcel parcel) {
            return new MovieParcel_List(parcel);
        }

        @Override
        public MovieParcel_List[] newArray(int i) {
            return new MovieParcel_List[i];
        }
    };
}