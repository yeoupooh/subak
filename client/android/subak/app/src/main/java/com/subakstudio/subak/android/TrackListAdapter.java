package com.subakstudio.subak.android;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.subakstudio.subak.api.Track;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinwoomin on 7/31/16.
 */
public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

    public static final String ACTION_PLAY = "Play";
    public static final String ACTION_DOWNLOAD = "Download";
    private final List<Track> trackList;
    private static final String TAG = TrackListAdapter.class.getSimpleName();
    private ITrackSelectedListener trackSelectedListener;

    public TrackListAdapter(List<Track> trackList) {
        this.trackList = trackList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(v, trackSelectedListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Track track = trackList.get(position);
        holder.title.setText(track.getTrack());
        holder.artist.setText(track.getArtist());
        holder.itemView.setTag(R.string.tag_track, track);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.showContextMenu();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public void setOnTrackSelectedListener(ITrackSelectedListener trackSelectedListener) {
        this.trackSelectedListener = trackSelectedListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        private final ITrackSelectedListener selectedListener;

        @BindView(R.id.textViewTitle)
        TextView title;

        @BindView(R.id.textViewArtist)
        TextView artist;

        public ViewHolder(View view, ITrackSelectedListener selectedListener) {
            super(view);
            this.selectedListener = selectedListener;

            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "click: track=" + getTrack());
            view.showContextMenu();
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle(String.format("%s - %s", getTrack().getArtist(), getTrack().getTrack()));
            addMenuItem(contextMenu, view, ACTION_PLAY);
            addMenuItem(contextMenu, view, ACTION_DOWNLOAD);
        }

        private Track getTrack() {
            return (Track) itemView.getTag(R.string.tag_track);
        }

        private void addMenuItem(ContextMenu contextMenu, View view, String title) {
            MenuItem menuItem = contextMenu.add(Menu.NONE, view.getId(), Menu.NONE, title);
            menuItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Log.d(TAG, "menu item click: " + menuItem);
            Track track = getTrack();
            Log.d(TAG, "click: track=" + track);
            selectedListener.onAction(menuItem.getTitle().toString(), track);
            return true;
        }
    }
}
