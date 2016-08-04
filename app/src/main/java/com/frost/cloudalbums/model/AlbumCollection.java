package com.frost.cloudalbums.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AlbumCollection {

    @SerializedName("collection")
    private List<Album> albumList = new ArrayList<>();
    @SerializedName("next_href")
    private String nextHref;

    public List<Album> getAlbumList() {
        return albumList;
    }

    public String getNextHref() {
        return nextHref;
    }
}
