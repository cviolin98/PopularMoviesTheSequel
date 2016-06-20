package com.chrisblackledge.popularmoviesthesequel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"id", "url"})
public class ReviewParcel_Content implements Parcelable {

    @JsonProperty("author")
    private String mAuthor = "";

    @JsonProperty("content")
    private String mContent = "";

    public ReviewParcel_Content() {
    }

    public ReviewParcel_Content(String author,
                                String content) {
        this.mAuthor = author;
        this.mContent = content;
    }

    private ReviewParcel_Content(Parcel in) {
        this.mAuthor = in.readString();
        this.mContent = in.readString();
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return mAuthor + "--" +
                mContent; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAuthor);
        parcel.writeString(mContent);
    }

    public static final Creator<ReviewParcel_Content> CREATOR = new Creator<ReviewParcel_Content>() {
        @Override
        public ReviewParcel_Content createFromParcel(Parcel parcel) {
            return new ReviewParcel_Content(parcel);
        }

        @Override
        public ReviewParcel_Content[] newArray(int i) {
            return new ReviewParcel_Content[i];
        }
    };
}