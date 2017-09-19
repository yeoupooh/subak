package com.subakstudio.subak.android

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.subakstudio.android.util.ActivityUtils
import com.subakstudio.subak.api.Engine
import com.subakstudio.subak.api.SubakApiInterface
import com.subakstudio.subak.api.SubakClient
import com.subakstudio.subak.api.Track

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class Main2Activity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    /**
     * The [ViewPager] that will host the section contents.
     */
    @BindView(R.id.container)
    internal var mViewPager: ViewPager? = null

    @BindView(R.id.sliding_tabs)
    internal var tabLayout: TabLayout? = null

    private var progressDialog: ProgressDialog? = null

    private var engines: MutableList<Engine>? = null
    private var client: SubakClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        ButterKnife.bind(this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        setUpViewPager()
        setUpTabLayout()

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            // get current engine
            Log.d(TAG, "get track list: view=" + tabLayout!!.getTabAt(tabLayout!!.selectedTabPosition)!!.tag!!)
            fetchTrackList(tabLayout!!.selectedTabPosition)
        }

        setUpSubakClient()

        setUpEngineList()

        // fetch engine list
        fetchEngineList()

        // for search view
        handleIntent(intent)
    }

    private fun setUpViewPager() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager!!.adapter = mSectionsPagerAdapter

        mViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                //                fetchTrackList(position);
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun fetchTrackList(position: Int) {
        val fragment = mSectionsPagerAdapter!!.getItem(position)
        Log.d(TAG, "page selected: fragement=" + fragment + ",trackList=" + fragment.arguments)
        if (fragment is PlaceholderFragment) {
            if (fragment.controller != null) {
                fragment.controller!!.fetchTrackList("")
            } else {
                showError(RuntimeException("no controller"))
            }
        }
    }

    private fun setUpEngineList() {
        engines = ArrayList()
    }


    private fun setUpSubakClient() {
        client = SubakClient(serverBaseUrl)
    }

    private val serverBaseUrl: String
        get() {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            return sharedPref.getString(SettingsActivity.KEY_SERVER_BASE_URL, "http://localhost:8081")
        }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // for search view
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        // for search view
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Log.d(TAG, "search: query=" + query)
            //use the query to search your data somehow
        }
    }

    private fun hideProgressDialog() {
        progressDialog!!.hide()
    }

    private fun showProgressDialog(msg: String) {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage(msg)
        progressDialog!!.show()
    }

    private fun fetchEngineList() {
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

                        engines!!.clear()
                        addChartTabs(result)
                        //                        addEngines(SubakApiInterface.ENGINE_TYPE_CHART, result, engines);
                        //                        addEngines(SubakApiInterface.ENGINE_TYPE_SEARCH, result, engines);
                        //                        spinnerEnginesDataAdapter.notifyDataSetChanged();
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

    private fun showError(e: Throwable) {
        e.printStackTrace()
        Toast.makeText(this, String.format("%s: %s", e.javaClass.getSimpleName(), e.localizedMessage), Toast.LENGTH_LONG).show()
    }

    private fun addChartTabs(result: List<Engine>) {
        showProgressDialog("Loading Engines...")
        Observable.from(result)
                .filter { engine -> engine.type == SubakApiInterface.ENGINE_TYPE_CHART }
                .subscribe(object : Subscriber<Engine>() {
                    override fun onCompleted() {
                        mSectionsPagerAdapter!!.notifyDataSetChanged()
                        hideProgressDialog()
                    }

                    override fun onError(e: Throwable) {
                        hideProgressDialog()
                    }

                    override fun onNext(engine: Engine) {
                        mSectionsPagerAdapter!!.addEngine(engine, "")
                    }
                })
    }

    private fun setUpTabLayout() {
        tabLayout!!.setupWithViewPager(mViewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main2, menu)

        // for search view
        // Associate searchable configuration with the SearchView
        val searchItem = menu.findItem(R.id.search)
        val searchView =
                //                (SearchView) menu.findItem(R.id.search).getActionView();
                MenuItemCompat.getActionView(searchItem) as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(
                //                searchManager.getSearchableInfo(getComponentName()));
                searchManager.getSearchableInfo(
                        ComponentName(this, Main2Activity::class.java)))
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                search(query)
                searchItem.collapseActionView()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        return true
    }

    private fun search(query: String) {
        Log.d(TAG, "search: query=" + query)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "menu item selected: item=" + item)
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        when (id) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))

            R.id.action_bar_add -> {
                mSectionsPagerAdapter!!.newPage()
                mSectionsPagerAdapter!!.notifyDataSetChanged()
            }

            R.id.action_bar_remove -> {
                mSectionsPagerAdapter!!.removePage()
                mSectionsPagerAdapter!!.notifyDataSetChanged()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        @BindView(R.id.recyclerViewItems)
        internal var recyclerViewItems: RecyclerView? = null

        private var trackList: List<Track>? = null
        private var trackListAdapter: TrackListAdapter? = null
        private var keptTrack: Track? = null
        private var model: SubakClientModel? = null
        var controller: SubakClientController? = null
            private set

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_main2, container, false)
            ButterKnife.bind(this, rootView)
            //            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //            textView.setText(getArguments().getString(KEY_NAME));

            setUpTrackList(activity)

            setUpRecyclerView(context)

            setUpMVC()

            controller!!.fetchTrackList(arguments.getString(KEY_KEYWORD))

            return rootView
        }

        private fun setUpMVC() {
            model = SubakClientModel()
            model!!.activity = activity
            model!!.trackList = trackList
            model!!.trackListAdapter = trackListAdapter
            val engine = Engine()
            engine.id = arguments.getString(KEY_ID)
            engine.type = arguments.getString(KEY_TYPE)
            engine.name = arguments.getString(KEY_NAME)
            engine.path = arguments.getString(KEY_PATH)
            model!!.engine = engine
            controller = SubakClientController(model!!)
        }

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        private fun setUpTrackList(activity: Activity) {
            trackList = ArrayList()
            trackListAdapter = TrackListAdapter(trackList!!)
            trackListAdapter!!.setOnTrackSelectedListener(ITrackSelectedListener { action, track ->
                Log.d(TAG, "onaction:" + action)
                if (action == TrackListAdapter.ACTION_PLAY) {
                    if (track.file != null) {
                        ActivityUtils.launchMusicPlayer(activity, track.file)
                    }
                } else if (action == TrackListAdapter.ACTION_DOWNLOAD) {
                    val hasWriteContactsPermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                REQUEST_CODE_ASK_PERMISSIONS)
                        keptTrack = track
                        return@ITrackSelectedListener
                    }
                    downloadTrack(track)
                } else if (action == TrackListAdapter.ACTION_SEARCH_VIA) {
                    // TODO
                    //                    editTextSearch.setText(track.getArtist() + " " + track.getTrack());
                } else {
                    Toast.makeText(activity, "Unknown action: " + action, Toast.LENGTH_LONG).show()
                }
            })
        }

        private fun downloadTrack(track: Track) {

        }

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        private fun setUpRecyclerView(context: Context) {
            recyclerViewItems!!.layoutManager = LinearLayoutManager(context)
            recyclerViewItems!!.setHasFixedSize(true)
            recyclerViewItems!!.adapter = trackListAdapter
            registerForContextMenu(recyclerViewItems!!)
            recyclerViewItems!!.setOnContextClickListener { view ->
                Log.d(TAG, "oncontextclick: view" + view)
                false
            }
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            //        private static final String ARG_SECTION_NUMBER = "section_number";
            private val KEY_NAME = "name"
            private val KEY_PATH = "path"
            private val KEY_KEYWORD = "keyword"
            private val KEY_TYPE = "type"
            private val KEY_ID = "id"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(engineHolder: EngineHolder): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putString(KEY_ID, engineHolder.engine.id)
                args.putString(KEY_NAME, engineHolder.engine.name)
                args.putString(KEY_PATH, engineHolder.engine.path)
                args.putString(KEY_TYPE, engineHolder.engine.type)
                args.putString(KEY_KEYWORD, engineHolder.keyword)
                fragment.arguments = args
                return fragment
            }
        }
    }

    internal inner class EngineHolder(var engine: Engine, var keyword: String)

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private var size = 0
        private val list = ArrayList<EngineHolder>()

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(list[position])
        }

        override fun getCount(): Int {
            return list.size
        }

        fun addEngine(engine: Engine, keyword: String) {
            list.add(EngineHolder(engine, keyword))
        }

        fun newPage() {
            size++
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return list[position].engine.name
        }

        fun removePage() {
            size--
        }

        fun getEngineHolderAt(position: Int): EngineHolder {
            return list[position]
        }
    }

    companion object {

        private val TAG = Main2Activity::class.java!!.getSimpleName()
        private val REQUEST_CODE_ASK_PERMISSIONS = 112
    }
}
