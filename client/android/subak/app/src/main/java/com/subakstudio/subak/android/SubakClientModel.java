package com.subakstudio.subak.android;

import android.app.Activity;

import com.subakstudio.subak.api.Engine;
import com.subakstudio.subak.api.Track;

import java.util.List;

/**
 * Created by jinwoomin on 8/15/16.
 */

public class SubakClientModel {
    private Activity activity;
    private List<Track> trackList;
    private TrackListAdapter trackListAdapter;
    private Engine engine;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public TrackListAdapter getTrackListAdapter() {
        return trackListAdapter;
    }

    public void setTrackList(List<Track> trackList) {
        this.trackList = trackList;
    }

    public void setTrackListAdapter(TrackListAdapter trackListAdapter) {
        this.trackListAdapter = trackListAdapter;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
