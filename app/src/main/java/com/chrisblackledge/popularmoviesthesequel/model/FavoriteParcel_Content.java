package com.chrisblackledge.popularmoviesthesequel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FavoriteParcel_Content implements Parcelable {

    private String mOriginalTitle = "";
    private String mOverview = "";
    private String mReleaseDate = "";
    private double mUserRating = 0.0;
    private byte[] mImagePoster = null;
    private int mId = 0;

    public FavoriteParcel_Content() {
    }

    public FavoriteParcel_Content(String title,
                                  String overview,
                                  String releaseDate,
                                  double userRating,
                                  byte[] posterImg,
                                  int id) {
        this.mOriginalTitle = title;
        this.mOverview = overview;
        this.mReleaseDate = releaseDate;
        this.mUserRating = userRating;
        this.mImagePoster = posterImg;
        this.mId = id;
    }

    private FavoriteParcel_Content(Parcel in) {
        this.mOriginalTitle = in.readString();
        this.mOverview = in.readString();
        this.mReleaseDate = in.readString();
        this.mUserRating = in.readDouble();

        mImagePoster = new byte[in.readInt()];
        in.readByteArray(mImagePoster);

        this.mId = in.readInt();
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public double getUserRating() {
        return mUserRating;
    }

    public byte[] getImagePoster() {
        return mImagePoster;
    }

    public int getId() {
        return mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return mOriginalTitle + "--" +
                mOverview + "--" +
                mReleaseDate + "--" +
                mUserRating + "--" +
                mId; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mOverview);
        parcel.writeString(mReleaseDate);
        parcel.writeDouble(mUserRating);
        parcel.writeInt(mImagePoster.length);
        parcel.writeByteArray(mImagePoster);
        parcel.writeInt(mId);
    }

    public static final Creator<FavoriteParcel_Content> CREATOR = new Creator<FavoriteParcel_Content>() {
        @Override
        public FavoriteParcel_Content createFromParcel(Parcel parcel) {
            return new FavoriteParcel_Content(parcel);
        }

        @Override
        public FavoriteParcel_Content[] newArray(int i) {
            return new FavoriteParcel_Content[i];
        }

    };
}