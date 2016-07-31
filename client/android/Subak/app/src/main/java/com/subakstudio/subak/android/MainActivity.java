package com.subakstudio.subak.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // Automatically finds each field by the specified ID.
    @BindView(R.id.textViewHelloWorld)
    TextView textViewHelloWorld;

    @BindView(R.id.recyclerViewItems)
    RecyclerView recyclerViewItems;

    private ItemsAdapter itemsAdapter;
    private ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
