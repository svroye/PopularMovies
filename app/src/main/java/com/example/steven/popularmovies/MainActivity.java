package com.example.steven.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    MovieAdapter mAdapter;
    private static final int NUM_LIST_ITEMS = 100;

    // https://stackoverflow.com/questions/40587168/simple-android-grid-example-using-recyclerview-with-gridlayoutmanager-like-the
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.mainActivity_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mAdapter = new MovieAdapter(NUM_LIST_ITEMS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
