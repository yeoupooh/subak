package com.subakstudio.subak.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by jinwoomin on 8/3/16.
 */

interface SubakApiInterface {

    @get:GET("/api/engines")
    val engines: Call<List<Engine>>

    @GET
    fun getTracks(@Url url: String): Call<ResponseBody>

    companion object {
        val ENGINE_TYPE_CHART = "chart"
        val ENGINE_TYPE_SEARCH = "search"
    }
}
