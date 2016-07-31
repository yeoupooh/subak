package com.subakstudio.subak.android;

/**
 * Created by jinwoomin on 7/31/16.
 */
public class Item {
    private final String subtitle;
    private final String title;

    public Item(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
