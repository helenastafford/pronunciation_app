package com.example.pronunciationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class IndividualSoundActivity extends AppCompatActivity {
    private MediaPlayer teacherPlayer;
    private MediaPlayer studentPlayer;

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
//        if (mediaPlayer == null) {
//            mediaPlayer = MediaPlayer.create(this, R.raw.recording);
//            mediaPlayer.setLooping(true);
//        }
        ImageButton button = findViewById(R.id.playPauseButton);
        ImageView teacherSpeakingImageView = findViewById(R.id.teacherSpeakingImageView);
        ImageView studentSpeakingImageView = findViewById(R.id.studentSpeakingImageView);

        if (teacherPlayer.isPlaying() || studentPlayer.isPlaying()) {
            button.setImageResource(R.drawable.playicon);
            teacherSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
            studentSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
            //getResources().getDrawable(android.R.drawable.ic_media_play,null)
            if (teacherPlayer.isPlaying()) {
                teacherPlayer.pause();
            }
            if (studentPlayer.isPlaying()) {
                studentPlayer.pause();
            }
        } else {
            button.setImageResource(R.drawable.pauseicon);
            //mediaPlayer.seekTo(0);

            teacherPlayer.start();

            teacherSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            studentSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (teacherPlayer != null) {
            teacherPlayer.release();
        }
        if (studentPlayer != null) {
            studentPlayer.release();
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

        // Pause any currently playing recordings
        if (teacherPlayer != null && teacherPlayer.isPlaying()) {
            teacherPlayer.pause();
        }
        if (studentPlayer != null && studentPlayer.isPlaying()) {
            studentPlayer.pause();
        }

        // Set button to show play (as playback has been paused)
        ImageButton button = findViewById(R.id.playPauseButton);
        button.setImageResource(R.drawable.playicon);

        Intent intent = getIntent();
        String pinyin = intent.getStringExtra("pinyin");
        String teacherRecordingName = pinyin + "_" + tone + "_teacher";
        String studentRecordingName = pinyin + "_" + tone + "_user";

        //recordingName = "recording";
        //System.out.println(recordingName);
        int teacherRecordingId = getResources().getIdentifier(teacherRecordingName, "raw", this.getPackageName());
        int studentRecordingId = getResources().getIdentifier(studentRecordingName, "raw", this.getPackageName());
        teacherPlayer = MediaPlayer.create(this, teacherRecordingId);
        studentPlayer = MediaPlayer.create(this, studentRecordingId);

        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ImageView teacherSpeakingImageView = findViewById(R.id.teacherSpeakingImageView);
                ImageView studentSpeakingImageView = findViewById(R.id.studentSpeakingImageView);
                if (mp == teacherPlayer) {
                    studentPlayer.start();
                    teacherSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
                    studentSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else {
                    teacherPlayer.start();
                    teacherSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    studentSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
                }
            }
        };


        teacherPlayer.setOnCompletionListener(onCompletionListener);
        studentPlayer.setOnCompletionListener(onCompletionListener);

        //mediaPlayer.setLooping(true);
    }
}