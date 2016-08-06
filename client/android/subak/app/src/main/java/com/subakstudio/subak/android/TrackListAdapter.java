package com.subakstudio.subak.android;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.subakstudio.android.util.ActivityUtils;
import com.subakstudio.subak.api.Track;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinwoomin on 7/31/16.
 */
public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

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
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public void setOnTrackSelectedListener(ITrackSelectedListener trackSelectedListener) {
        this.trackSelectedListener = trackSelectedListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        }

        @Override
        public void onClick(View view) {
            Track track = (Track) itemView.getTag(R.string.tag_track);
            Log.d(TAG, "click: track=" + track);
//            EventBus.getDefault().post(new BookSelectedEvent(
//                    (Long)view.getTag(R.string.tag_book_id),
//                    (String)view.getTag(R.string.tag_book_title)));
            if (selectedListener != null) {
                selectedListener.onSelect(track);
            }
        }
    }
}
