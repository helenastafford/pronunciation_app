package com.example.pronunciationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private RecyclerView soundRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundRecyclerView = findViewById(R.id.soundRecyclerView);
        soundRecyclerView.setHasFixedSize(true);


        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.generateDefaultLayoutParams().setMargins(30,30,30,30);
        soundRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new SoundAdapter();
        soundRecyclerView.setAdapter(mAdapter);
    }
}