package com.subakstudio.subak.api

import android.util.Log

import com.fasterxml.jackson.databind.ObjectMapper

import java.io.IOException
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import rx.Observable
import rx.Subscriber

/**
 * Created by jinwoomin on 8/6/16.
 */
class SubakClient(private val serverBaseUrl: String) {

    private val service: SubakApiInterface
        get() {
            val client = OkHttpClient().newBuilder()
                    .readTimeout(1, TimeUnit.MINUTES)
                    .connectTimeout(1, TimeUnit.MINUTES).build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(serverBaseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(client)
                    .build()
            return retrofit.create(SubakApiInterface::class.java!!)
        }

    val engineList: Observable<Engine>
        get() = Observable.create { subscriber ->
            val call = service.engines
            var engines: List<Engine>? = null
            try {
                engines = call.execute().body()
                for (engine in engines!!) {
                    subscriber.onNext(engine)
                }
                subscriber.onCompleted()
            } catch (e: IOException) {
                subscriber.onError(e)
            }
        }

    fun getTrackList(engine: Engine, keyword: String): Observable<TrackListResponse> {
        return Observable.create { subscriber ->
            var path = engine.path
            if (engine.type == SubakApiInterface.ENGINE_TYPE_SEARCH) {
                path = path!!.replace(":keyword", keyword)
            }
            val call = service.getTracks(path)
            try {
                val response = call.execute()
                if (response != null) {
                    val trackListJson = response.body().string()
                    Log.d(TAG, "res:" + trackListJson)
                    val mapper = ObjectMapper()
                    subscriber.onNext(mapper.readValue<TrackListResponse>(trackListJson, TrackListResponse::class.java!!))
                    subscriber.onCompleted()
                }
            } catch (e: IOException) {
                subscriber.onError(e)
            }
        }
    }

    companion object {

        private val TAG = SubakClient::class.java!!.getSimpleName()
    }

}
