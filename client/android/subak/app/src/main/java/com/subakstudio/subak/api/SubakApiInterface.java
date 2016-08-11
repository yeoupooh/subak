package com.subakstudio.subak.api;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by jinwoomin on 8/3/16.
 */

public interface SubakApiInterface {
    String ENGINE_TYPE_CHART = "chart";
    String ENGINE_TYPE_SEARCH = "search";

    @GET("/api/engines")
    Call<List<Engine>> getEngines();

    @GET
    Call<ResponseBody> getTracks(@Url String url);
}
