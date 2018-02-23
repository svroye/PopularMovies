package com.example.steven.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";

    public int movieId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentThatStartedActivity = getIntent();

        if(intentThatStartedActivity != null){
            if (intentThatStartedActivity.hasExtra("id")){
                movieId = intentThatStartedActivity.getIntExtra("id", -1);
            }
            Log.d(TAG, "ID " + movieId);
        }
    }
}
