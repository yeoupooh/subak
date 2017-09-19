package com.subakstudio.subak.android

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.subakstudio.android.util.ActivityUtils
import com.subakstudio.subak.api.Engine
import com.subakstudio.subak.api.SubakApiInterface
import com.subakstudio.subak.api.SubakClient
import com.subakstudio.subak.api.Track
import com.subakstudio.subak.api.TrackListResponse

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnEditorAction
import butterknife.OnItemSelected
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.functions.Func2
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    // Automatically finds each field by the specified ID.
    //    @BindView(R.id.textViewHelloWorld)
    //    TextView textViewHelloWorld;

    @BindView(R.id.recyclerViewItems)
    internal var recyclerViewItems: RecyclerView? = null

    private var trackListAdapter: TrackListAdapter? = null
    private var trackList: ArrayList<Track>? = null

    @BindView(R.id.editText)
    internal var editTextSearch: EditText? = null

    @BindView(R.id.spinner)
    internal var spinnerEngines: Spinner? = null
    internal var spinnerEnginesDataAdapter: ArrayAdapter<Engine>
    internal var engines: MutableList<Engine>
    private var progressDialog: ProgressDialog? = null
    private var client: SubakClient? = null
    private var dm: DownloadManager? = null
    private var enqueue: Long = 0
    private var keptTrack: Track? = null

    @OnItemSelected(R.id.spinner)
    fun spinnerItemSelected(spinner: Spinner, position: Int) {
        // code here
        Log.d(TAG, "onitemselected: path=" + engines[position].path!!)
    }

    @OnEditorAction(R.id.editText)
    fun editTextSearchAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        Log.d(TAG, "onEditorAction: ")
        getTrackList(spinnerEngines!!.selectedItem as Engine)
        return true
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        setUpSubakClient()
        setUpTrackList()
        setUpEnginesSpinner()

        getEngineList()
    }

    private fun setUpSubakClient() {
        client = SubakClient(serverBaseUrl)
    }

    private fun setUpEnginesSpinner() {
        engines = ArrayList()
        spinnerEnginesDataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, engines)
        spinnerEngines!!.adapter = spinnerEnginesDataAdapter
        // Specify the layout to use when the list of choices appears
        spinnerEnginesDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEngines!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                Log.d(TAG, "item:" + parent.getItemAtPosition(pos))
                if (parent.getItemAtPosition(pos) is Engine) {
                    selectEngine(parent.getItemAtPosition(pos) as Engine)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d(TAG, "item nothing: " + parent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                downloadTrack(keptTrack)
            } else {
                // Permission Denied
                Toast.makeText(this@MainActivity, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                        .show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun setUpTrackList() {
        recyclerViewItems!!.layoutManager = LinearLayoutManager(this)
        trackList = ArrayList()
        // FIXME remove dummy data
        for (i in 0..99) {
            val track = Track()
            track.track = "track" + i
            track.artist = "artist" + i
            trackList!!.add(track)
        }
        trackListAdapter = TrackListAdapter(trackList)
        val activity = this
        trackListAdapter!!.setOnTrackSelectedListener(ITrackSelectedListener { action, track ->
            Log.d(TAG, "onaction:" + action)
            if (action == TrackListAdapter.ACTION_PLAY) {
                if (track.file != null) {
                    ActivityUtils.launchMusicPlayer(activity, track.file)
                }
            } else if (action == TrackListAdapter.ACTION_DOWNLOAD) {
                val hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_CODE_ASK_PERMISSIONS)
                    keptTrack = track
                    return@ITrackSelectedListener
                }
                downloadTrack(track)
            } else if (action == TrackListAdapter.ACTION_SEARCH_VIA) {
                editTextSearch!!.setText(track.artist + " " + track.track)
            } else {
                Toast.makeText(activity, "Unknown action: " + action, Toast.LENGTH_LONG).show()
            }
        })
        recyclerViewItems!!.setHasFixedSize(true)
        recyclerViewItems!!.adapter = trackListAdapter
        registerForContextMenu(recyclerViewItems)
        recyclerViewItems!!.setOnContextClickListener { view ->
            Log.d(TAG, "oncontextclick: view" + view)
            false
        }
    }

    private fun downloadTrack(track: Track?) {
        // http://stackoverflow.com/questions/16773348/set-custom-folder-android-download-manager
        dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(
                Uri.parse(track!!.file))
        request.setMimeType("audio/mp3")
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                String.format("Subak/%s - %s.%s",
                        track.artist, track.track, "mp3"))
        Log.d(TAG, "enqueue track: " + track.file!!)
        enqueue = dm!!.enqueue(request)
    }

    // TODO Remove this if it is not useful
    @OnClick(R.id.fab)
    fun sayHi(button: FloatingActionButton) {
        Snackbar.make(button, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        //        textViewHelloWorld.setText(stringFromJNI());
        getEngineList()
    }

    private fun getEngineList() {
        showProgressDialog("Loading engines...")
        val result = ArrayList<Engine>()
        client!!.engineList
                // To avoid backpress exception
                .onBackpressureBuffer(10000)
                // To avoid networkonthread exception
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Engine>() {
                    override fun onCompleted() {
                        hideProgressDialog()

                        engines.clear()
                        addEngines(SubakApiInterface.ENGINE_TYPE_CHART, result, engines)
                        addEngines(SubakApiInterface.ENGINE_TYPE_SEARCH, result, engines)
                        spinnerEnginesDataAdapter.notifyDataSetChanged()
                        //                        if (engines.size() > 0) {
                        //                            selectEngine(engines.get(0));
                        //                        }
                    }

                    override fun onError(e: Throwable) {
                        hideProgressDialog()
                        showError(e)
                    }

                    override fun onNext(engine: Engine) {
                        result.add(engine)
                    }
                })
    }

    private fun addEngines(engineType: String, result: List<Engine>, toList: MutableList<Engine>) {
        Observable.from(result)
                .filter { engine -> engine.type == engineType }
                .toSortedList { engine, engine2 -> engine.name!!.compareTo(engine2.name!!) }
                .subscribe(object : Subscriber<List<Engine>>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        showError(e)
                    }

                    override fun onNext(list: List<Engine>) {
                        toList.addAll(list)
                    }
                })
    }

    private fun showError(e: Throwable) {
        e.printStackTrace()
        Log.e(TAG, "error in gettting engine list. e=" + e.message)
        Toast.makeText(this, String.format("%s: %s", e.javaClass.getSimpleName(), e.localizedMessage), Toast.LENGTH_LONG).show()
    }

    private val serverBaseUrl: String
        get() {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            return sharedPref.getString(SettingsActivity.KEY_SERVER_BASE_URL, "http://localhost:8081")
        }

    private fun selectEngine(engine: Engine) {
        Log.d(TAG, "select: engine=" + engine + ", type:" + engine.type)
        if (engine.type == SubakApiInterface.ENGINE_TYPE_CHART) {
            getTrackList(engine)
        }
    }

    private fun getTrackList(engine: Engine) {
        Log.d(TAG, "getTrackListLegacy: engine=" + engine + ",path=" + engine.path)
        showProgressDialog("Getting tracks...")
        client!!.getTrackList(engine, editTextSearch!!.text.toString())
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
                        trackList!!.clear()
                        trackList!!.addAll(trackListResponse.tracks)
                        trackListAdapter!!.notifyDataSetChanged()
                    }
                }
                )
    }

    private fun hideProgressDialog() {
        progressDialog!!.hide()
    }

    private fun showProgressDialog(msg: String) {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage(msg)
        progressDialog!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds trackList to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        private val TAG = MainActivity::class.java!!.getSimpleName()
        private val REQUEST_CODE_ASK_PERMISSIONS = 112
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    //    static {
    //        System.loadLibrary("native-lib");
    //    }
}
