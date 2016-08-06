package com.subakstudio.subak.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subakstudio.android.util.ActivityUtils;
import com.subakstudio.subak.api.SubakApiInterface;
import com.subakstudio.subak.api.Engine;
import com.subakstudio.subak.api.Track;
import com.subakstudio.subak.api.TrackListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    // Automatically finds each field by the specified ID.
    @BindView(R.id.textViewHelloWorld)
    TextView textViewHelloWorld;

    @BindView(R.id.recyclerViewItems)
    RecyclerView recyclerViewItems;

    private TrackListAdapter trackListAdapter;
    private ArrayList<Track> trackList;

    @BindView(R.id.spinner)
    Spinner spinner;
    ArrayAdapter<Engine> spinnerDataAdapter;
    List<Engine> engines;
    private ProgressDialog progressDialog;

    @OnItemSelected(R.id.spinner)
    public void spinnerItemSelected(Spinner spinner, int position) {
        // code here
        Log.d(TAG, "onitemselected: path=" + engines.get(position).getPath());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpRestAdaptor();
        setUpList();
        setUpEnginesSpinner();

    }

    private void setUpEnginesSpinner() {
        engines = new ArrayList<>();
        spinnerDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, engines);
        spinner.setAdapter(spinnerDataAdapter);
        // Specify the layout to use when the list of choices appears
        spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void setUpList() {
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
        final Context context = this;
        trackListAdapter.setOnTrackSelectedListener(new ITrackSelectedListener() {
            @Override
            public void onSelect(Track track) {
                if (track.getFile() != null) {
                    ActivityUtils.launchMusicPlayer(context, track.getFile());
                }
            }
        });
        recyclerViewItems.setHasFixedSize(true);
        recyclerViewItems.setAdapter(trackListAdapter);
    }

    @OnClick(R.id.fab)
    public void sayHi(FloatingActionButton button) {
        Snackbar.make(button, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        textViewHelloWorld.setText(stringFromJNI());

        requestGetEngines();
    }

    private void requestGetEngines() {
        showProgressDialog("Loading engines...");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getServerBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        SubakApiInterface service = retrofit.create(SubakApiInterface.class);
        Call<List<Engine>> call = service.getEngines();
        call.enqueue(new Callback<List<Engine>>() {
            @Override
            public void onResponse(Call<List<Engine>> call, Response<List<Engine>> response) {
                Log.d(TAG, "response:" + response.body().size());
                for (Engine engine : response.body()) {
                    Log.d(TAG, "engine:" + engine.getId());
                    engines.add(engine);
                }
                spinnerDataAdapter.notifyDataSetChanged();
                if (engines.size() > 0) {
                    spinner.setSelection(0);
                }
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Engine>> call, Throwable t) {
                Log.e(TAG, "response:" + t);
                hideProgressDialog();
            }
        });
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
        showProgressDialog("Getting tracks...");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getServerBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        SubakApiInterface service = retrofit.create(SubakApiInterface.class);
        Call<ResponseBody> call = service.getTracks(engine.getPath());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String trackListJson = response.body().string();
                    Log.d(TAG, "res:" + trackListJson);
                    ObjectMapper mapper = new ObjectMapper();
                    TrackListResponse trackListResponse = mapper.readValue(trackListJson, TrackListResponse.class);
                    trackList.clear();
                    trackList.addAll(trackListResponse.getTracks());
                    trackListAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgressDialog();
            }
        });
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
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
