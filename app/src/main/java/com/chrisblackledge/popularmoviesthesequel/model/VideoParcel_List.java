package com.chrisblackledge.popularmoviesthesequel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class VideoParcel_List implements Parcelable {

    @JsonProperty("id")
    private int mId;

    @JsonProperty("results")
    private List<VideoParcel_Content> mVideoParcels = new ArrayList<VideoParcel_Content>();;

    public VideoParcel_List() {
    }

    public VideoParcel_List(int id,
                            List<VideoParcel_Content> videoParcels) {
        this.mId = id;
        this.mVideoParcels = videoParcels;
    }

    private VideoParcel_List(Parcel in) {
        this.mId = in.readInt();
        in.readTypedList(mVideoParcels, VideoParcel_Content.CREATOR);
    }

    public int getId() {
        return mId;
    }

    public List<VideoParcel_Content> getVideoParcels() {
        return mVideoParcels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return mId + "--" +
                mVideoParcels.size();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeTypedList(mVideoParcels);
    }

    public static final Creator<VideoParcel_List> CREATOR = new Creator<VideoParcel_List>() {
        @Override
        public VideoParcel_List createFromParcel(Parcel parcel) {
            return new VideoParcel_List(parcel);
        }

        @Override
        public VideoParcel_List[] newArray(int i) {
            return new VideoParcel_List[i];
        }
    };
}