package com.frost.cloudalbums.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class User extends RealmObject {

    @PrimaryKey
    private Integer id;
    private String username;
    @SerializedName("avatar_url")
    private String avatarUrl;
    private String country;
    private String description;
    private String city;
    @SerializedName("track_count")
    private Integer trackCount;
    @SerializedName("playlist_count")
    private Integer playlistCount;
    @SerializedName("followers_count")
    private Integer followersCount;
    @SerializedName("followings_count")
    private Integer followingsCount;

    public User() {}


    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public Integer getFollowingsCount() {
        return followingsCount;
    }

    public Integer getTrackCount() {
        return trackCount;
    }

    public Integer getPlaylistCount() {
        return playlistCount;
    }
}
