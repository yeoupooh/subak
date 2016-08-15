package com.subakstudio.subak.android;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.subakstudio.subak.api.Engine;
import com.subakstudio.subak.api.SubakClient;
import com.subakstudio.subak.api.Track;
import com.subakstudio.subak.api.TrackListResponse;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jinwoomin on 8/15/16.
 */

public class SubakClientController {
    private static final String TAG = SubakClientController.class.getSimpleName();
    private SubakClientModel model;
    private ProgressDialog progressDialog;
    private SubakClient client;

    public SubakClientController(SubakClientModel model) {
        this.model = model;

        client = new SubakClient(getServerBaseUrl());
    }

    private String getServerBaseUrl() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(model.getActivity());
        return sharedPref.getString(SettingsActivity.KEY_SERVER_BASE_URL, "http://localhost:8081");

    }

    private void showProgressDialog(String msg) {
        progressDialog = new ProgressDialog(model.getActivity());
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.hide();
    }

    private void fetchTrackList(Engine engine, String keyword, final List<Track> trackList, final TrackListAdapter trackListAdapter) {
        Log.d(TAG, "getTrackListLegacy: engine=" + engine + ",path=" + engine.getPath());
        showProgressDialog("Getting tracks...");
        client.getTrackList(engine, keyword)
                // To avoid backpress exception
                .onBackpressureBuffer(10000)
                // To avoid networkonthread exception
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TrackListResponse>() {
                               @Override
                               public void onCompleted() {
                                   hideProgressDialog();
                                   trackListAdapter.notifyDataSetChanged();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   hideProgressDialog();
                                   showError(e);
                               }

                               @Override
                               public void onNext(TrackListResponse trackListResponse) {
                                   trackList.clear();
                                   trackList.addAll(trackListResponse.getTracks());
                               }
                           }
                );
    }

    private void showError(Throwable e) {
        e.printStackTrace();
        Toast.makeText(model.getActivity(), String.format("%s: %s", e.getClass().getSimpleName(), e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
    }

    public void fetchTrackList(String keyword) {
        Engine engine = model.getEngine();

        Log.d(TAG, "getTrackListLegacy: engine=" + engine + ",path=" + engine.getPath());
        showProgressDialog("Getting tracks...");
        client.getTrackList(engine, keyword)
                // To avoid backpress exception
                .onBackpressureBuffer(10000)
                // To avoid networkonthread exception
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TrackListResponse>() {
                               @Override
                               public void onCompleted() {
                                   hideProgressDialog();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   hideProgressDialog();
                                   showError(e);
                               }

                               @Override
                               public void onNext(TrackListResponse trackListResponse) {
                                   model.getTrackList().clear();
                                   model.getTrackList().addAll(trackListResponse.getTracks());
                                   model.getTrackListAdapter().notifyDataSetChanged();
                               }
                           }
                );
    }
}
