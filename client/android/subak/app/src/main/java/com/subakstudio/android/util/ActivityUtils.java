package com.subakstudio.android.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by jinwoomin on 8/6/16.
 */
public class ActivityUtils {
    public static void launchMusicPlayer(Context context, String url) {
        Intent playAudioIntent = new Intent();
        playAudioIntent.setAction(Intent.ACTION_VIEW);
        playAudioIntent.setDataAndType(Uri.parse(url), "audio/*");
        context.startActivity(playAudioIntent);
    }
}
