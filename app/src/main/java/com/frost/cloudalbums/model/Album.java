package com.frost.cloudalbums.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Album extends RealmObject {

    @PrimaryKey
    private Integer id;
    @SerializedName("artwork_url")
    private String artworkUrl;
    private String genre;
    @SerializedName("tag_list")
    private String tagList;
    private String title;
    @SerializedName("track_count")
    private Integer trackCount;
    private User user;
    @SerializedName("tracks")
    private RealmList<Track> trackList = new RealmList<>();

    public Album() {}

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public User getUser() {
        return user;
    }

    public RealmList<Track> getTrackList() {
        return trackList;
    }

    public Integer getTrackCount() {
        return trackCount;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTagList() {
        return tagList;
    }
}
