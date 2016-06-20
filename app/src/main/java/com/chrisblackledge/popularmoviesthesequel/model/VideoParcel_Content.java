package com.chrisblackledge.popularmoviesthesequel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"id", "iso_639_1", "iso_3166_1", "site", "size", "type"})
public class VideoParcel_Content implements Parcelable {

    @JsonProperty("key")
    private String mKey = "";

    @JsonProperty("name")
    private String mName = "";

    public VideoParcel_Content() {
    }

    public VideoParcel_Content(String key,
                               String name) {
        this.mKey = key;
        this.mName = name;
    }

    private VideoParcel_Content(Parcel in) {
        this.mKey = in.readString();
        this.mName = in.readString();
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return mKey + "--" +
                mName; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mKey);
        parcel.writeString(mName);
    }

    public static final Creator<VideoParcel_Content> CREATOR = new Creator<VideoParcel_Content>() {
        @Override
        public VideoParcel_Content createFromParcel(Parcel parcel) {
            return new VideoParcel_Content(parcel);
        }

        @Override
        public VideoParcel_Content[] newArray(int i) {
            return new VideoParcel_Content[i];
        }
    };
}