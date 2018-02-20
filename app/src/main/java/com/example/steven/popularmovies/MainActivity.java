package com.example.steven.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieAdapter.GridItemClickListener {

    // tag used for logging
    public static final String TAG = "MainActivity";
    // RecyclerView populating the activity and containing movie images
    RecyclerView mRecyclerView;
    // adapter to set to the RecyclerView
    MovieAdapter mAdapter;
    private static final int NUM_LIST_ITEMS = 100;

    // https://stackoverflow.com/questions/40587168/simple-android-grid-example-using-recyclerview-with-gridlayoutmanager-like-the
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.mainActivity_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mAdapter = new MovieAdapter(NUM_LIST_ITEMS, this);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /*
    Create the option menu in the header
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    Actions for what should happen when one of the options
    in the menu is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.menuMain_settings_item:
                Intent intentToSettingsActivity = new Intent(MainActivity.this,
                        SettingsActivity.class);
                startActivity(intentToSettingsActivity);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGridItemClick(int clickedItemIndex) {
        Log.d(TAG, "POS: " + clickedItemIndex);
        Intent intentToDetailActivity = new Intent(MainActivity.this,
                DetailActivity.class);
        startActivity(intentToDetailActivity);
    }
}
