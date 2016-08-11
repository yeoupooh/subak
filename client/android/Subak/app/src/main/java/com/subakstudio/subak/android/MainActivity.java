package com.subakstudio.subak.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.subakstudio.android.util.ActivityUtils;
import com.subakstudio.subak.api.Engine;
import com.subakstudio.subak.api.SubakApiInterface;
import com.subakstudio.subak.api.SubakClient;
import com.subakstudio.subak.api.Track;
import com.subakstudio.subak.api.TrackListResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemSelected;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 112;
    // Automatically finds each field by the specified ID.
//    @BindView(R.id.textViewHelloWorld)
//    TextView textViewHelloWorld;

    @BindView(R.id.recyclerViewItems)
    RecyclerView recyclerViewItems;

    private TrackListAdapter trackListAdapter;
    private ArrayList<Track> trackList;

    @BindView(R.id.editText)
    EditText editTextSearch;

    @BindView(R.id.spinner)
    Spinner spinnerEngines;
    ArrayAdapter<Engine> spinnerEnginesDataAdapter;
    List<Engine> engines;
    private ProgressDialog progressDialog;
    private SubakClient client;
    private DownloadManager dm;
    private long enqueue;
    private Track keptTrack;

    @OnItemSelected(R.id.spinner)
    public void spinnerItemSelected(Spinner spinner, int position) {
        // code here
        Log.d(TAG, "onitemselected: path=" + engines.get(position).getPath());
    }

    @OnEditorAction(R.id.editText)
    public boolean editTextSearchAction(TextView v, int actionId, KeyEvent event) {
        Log.d(TAG, "onEditorAction: ");
        getTrackList((Engine) spinnerEngines.getSelectedItem());
        return true;
    }

    private void getTrackList(Engine engine, Editable text) {
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setUpSubakClient();
        setUpRestAdaptor();
        setUpTrackList();
        setUpEnginesSpinner();

        getEngineList();
    }

    private void setUpSubakClient() {
        client = new SubakClient(getServerBaseUrl());
    }

    private void setUpEnginesSpinner() {
        engines = new ArrayList<>();
        spinnerEnginesDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, engines);
        spinnerEngines.setAdapter(spinnerEnginesDataAdapter);
        // Specify the layout to use when the list of choices appears
        spinnerEnginesDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEngines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Log.d(TAG, "item:" + parent.getItemAtPosition(pos));
                if (parent.getItemAtPosition(pos) instanceof Engine) {
                    selectEngine((Engine) parent.getItemAtPosition(pos));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "item nothing: " + parent);
            }
        });
    }

    private void setUpRestAdaptor() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    downloadTrack(keptTrack);
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setUpTrackList() {
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        trackList = new ArrayList<>();
        // FIXME remove dummy data
        for (int i = 0; i < 100; i++) {
            Track track = new Track();
            track.setTrack("track" + i);
            track.setArtist("artist" + i);
            trackList.add(track);
        }
        trackListAdapter = new TrackListAdapter(trackList);
        final Activity activity = this;
        trackListAdapter.setOnTrackSelectedListener(new ITrackSelectedListener() {
            @Override
            public void onAction(String action, Track track) {
                Log.d(TAG, "onaction:" + action);
                if (action.equals(TrackListAdapter.ACTION_PLAY)) {
                    if (track.getFile() != null) {
                        ActivityUtils.launchMusicPlayer(activity, track.getFile());
                    }
                } else if (action.equals(TrackListAdapter.ACTION_DOWNLOAD)) {
                    int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_ASK_PERMISSIONS);
                        keptTrack = track;
                        return;
                    }
                    downloadTrack(track);
                }
            }
        });
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

    private void downloadTrack(Track track) {
        // http://stackoverflow.com/questions/16773348/set-custom-folder-android-download-manager
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(track.getFile()));
        request.setMimeType("audio/mp3");
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                String.format("Subak/%s - %s.%s",
                        track.getArtist(), track.getTrack(), "mp3"));
        Log.d(TAG, "enqueue track: " + track.getFile());
        enqueue = dm.enqueue(request);
    }

    // TODO Remove this if it is not useful
    @OnClick(R.id.fab)
    public void sayHi(FloatingActionButton button) {
        Snackbar.make(button, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
//        textViewHelloWorld.setText(stringFromJNI());
    }

    private void getEngineList() {
        client.setSubackListener(new SubakClient.ISubakListener() {
            @Override
            public void onRequest() {
                showProgressDialog("Loading engines...");
            }

            @Override
            public void onSuccess(Object result) {
                if (result instanceof List) {
                    engines.clear();

                    // sort engine by chart and search
                    List<Engine> chartList = new ArrayList<>();
                    List<Engine> searchList = new ArrayList<>();
                    for (Engine engine : (List<Engine>) result) {
                        Log.d(TAG, "engine:" + engine.getId());
                        if (engine.getType().equals(SubakApiInterface.ENGINE_TYPE_CHART)) {
                            chartList.add(engine);
                        } else {
                            searchList.add(engine);
                        }
                    }
                    Collections.sort(chartList, new Comparator<Engine>() {
                        @Override
                        public int compare(Engine e1, Engine e2) {
                            return e1.getName().compareTo(e2.getName());
                        }
                    });
                    Collections.sort(searchList, new Comparator<Engine>() {
                        @Override
                        public int compare(Engine e1, Engine e2) {
                            return e1.getName().compareTo(e2.getName());
                        }
                    });
                    engines.addAll(chartList);
                    engines.addAll(searchList);

                    spinnerEnginesDataAdapter.notifyDataSetChanged();

                    if (engines.size() > 0) {
                        spinnerEngines.setSelection(0);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFinally() {
                hideProgressDialog();
            }
        });
        client.getEngineList();
    }

    private String getServerBaseUrl() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(SettingsActivity.KEY_SERVER_BASE_URL, "http://localhost:8081");
    }

    private void selectEngine(Engine engine) {
        Log.d(TAG, "select: engine=" + engine + ", type:" + engine.getType());
        if (engine.getType().equals(SubakApiInterface.ENGINE_TYPE_CHART)) {
            getTrackList(engine);
        }
    }

    private void getTrackList(Engine engine) {
        Log.d(TAG, "getTrackList: engine=" + engine);
        client.setSubackListener(new SubakClient.ISubakListener() {
            @Override
            public void onRequest() {
                showProgressDialog("Getting tracks...");
            }

            @Override
            public void onSuccess(Object result) {
                if (result instanceof TrackListResponse) {
                    TrackListResponse trackListResponse = (TrackListResponse) result;
                    trackList.clear();
                    trackList.addAll(trackListResponse.getTracks());
                    trackListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFinally() {
                hideProgressDialog();
            }
        });
        client.getTrackList(engine, editTextSearch.getText().toString());
    }

    private void hideProgressDialog() {
        progressDialog.hide();
    }

    private void showProgressDialog(String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds trackList to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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
