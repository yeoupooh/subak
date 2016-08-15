package com.subakstudio.subak.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.subakstudio.android.util.ActivityUtils;
import com.subakstudio.subak.api.Engine;
import com.subakstudio.subak.api.SubakApiInterface;
import com.subakstudio.subak.api.SubakClient;
import com.subakstudio.subak.api.Track;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = Main2Activity.class.getSimpleName();
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 112;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    private ProgressDialog progressDialog;

    private List<Engine> engines;
    private SubakClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpViewPager();
        setUpTabLayout();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // get current engine
                Log.d(TAG, "get track list: view=" + tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getTag());
                fetchTrackList(tabLayout.getSelectedTabPosition());
            }
        });

        setUpSubakClient();

        setUpEngineList();

        // fetch engine list
        fetchEngineList();

        // for search view
        handleIntent(getIntent());
    }

    private void setUpViewPager() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                fetchTrackList(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void fetchTrackList(int position) {
        Fragment fragment = mSectionsPagerAdapter.getItem(position);
        Log.d(TAG, "page selected: fragement=" + fragment + ",trackList=" + fragment.getArguments());
        if (fragment instanceof PlaceholderFragment) {
            PlaceholderFragment phFragment = (PlaceholderFragment) fragment;
            if (phFragment.getController() != null) {
                phFragment.getController().fetchTrackList("");
            } else {
                showError(new RuntimeException("no controller"));
            }
        }
    }

    private void setUpEngineList() {
        engines = new ArrayList<>();
    }


    private void setUpSubakClient() {
        client = new SubakClient(getServerBaseUrl());
    }

    private String getServerBaseUrl() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(SettingsActivity.KEY_SERVER_BASE_URL, "http://localhost:8081");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // for search view
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // for search view
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "search: query=" + query);
            //use the query to search your data somehow
        }
    }

    private void hideProgressDialog() {
        progressDialog.hide();
    }

    private void showProgressDialog(String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    private void fetchEngineList() {
        showProgressDialog("Loading engines...");
        final List<Engine> result = new ArrayList<>();
        client.getEngineList()
                // To avoid backpress exception
                .onBackpressureBuffer(10000)
                // To avoid networkonthread exception
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Engine>() {
                    @Override
                    public void onCompleted() {
                        hideProgressDialog();

                        engines.clear();
                        addChartTabs(result);
//                        addEngines(SubakApiInterface.ENGINE_TYPE_CHART, result, engines);
//                        addEngines(SubakApiInterface.ENGINE_TYPE_SEARCH, result, engines);
//                        spinnerEnginesDataAdapter.notifyDataSetChanged();
//                        if (engines.size() > 0) {
//                            selectEngine(engines.get(0));
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        showError(e);
                    }

                    @Override
                    public void onNext(Engine engine) {
                        result.add(engine);
                    }
                });
    }

    private void showError(Throwable e) {
        e.printStackTrace();
        Toast.makeText(this, String.format("%s: %s", e.getClass().getSimpleName(), e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
    }

    private void addChartTabs(List<Engine> result) {
        showProgressDialog("Loading Engines...");
        Observable.from(result)
                .filter(new Func1<Engine, Boolean>() {
                    @Override
                    public Boolean call(Engine engine) {
                        return engine.getType().equals(SubakApiInterface.ENGINE_TYPE_CHART);
                    }
                })
                .subscribe(new Subscriber<Engine>() {
                    @Override
                    public void onCompleted() {
                        mSectionsPagerAdapter.notifyDataSetChanged();
                        hideProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                    }

                    @Override
                    public void onNext(Engine engine) {
                        mSectionsPagerAdapter.addEngine(engine, "");
                    }
                });
    }

    private void setUpTabLayout() {
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);

        // for search view
        // Associate searchable configuration with the SearchView
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
                (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
                searchManager.getSearchableInfo(
                        new ComponentName(this, Main2Activity.class)));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                searchItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void search(String query) {
        Log.d(TAG, "search: query=" + query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "menu item selected: item=" + item);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_bar_add:
                mSectionsPagerAdapter.newPage();
                mSectionsPagerAdapter.notifyDataSetChanged();
                break;

            case R.id.action_bar_remove:
                mSectionsPagerAdapter.removePage();
                mSectionsPagerAdapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String KEY_NAME = "name";
        private static final String KEY_PATH = "path";
        private static final String KEY_KEYWORD = "keyword";
        private static final String KEY_TYPE = "type";
        private static final String KEY_ID = "id";

        @BindView(R.id.recyclerViewItems)
        RecyclerView recyclerViewItems;

        private List<Track> trackList;
        private TrackListAdapter trackListAdapter;
        private Track keptTrack;
        private SubakClientModel model;
        private SubakClientController controller;

        public SubakClientController getController() {
            return controller;
        }

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(EngineHolder engineHolder) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(KEY_ID, engineHolder.engine.getId());
            args.putString(KEY_NAME, engineHolder.engine.getName());
            args.putString(KEY_PATH, engineHolder.engine.getPath());
            args.putString(KEY_TYPE, engineHolder.engine.getType());
            args.putString(KEY_KEYWORD, engineHolder.keyword);
            fragment.setArguments(args);
            return fragment;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            ButterKnife.bind(this, rootView);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getArguments().getString(KEY_NAME));

            setUpTrackList(getActivity());

            setUpRecyclerView(getContext());

            setUpMVC();

            controller.fetchTrackList(getArguments().getString(KEY_KEYWORD));

            return rootView;
        }

        private void setUpMVC() {
            model = new SubakClientModel();
            model.setActivity(getActivity());
            model.setTrackList(trackList);
            model.setTrackListAdapter(trackListAdapter);
            Engine engine = new Engine();
            engine.setId(getArguments().getString(KEY_ID));
            engine.setType(getArguments().getString(KEY_TYPE));
            engine.setName(getArguments().getString(KEY_NAME));
            engine.setPath(getArguments().getString(KEY_PATH));
            model.setEngine(engine);
            controller = new SubakClientController(model);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        private void setUpTrackList(final Activity activity) {
            trackList = new ArrayList<>();
            trackListAdapter = new TrackListAdapter(trackList);
            trackListAdapter.setOnTrackSelectedListener(new ITrackSelectedListener() {
                @Override
                public void onAction(String action, Track track) {
                    Log.d(TAG, "onaction:" + action);
                    if (action.equals(TrackListAdapter.ACTION_PLAY)) {
                        if (track.getFile() != null) {
                            ActivityUtils.launchMusicPlayer(activity, track.getFile());
                        }
                    } else if (action.equals(TrackListAdapter.ACTION_DOWNLOAD)) {
                        int hasWriteContactsPermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_CODE_ASK_PERMISSIONS);
                            keptTrack = track;
                            return;
                        }
                        downloadTrack(track);
                    } else if (action.equals(TrackListAdapter.ACTION_SEARCH_VIA)) {
                        // TODO
//                    editTextSearch.setText(track.getArtist() + " " + track.getTrack());
                    } else {
                        Toast.makeText(activity, "Unknown action: " + action, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private void downloadTrack(Track track) {

        }

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        private void setUpRecyclerView(Context context) {
            recyclerViewItems.setLayoutManager(new LinearLayoutManager(context));
            recyclerViewItems.setHasFixedSize(true);
            recyclerViewItems.setAdapter(trackListAdapter);
            registerForContextMenu(recyclerViewItems);
            recyclerViewItems.setOnContextClickListener(new View.OnContextClickListener() {
                @Override
                public boolean onContextClick(View view) {
                    Log.d(TAG, "oncontextclick: view" + view);
                    return false;
                }
            });
        }
    }

    class EngineHolder {
        Engine engine;
        String keyword;

        public EngineHolder(Engine engine, String keyword) {
            this.engine = engine;
            this.keyword = keyword;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int size = 0;
        private List<EngineHolder> list = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(list.get(position));
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public void addEngine(Engine engine, String keyword) {
            list.add(new EngineHolder(engine, keyword));
        }

        public void newPage() {
            size++;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position).engine.getName();
        }

        public void removePage() {
            size--;
        }

        public EngineHolder getEngineHolderAt(int position) {
            return list.get(position);
        }
    }
}
