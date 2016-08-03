package com.subakstudio.subak.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.subakstudio.subak.api.SubakApiInterface;
import com.subakstudio.subak.api.SubakEngine;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
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

    private ItemsAdapter itemsAdapter;
    private ArrayList<Item> items;

    @BindView(R.id.spinner)
    Spinner spinner;
    ArrayAdapter<SubakEngine> spinnerAdapter;
    List<SubakEngine> engines;

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
        spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, engines);
        spinner.setAdapter(spinnerAdapter);
    }

    private void setUpRestAdaptor() {
    }

    private void setUpList() {
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add(new Item("title", String.valueOf(i)));
        }
        itemsAdapter = new ItemsAdapter(items);
        recyclerViewItems.setHasFixedSize(true);
        recyclerViewItems.setAdapter(itemsAdapter);
    }

    @OnClick(R.id.fab)
    public void sayHi(FloatingActionButton button) {
        Snackbar.make(button, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        textViewHelloWorld.setText(stringFromJNI());

        requestGetEngines();
    }

    private void requestGetEngines() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8081")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        SubakApiInterface service = retrofit.create(SubakApiInterface.class);
        Call<List<SubakEngine>> call = service.getEngines();
        call.enqueue(new Callback<List<SubakEngine>>() {
            @Override
            public void onResponse(Call<List<SubakEngine>> call, Response<List<SubakEngine>> response) {
                Log.d(TAG, "response:" + response.body().size());
                for (SubakEngine engine : response.body()) {
                    Log.d(TAG, "engine:" + engine.getId());
                    engines.add(engine);
                }
            }

            @Override
            public void onFailure(Call<List<SubakEngine>> call, Throwable t) {
                Log.e(TAG, "response:" + t);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
