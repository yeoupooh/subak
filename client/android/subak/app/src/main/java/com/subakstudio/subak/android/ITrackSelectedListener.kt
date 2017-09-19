package com.subakstudio.subak.android

import com.subakstudio.subak.api.Track

/**
 * Created by jinwoomin on 8/6/16.
 */
interface ITrackSelectedListener {
    fun onAction(action: String, track: Track)
}
