package com.example.pronunciationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class IndividualSoundActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);
        Intent intent = getIntent();
        TextView pinyinTextView = findViewById(R.id.pinyinTextView);
        String pinyin = intent.getStringExtra("pinyin");
        pinyinTextView.setText(pinyin);
        setTone(1);
    }

    public void handlePlayButtonClick(View view) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.recording);
            mediaPlayer.setLooping(true);
        }
        ImageButton button = findViewById(R.id.playPauseButton);
        if (mediaPlayer.isPlaying()) {
            button.setImageResource(R.drawable.playicon);
            //getResources().getDrawable(android.R.drawable.ic_media_play,null)
            mediaPlayer.pause();
        } else {
            button.setImageResource(R.drawable.pauseicon);
            //mediaPlayer.seekTo(0);

            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public void handleTone1Click(View view) {
        setTone(1);
    }

    public void handleTone2Click(View view) {
        setTone(2);
    }

    public void handleTone3Click(View view) {
        setTone(3);
    }

    public void handleTone4Click(View view) {
        setTone(4);
    }

    private void setTone(int tone) {
        if (tone < 1 || tone > 4) {
            throw new IllegalArgumentException();
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            ImageButton button = findViewById(R.id.playPauseButton);
            button.setImageResource(R.drawable.playicon);
        }
        Intent intent = getIntent();
        String pinyin = intent.getStringExtra("pinyin");
        String recordingName = pinyin + "_" + tone + "_teacher";
        //recordingName = "recording";
        System.out.println(recordingName);
        int id = getResources().getIdentifier(recordingName, "raw", this.getPackageName());
        mediaPlayer = MediaPlayer.create(this, id);
        mediaPlayer.setLooping(true);
    }
}