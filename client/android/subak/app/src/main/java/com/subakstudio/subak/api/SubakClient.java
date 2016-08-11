package com.subakstudio.subak.api;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by jinwoomin on 8/6/16.
 */
public class SubakClient {

    private static final String TAG = SubakClient.class.getSimpleName();
    private final String serverBaseUrl;
    private ISubakListener listener;

    public interface ISubakListener {
        void onRequest();

        void onSuccess(Object result);

        void onError(Throwable throwable);

        void onFinally();
    }

    private SubakApiInterface getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(SubakApiInterface.class);
    }

    public SubakClient(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }

    public void setSubackListener(ISubakListener listener) {
        this.listener = listener;
    }

    public void getEngineList() {
        if (listener != null) {
            listener.onRequest();
        }
        Call<List<Engine>> call = getService().getEngines();
        call.enqueue(new Callback<List<Engine>>() {
            @Override
            public void onResponse(Call<List<Engine>> call, Response<List<Engine>> response) {
                Log.d(TAG, "onResponse:" + response.body().size());
                if (listener != null) {
                    listener.onSuccess(response.body());
                    listener.onFinally();
                }
            }

            @Override
            public void onFailure(Call<List<Engine>> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
                if (listener != null) {
                    listener.onError(t);
                    listener.onFinally();
                }
            }
        });
    }

    public void getTrackList(Engine engine, String keyword) {
        if (listener != null) {
            listener.onRequest();
        }
        String path = engine.getPath();
        if (engine.getType().equals(SubakApiInterface.ENGINE_TYPE_SEARCH)) {
            path = path.replace(":keyword", keyword);
        }
        Call<ResponseBody> call = getService().getTracks(path);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse:" + response.body());
                if (listener != null) {
                    try {
                        if (response.body() != null) {
                            String trackListJson = response.body().string();
                            Log.d(TAG, "res:" + trackListJson);
                            ObjectMapper mapper = new ObjectMapper();
                            listener.onSuccess(mapper.readValue(trackListJson, TrackListResponse.class));
                        } else {
                            listener.onSuccess("{}");
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    listener.onFinally();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
                if (listener != null) {
                    listener.onError(t);
                    listener.onFinally();
                }
            }
        });
    }


}
