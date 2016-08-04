package com.frost.cloudalbums.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class TrackCollection {

    @SerializedName("collection")
    private List<Track> trackList = new ArrayList<>();
    @SerializedName("next_href")
    private String nextHref;

    public List<Track> getTrackList() {
        return trackList;
    }

    public String getNextHref() {
        return nextHref;
    }
}
