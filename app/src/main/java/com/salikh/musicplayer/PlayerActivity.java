package com.salikh.musicplayer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.marcinmoskala.arcseekbar.ArcSeekBar;

import java.io.File;
import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;
import soup.neumorphism.NeumorphButton;

public class PlayerActivity extends AppCompatActivity {

    public static String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    int maxVolume, currentVolume;
    private NeumorphButton backBtn, nextBtn, revBtn, menuBtn;
    private TextView textsName;
    private ImageView playBtn, pauseView, imageView;
    private SeekBar musicSeek;
    private ArcSeekBar seekBar;
    private String sname;
    private ArrayList<File> mySong;
    private Thread updateSeekBar;
    private AudioManager audioManager;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.white));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));


        loadView();

        int[] colorArray = getResources().getIntArray(R.array.gradient);
        seekBar.setProgressGradient(colorArray);

        pauseView.setImageResource(R.drawable.ic_baseline_pause_24);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Bundle bundle = getIntent().getExtras();

        mySong = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = getIntent().getStringExtra("songName");
        position = bundle.getInt("pos", 0);


        textsName.setSelected(true);
        Uri uri = Uri.parse(mySong.get(position).toString());
        sname = mySong.get(position).getName();
        textsName.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();


        seekBarMusic();

        serListeners();

        volumeMusic();
    }


    private void serListeners() {

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StyleableToast.makeText(PlayerActivity.this, "Mavjud emas", R.style.salihkToast).show();
            }
        });

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    pauseView.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                } else {
                    pauseView.setImageResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextBtn.performClick();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1) % mySong.size());
                Uri u = Uri.parse(mySong.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySong.get(position).getName();
                textsName.setText(sname);
                updateSeekBar = null;
                mediaPlayer.start();
                pauseView.setImageResource(R.drawable.ic_baseline_pause_24);
                startAnimator(imageView);
                setMusicSeekBarState();
            }
        });

        revBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position - 1) < 0) ? (mySong.size() - 1) : (position - 1);
                Uri u = Uri.parse(mySong.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySong.get(position).getName();
                updateSeekBar = null;
                textsName.setText(sname);
                mediaPlayer.start();
                pauseView.setImageResource(R.drawable.ic_baseline_pause_24);
                startAnimatore(imageView);
                setMusicSeekBarState();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
                Animatoo.animateZoom(PlayerActivity.this);
                finish();
            }
        });
    }

    private void volumeMusic() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        seekBar.setMaxProgress(maxVolume);
        seekBar.setProgress(currentVolume);

    }

    private void seekBarMusic() {
        musicSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        setMusicSeekBarState();
    }


    private void loadView() {
        backBtn = findViewById(R.id.back_btn);
        nextBtn = findViewById(R.id.next_next);
        revBtn = findViewById(R.id.back_next);
        playBtn = findViewById(R.id.pause);
        textsName = findViewById(R.id.music_name);
        pauseView = findViewById(R.id.pause_view);
        imageView = findViewById(R.id.imageMus);
        seekBar = findViewById(R.id.seekBer_view);
        musicSeek = findViewById(R.id.sekkbar);
        menuBtn = findViewById(R.id.menu_btn);
    }

    private void setMusicSeekBarState() {
        if (updateSeekBar != null) {
            updateSeekBar.stop();
            updateSeekBar = null;
        }
        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        musicSeek.setProgress(currentPosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        musicSeek.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
    }

    public void startAnimator(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator);
        animatorSet.start();
    }

    public void startAnimatore(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, -360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator);
        animatorSet.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        volumeMusic();
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        volumeMusic();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(intent);
        Animatoo.animateZoom(PlayerActivity.this);
        finish();
        super.onBackPressed();
    }
}