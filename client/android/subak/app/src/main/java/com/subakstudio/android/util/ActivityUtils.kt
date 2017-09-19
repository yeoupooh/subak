package com.subakstudio.android.util

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by jinwoomin on 8/6/16.
 */
object ActivityUtils {
    fun launchMusicPlayer(context: Context, url: String) {
        val playAudioIntent = Intent()
        playAudioIntent.action = Intent.ACTION_VIEW
        playAudioIntent.setDataAndType(Uri.parse(url), "audio/*")
        context.startActivity(playAudioIntent)
    }
}
