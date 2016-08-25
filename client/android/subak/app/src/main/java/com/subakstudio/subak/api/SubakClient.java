package com.subakstudio.subak.api;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by jinwoomin on 8/6/16.
 */
public class SubakClient {

    private static final String TAG = SubakClient.class.getSimpleName();
    private final String serverBaseUrl;

    private SubakApiInterface getService() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(SubakApiInterface.class);
    }

    public SubakClient(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }

    public Observable<Engine> getEngineList() {
        return Observable.create(new Observable.OnSubscribe<Engine>() {
            @Override
            public void call(final Subscriber<? super Engine> subscriber) {
                Call<List<Engine>> call = getService().getEngines();
                List<Engine> engines = null;
                try {
                    engines = call.execute().body();
                    for (Engine engine : engines) {
                        subscriber.onNext(engine);
                    }
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<TrackListResponse> getTrackList(final Engine engine, final String keyword) {
        return Observable.create(new Observable.OnSubscribe<TrackListResponse>() {
            @Override
            public void call(final Subscriber<? super TrackListResponse> subscriber) {
                String path = engine.getPath();
                if (engine.getType().equals(SubakApiInterface.ENGINE_TYPE_SEARCH)) {
                    path = path.replace(":keyword", keyword);
                }
                Call<ResponseBody> call = getService().getTracks(path);
                try {
                    Response<ResponseBody> response = call.execute();
                    if (response != null) {
                        String trackListJson = response.body().string();
                        Log.d(TAG, "res:" + trackListJson);
                        ObjectMapper mapper = new ObjectMapper();
                        subscriber.onNext(mapper.readValue(trackListJson, TrackListResponse.class));
                        subscriber.onCompleted();
                    }
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}
