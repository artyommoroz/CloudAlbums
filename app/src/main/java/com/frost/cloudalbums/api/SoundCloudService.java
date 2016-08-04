package com.frost.cloudalbums.api;


import com.frost.cloudalbums.model.AccessToken;
import com.frost.cloudalbums.model.AlbumCollection;
import com.frost.cloudalbums.model.TrackCollection;
import com.frost.cloudalbums.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SoundCloudService {

    String BASE_URL = "https://api-v2.soundcloud.com/";
    String BASE_URL_OLD = "https://api.soundcloud.com/";

    @GET("search/albums?client_id=f4fd8e3fdb875f6be821dc3fa9f75b29&q=*&limit=100")
    Call<AlbumCollection> loadAlbums(@Query("filter.genre_or_tag") String genre);


    @GET("search/tracks?client_id=f4fd8e3fdb875f6be821dc3fa9f75b29&limit=100")
    Call<TrackCollection> searchTracks(@Query("q") String query);

    @POST("oauth2/token?grant_type=password&client_id=f4fd8e3fdb875f6be821dc3fa9f75b29" +
            "&client_secret=e560f821093765e67e6bd1fec8b9430f&scope=non-expiring")
    Call<AccessToken> getToken(@Query("username") String username,
                               @Query("password") String password);

    @GET("me?")
    Call<User> getProfileInfo(@Query("oauth_token") String token);
}
