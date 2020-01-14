package com.codingwithmitch.audiostreamer.api;



import com.codingwithmitch.audiostreamer.pojo.SongResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("music.json")
    Call<SongResponse> getSongs();
}
