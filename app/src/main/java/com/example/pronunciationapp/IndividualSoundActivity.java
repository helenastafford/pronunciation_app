package com.example.pronunciationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class IndividualSoundActivity extends AppCompatActivity {
    private MediaPlayer teacherPlayer;
    private MediaPlayer userPlayer;
    private MediaRecorder recorder;
    private boolean isTeacherPlaying = false;
    private boolean isUserPlaying = false;
    private boolean isLooping = false;
    private int buttonPressCount = 0;
    private boolean hasUserRecording = false;

    // Representation Invariant: !isTeacherPlaying || !isUserPlaying
    // teacher icon lit up if and only if isTeacherPlaying, user icon lit up if and only if isUserPlaying
    // reset to default upon leaving page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);
        Intent intent = getIntent();
        TextView pinyinTextView = findViewById(R.id.pinyinTextView);
        final String pinyin = intent.getStringExtra("pinyin");
        pinyinTextView.setText(pinyin);

        ImageButton recordButton = findViewById(R.id.recordButton);
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    RadioGroup rg = findViewById(R.id.toneRadioGroup);
                    int id = rg.getCheckedRadioButtonId();
                    int tone;
                    if (id == R.id.tone1RadioButton) {
                        tone = 1;
                    } else if (id == R.id.tone2RadioButton) {
                        tone = 2;
                    } else if (id == R.id.tone3RadioButton) {
                        tone = 3;
                    } else {
                        tone = 4;
                    }

                    String fileName = pinyin + "_" + tone + "_user";
                    recorder.setOutputFile(fileName);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                    try {
                        recorder.prepare();
                    } catch (IOException e) {
                        System.out.println("prepare() failed");
                    }
                    recorder.start();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                } else {
                    return false;
                }
                return true;
            }
        });
        setTone(1);
    }

    public void handlePlayButtonClick(View view) {
        buttonPressCount++;
        System.out.println("play/pause button pressed " + buttonPressCount);
        boolean mediaWasPaused = pauseMedia();
        if (!mediaWasPaused) {
            if (hasUserRecording) {
                isLooping = true;
                playTeacher();
            } else {
                noRecordingToast();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (teacherPlayer != null) {
            teacherPlayer.release();
        }
        if (userPlayer != null) {
            userPlayer.release();
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

    // pre: media is not playing
    private void playTeacher() {
        teacherPlayer.start();
        isTeacherPlaying = true;

        ImageButton button = findViewById(R.id.playPauseButton);
        ImageView teacherSpeakingImageView = findViewById(R.id.teacherSpeakingImageView);

        if (isLooping) {
            button.setImageResource(R.drawable.pauseicon);
        }
        teacherSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    private void playUser() {
        userPlayer.start();
        isUserPlaying = true;

        ImageButton button = findViewById(R.id.playPauseButton);
        ImageView userSpeakingImageView = findViewById(R.id.studentSpeakingImageView);

        if (isLooping) {
            button.setImageResource(R.drawable.pauseicon);
        }
        userSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    public void handleTeacherClick(View view) {
        playTeacher();
    }

    public void handleUserClick(View view) {
        if (hasUserRecording) {
            playUser();
        } else {
            noRecordingToast();
        }
    }

    private void noRecordingToast() {
        Toast toast = Toast.makeText(this, "You have not yet made a recording for this sound.", Toast.LENGTH_SHORT);
        toast.show();
    }

    // pauses media if currently playing, returns true if it pauses media
    private boolean pauseMedia(){
        if(isTeacherPlaying){
            teacherPlayer.pause();
            teacherPlayer.seekTo(0);
            isTeacherPlaying = false;
        } else if(isUserPlaying){
            userPlayer.pause();
            userPlayer.seekTo(0);
            isUserPlaying = false;
        } else {
            return false;
        }
        ImageButton button = findViewById(R.id.playPauseButton);
        ImageView teacherSpeakingImageView = findViewById(R.id.teacherSpeakingImageView);
        ImageView studentSpeakingImageView = findViewById(R.id.studentSpeakingImageView);

        button.setImageResource(R.drawable.playicon);
        teacherSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
        studentSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));

        isLooping = false;
        return true;
    }

    private int getRecordingId(String speaker, int tone) {
        Intent intent = getIntent();
        String pinyin = intent.getStringExtra("pinyin");
        String teacherRecordingName = pinyin + "_" + tone + "_" + speaker;

        int recordingId = getResources().getIdentifier(teacherRecordingName, "raw", this.getPackageName());
        return recordingId;
    }

    private void setTone(int tone) {
        if (tone < 1 || tone > 4) {
            throw new IllegalArgumentException();
        }

        // Pause any currently playing recordings

        pauseMedia();



        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ImageView teacherSpeakingImageView = findViewById(R.id.teacherSpeakingImageView);
                ImageView studentSpeakingImageView = findViewById(R.id.studentSpeakingImageView);
                if (isLooping) {
                    if (mp == teacherPlayer) {
                        userPlayer.start();
                        isUserPlaying = true;
                        isTeacherPlaying = false;
                        teacherSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
                        studentSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        teacherPlayer.start();
                        isTeacherPlaying = true;
                        isUserPlaying = false;
                        teacherSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        studentSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
                    }
                } else {
                    if (mp == teacherPlayer) {
                        isTeacherPlaying = false;
                        teacherSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
                    } else {
                        isUserPlaying = false;
                        studentSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
                    }
                }
            }
        };

        int teacherRecordingId = getRecordingId("teacher", tone);
        teacherPlayer = MediaPlayer.create(this, teacherRecordingId);
        teacherPlayer.setOnCompletionListener(onCompletionListener);

        if (hasUserRecording) {
            int studentRecordingId = getRecordingId("user", tone);
            userPlayer = MediaPlayer.create(this, studentRecordingId);
            userPlayer.setOnCompletionListener(onCompletionListener);
        }
        //mediaPlayer.setLooping(true);
    }
}