package com.chrisblackledge.popularmoviesthesequel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"adult", "genre_ids", "original_language", "title", "backdrop_path", "popularity", "vote_count", "video"})
public class MovieParcel_Content implements Parcelable {

    @JsonProperty("original_title")
    private String mOriginalTitle = "";

    @JsonProperty("poster_path")
    private String mImagePosterUrl = "";

    @JsonProperty("overview")
    private String mOverview = "";

    @JsonProperty("vote_average")
    private double mUserRating = 0.0;

    @JsonProperty("release_date")
    private String mReleaseDate = "";

    @JsonProperty("id")
    private int mId = 0;

    // support offline image poster in favorites
    private byte[] mImagePoster = new byte[0];

    public MovieParcel_Content() {
    }

    public MovieParcel_Content(String title,
                               String posterUrl,
                               String overview,
                               double rating,
                               String releaseDate,
                               byte[] imagePoster,
                               int id) {
        this.mOriginalTitle = title;
        this.mImagePosterUrl = posterUrl;
        this.mOverview = overview;
        this.mUserRating = rating;
        this.mReleaseDate = releaseDate;
        this.mImagePoster = imagePoster;
        this.mId = id;
    }

    private MovieParcel_Content(Parcel in) {
        this.mOriginalTitle = in.readString();
        this.mImagePosterUrl = in.readString();
        this.mOverview = in.readString();
        this.mUserRating = in.readDouble();
        this.mReleaseDate = in.readString();

        mImagePoster = new byte[in.readInt()];
        in.readByteArray(mImagePoster);

        this.mId = in.readInt();
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getImagePosterURL() {
        return mImagePosterUrl;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getUserRating() {
        return mUserRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getId() {
        return mId;
    }

    public byte[] getImagePoster() {
        return mImagePoster;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return mOriginalTitle + "--" +
                mImagePosterUrl + "--" +
                mOverview + "--" +
                mUserRating + "--" +
                mReleaseDate + "--" +
                mId; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mImagePosterUrl);
        parcel.writeString(mOverview);
        parcel.writeDouble(mUserRating);
        parcel.writeString(mReleaseDate);

        parcel.writeInt(mImagePoster.length);
        parcel.writeByteArray(mImagePoster);

        parcel.writeInt(mId);
    }

    public static final Parcelable.Creator<MovieParcel_Content> CREATOR = new Parcelable.Creator<MovieParcel_Content>() {
        @Override
        public MovieParcel_Content createFromParcel(Parcel parcel) {
            return new MovieParcel_Content(parcel);
        }

        @Override
        public MovieParcel_Content[] newArray(int i) {
            return new MovieParcel_Content[i];
        }
    };
}