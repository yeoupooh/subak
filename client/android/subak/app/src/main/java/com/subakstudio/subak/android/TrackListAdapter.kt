package com.subakstudio.subak.android

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.subakstudio.subak.api.Track

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by jinwoomin on 7/31/16.
 */
class TrackListAdapter(private val trackList: List<Track>) : RecyclerView.Adapter<TrackListAdapter.ViewHolder>() {
    private var trackSelectedListener: ITrackSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false)

        return ViewHolder(v, trackSelectedListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = trackList[position]
        holder.title!!.text = track.track
        holder.artist!!.text = track.artist
        holder.itemView.setTag(R.string.tag_track, track)
        holder.itemView.setOnLongClickListener { view ->
            view.showContextMenu()
            false
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun setOnTrackSelectedListener(trackSelectedListener: ITrackSelectedListener) {
        this.trackSelectedListener = trackSelectedListener
    }

    class ViewHolder(view: View, private val selectedListener: ITrackSelectedListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        @BindView(R.id.textViewTitle)
        internal var title: TextView? = null

        @BindView(R.id.textViewArtist)
        internal var artist: TextView? = null

        init {

            ButterKnife.bind(this, view)
            view.setOnClickListener(this)
            view.setOnCreateContextMenuListener(this)
        }

        override fun onClick(view: View) {
            Log.d(TAG, "click: track=" + track)
            view.showContextMenu()
        }

        override fun onCreateContextMenu(contextMenu: ContextMenu, view: View, contextMenuInfo: ContextMenu.ContextMenuInfo) {
            contextMenu.setHeaderTitle(String.format("%s - %s", track.artist, track.track))
            addMenuItem(contextMenu, view, ACTION_PLAY)
            addMenuItem(contextMenu, view, ACTION_SEARCH_VIA)
            addMenuItem(contextMenu, view, ACTION_DOWNLOAD)
        }

        private val track: Track
            get() = itemView.getTag(R.string.tag_track) as Track

        private fun addMenuItem(contextMenu: ContextMenu, view: View, title: String) {
            val menuItem = contextMenu.add(Menu.NONE, view.id, Menu.NONE, title)
            menuItem.setOnMenuItemClickListener(this)
        }

        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
            Log.d(TAG, "menu item click: " + menuItem)
            val track = track
            Log.d(TAG, "click: track=" + track)
            selectedListener.onAction(menuItem.title.toString(), track)
            return true
        }
    }

    companion object {

        val ACTION_PLAY = "Play"
        val ACTION_SEARCH_VIA = "Search..."
        val ACTION_DOWNLOAD = "Download"
        private val TAG = TrackListAdapter::class.java!!.getSimpleName()
    }
}
