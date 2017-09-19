package com.subakstudio.subak.android

import android.app.Activity

import com.subakstudio.subak.api.Engine
import com.subakstudio.subak.api.Track

/**
 * Created by jinwoomin on 8/15/16.
 */

class SubakClientModel {
    var activity: Activity? = null
    var trackList: List<Track>? = null
    var trackListAdapter: TrackListAdapter? = null
    var engine: Engine? = null
}
