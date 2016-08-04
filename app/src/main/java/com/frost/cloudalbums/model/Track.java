package com.frost.cloudalbums.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;


public class Track extends RealmObject {


    @SerializedName("duration")
    private int duration;
    @SerializedName("title")
    private String title;
    @SerializedName("artwork_url")
    private String artworkUrl;

    public Track() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public int getDuration() {
        return duration;
    }
}
