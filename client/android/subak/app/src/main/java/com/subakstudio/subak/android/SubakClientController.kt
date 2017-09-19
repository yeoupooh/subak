package com.subakstudio.subak.android

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast

import com.subakstudio.subak.api.Engine
import com.subakstudio.subak.api.SubakClient
import com.subakstudio.subak.api.Track
import com.subakstudio.subak.api.TrackListResponse

import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by jinwoomin on 8/15/16.
 */

class SubakClientController(private val model: SubakClientModel) {
    private var progressDialog: ProgressDialog? = null
    private val client: SubakClient

    init {

        client = SubakClient(serverBaseUrl)
    }

    private val serverBaseUrl: String
        get() {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(model.activity)
            return sharedPref.getString(SettingsActivity.KEY_SERVER_BASE_URL, "http://localhost:8081")

        }

    private fun showProgressDialog(msg: String) {
        progressDialog = ProgressDialog(model.activity)
        progressDialog!!.setMessage(msg)
        progressDialog!!.show()
    }

    private fun hideProgressDialog() {
        progressDialog!!.hide()
    }

    private fun fetchTrackList(engine: Engine, keyword: String, trackList: MutableList<Track>, trackListAdapter: TrackListAdapter) {
        Log.d(TAG, "getTrackListLegacy: engine=" + engine + ",path=" + engine.path)
        showProgressDialog("Getting tracks...")
        client.getTrackList(engine, keyword)
                // To avoid backpress exception
                .onBackpressureBuffer(10000)
                // To avoid networkonthread exception
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<TrackListResponse>() {
                    override fun onCompleted() {
                        hideProgressDialog()
                        trackListAdapter.notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {
                        hideProgressDialog()
                        showError(e)
                    }

                    override fun onNext(trackListResponse: TrackListResponse) {
                        trackList.clear()
                        trackList.addAll(trackListResponse.tracks)
                    }
                }
                )
    }

    private fun showError(e: Throwable) {
        e.printStackTrace()
        Toast.makeText(model.activity, String.format("%s: %s", e.javaClass.getSimpleName(), e.localizedMessage), Toast.LENGTH_LONG).show()
    }

    fun fetchTrackList(keyword: String) {
        val engine = model.engine

        Log.d(TAG, "getTrackListLegacy: engine=" + engine + ",path=" + engine!!.path)
        showProgressDialog("Getting tracks...")
        client.getTrackList(engine, keyword)
                // To avoid backpress exception
                .onBackpressureBuffer(10000)
                // To avoid networkonthread exception
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<TrackListResponse>() {
                    override fun onCompleted() {
                        hideProgressDialog()
                    }

                    override fun onError(e: Throwable) {
                        hideProgressDialog()
                        showError(e)
                    }

                    override fun onNext(trackListResponse: TrackListResponse) {
                        model.trackList!!.clear()
                        model.trackList!!.addAll(trackListResponse.tracks)
                        model.trackListAdapter!!.notifyDataSetChanged()
                    }
                }
                )
    }

    companion object {
        private val TAG = SubakClientController::class.java!!.getSimpleName()
    }
}
