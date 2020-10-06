package com.example.pronunciationapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.File;
import java.io.IOException;

public class IndividualSoundActivity extends AppCompatActivity {
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private MediaPlayer teacherPlayer;
    private MediaPlayer userPlayer;
    private MediaRecorder recorder;
    private boolean isTeacherPlaying = false;
    private boolean isUserPlaying = false;
    private boolean isLooping = false;
    private int currentTone = 1;

    // Representation Invariant: !isTeacherPlaying || !isUserPlaying
    // teacher icon lit up if and only if isTeacherPlaying, user icon lit up if and only if isUserPlaying
    // reset to default upon leaving page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);

       // registerForActivityResult(new ActivityResultCallback());

        Intent intent = getIntent();
        TextView pinyinTextView = findViewById(R.id.pinyinTextView);
        final String pinyin = intent.getStringExtra("pinyin");
        pinyinTextView.setText(pinyin);

        ImageButton recordButton = findViewById(R.id.recordButton);
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!CheckPermissions())
                    RequestPermissions();

                if (CheckPermissions()) {
                    ImageButton recordButton = findViewById(R.id.recordButton);
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                        //change color
                        recordButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

                        String fileName = getFileName("user", currentTone);

                        File f = new File(getFilesDir(), fileName);
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("create new file failed");
                        }
                        recorder.setOutputFile(f);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                        try {
                            recorder.prepare();
                        } catch (IOException e) {
                            System.out.println(e);
                            System.out.println("prepare() or start() failed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            return false;
                        }
                        recorder.start();
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        //change color
                        recordButton.setBackgroundColor(getResources().getColor(R.color.colorRecordButton));

                        recorder.stop();
                        recorder.release();
                        recorder = null;
                        setTone(currentTone);
                        System.out.println();
                    } else {//returns false if motionEvent is not ACTION_DOWN or ACTION_UP
                        return false;
                    }
                } else {//returns false if not given permission
                    return false;
                }
                    return true;
                }

        });
        setTone(1);
    }

    public void handlePlayButtonClick(View view) {
        boolean mediaWasPaused = pauseMedia();
        if (!mediaWasPaused) {
            if (hasUserRecording()) {
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
            teacherPlayer = null;
        }
        if (userPlayer != null) {
            userPlayer.release();
            userPlayer = null;
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void playTeacher() {
        teacherPlayer.start();
        isTeacherPlaying = true;

        ImageButton button = findViewById(R.id.playPauseButton);
        ImageView teacherSpeakingImageView = findViewById(R.id.teacherSpeakingImageView);

        if (isLooping) {
            button.setImageResource(R.drawable.pauseicon);
        }
        teacherSpeakingImageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
                //.setColorFilter(R.color.colorAccent);
                //setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    private void playUser() {
        userPlayer.start();
        isUserPlaying = true;

        ImageButton button = findViewById(R.id.playPauseButton);
        ImageView userSpeakingImageView = findViewById(R.id.studentSpeakingImageView);

        if (isLooping) {
            button.setImageResource(R.drawable.pauseicon);
        }
        userSpeakingImageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void handleTeacherClick(View view) {
        playTeacher();
    }

    public void handleUserClick(View view) {

        if (hasUserRecording()) {
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        //teacherSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
       // studentSpeakingImageView.setBackgroundColor(getResources().getColor(R.color.colorNotSpeaking));
        studentSpeakingImageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorNotSpeaking)));
        teacherSpeakingImageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorNotSpeaking)));
        isLooping = false;
        return true;
    }

    private int getTeacherRecordingId(int tone) {
        String teacherRecordingName = getFileName("teacher", tone);

        int recordingId = getResources().getIdentifier(teacherRecordingName, "raw", this.getPackageName());
        return recordingId;
    }

    private File getUserRecordingFile(int tone) {
        String userRecordingName = getFileName("user", tone);

        return new File(getFilesDir(), userRecordingName);
    }

    private void setTone(int tone) {

        final Context context = this;
        if (tone < 1 || tone > 4) {
            throw new IllegalArgumentException();
        }
        currentTone = tone;
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
                        teacherSpeakingImageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorNotSpeaking)));
                        studentSpeakingImageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent)));
                    } else {
                        teacherPlayer.start();
                        isTeacherPlaying = true;
                        isUserPlaying = false;
                        teacherSpeakingImageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent)));
                        studentSpeakingImageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorNotSpeaking)));
                    }
                } else {
                    if (mp == teacherPlayer) {
                        isTeacherPlaying = false;
                        teacherSpeakingImageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorNotSpeaking)));
                    } else {
                        isUserPlaying = false;
                        studentSpeakingImageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorNotSpeaking)));
                    }
                }
            }
        };

        int teacherRecordingId = getTeacherRecordingId(tone);
        teacherPlayer = MediaPlayer.create(this, teacherRecordingId);
        teacherPlayer.setOnCompletionListener(onCompletionListener);

        if (hasUserRecording()) {
            File userRecordingFile = getUserRecordingFile(tone);
            userPlayer = MediaPlayer.create(this, Uri.fromFile(userRecordingFile));
            userPlayer.setOnCompletionListener(onCompletionListener);
        }
        //mediaPlayer.setLooping(true);
    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(IndividualSoundActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    private boolean hasUserRecording() {
        String fileName = getFileName("user", currentTone);
        File f = new File(getFilesDir(), fileName);
        return f.exists();
    }

    private String getFileName(String speaker,int tone) {
        Intent intent = getIntent();
        String pinyin = intent.getStringExtra("pinyin");
        String recordingName = pinyin + "_" + tone + "_" + speaker;
        return recordingName;
    }
}