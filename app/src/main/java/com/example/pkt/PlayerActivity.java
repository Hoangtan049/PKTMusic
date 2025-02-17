package com.example.pkt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private ArrayList<ListSong> songlist = new ArrayList<>();
    private int CurrentIndex = 0;
    private ImageButton btnPlay, btnPrev, btnNext;
    private TextView tvSongTitle, tvArtist, tvLyric, tvSongStart, tvSongEnd;
    private CircleImageView imageView;

    private boolean isPlaying = false;
    private boolean isPrepared = false; // Cờ trạng thái MediaPlayer
    private Handler handler = new Handler();

    // Runnable để cập nhật SeekBar theo thời gian
    private Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                seekBar.setProgress(currentPosition);
                String currentTime = formatTime(currentPosition);
                String totalTime = formatTime(duration);
                tvSongStart.setText(currentTime);
                tvSongEnd.setText(totalTime);
                handler.postDelayed(this, 1000); // Chạy lại Runnable sau 1 giây
            }
        }
    };

    private String formatTime(int timeInMillis) {
        int minutes = timeInMillis / 1000 / 60;
        int seconds = (timeInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
private  void  updateVIew(String songNames){
    DatabaseReference songRef=FirebaseDatabase.getInstance().getReference("ListSong");
    songRef.orderByChild("Name").equalTo(songNames).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                    DatabaseReference songRef = songSnapshot.getRef();
                    Integer currentViews = songSnapshot.child("View").getValue(Integer.class);
                    if (currentViews == null) currentViews = 0;
                    songRef.child("View").setValue(currentViews + 1);
                    Log.d("FirebaseUpdate", "Updated views for song: " + songNames);
                }
            } else {
                Log.e("FirebaseError", "Song with name '" + songNames + "' not found.");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
    private void fetchSongsFromIntent() {
        // Nhận dữ liệu từ Intent
        songlist = getIntent().getParcelableArrayListExtra("song_list");
        int currentIndex = getIntent().getIntExtra("current_song_index",0);
        if (songlist != null && !songlist.isEmpty()) {
            playSong(currentIndex);
        } else {
            Toast.makeText(this, "No songs found", Toast.LENGTH_SHORT).show();
        }
    }

    private void playSong(int index) {
        if (index < 0 || index >= songlist.size()) {
            Toast.makeText(this, "Invalid song index", Toast.LENGTH_SHORT).show();
            return;
        }

        CurrentIndex = index;
        ListSong currentSong = songlist.get(index);

        // Cập nhật giao diện
        tvSongTitle.setText(currentSong.getName());
        tvArtist.setText(currentSong.getArtist());
        tvLyric.setText(currentSong.getLyrics());
        Glide.with(this).load(currentSong.getImage()).into(imageView);
        seekBar.setProgress(0);
        btnPlay.setImageResource(R.drawable.baseline_pause_24);
        // Thiết lập MediaPlayer với URL mới
        initializeMediaPlayer(currentSong.getURL(),currentSong);

    }

    private void initializeMediaPlayer(String songUrl,ListSong currentSong) {
        if (songUrl == null || songUrl.isEmpty()) {
            Toast.makeText(this, "Invalid song URL", Toast.LENGTH_SHORT).show();
            Log.d("MediaPlayerState", "Music cc");
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release(); // Giải phóng tài nguyên nếu đã tồn tại MediaPlayer
            mediaPlayer = null;
        }

        isPrepared = false;
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(songUrl); // Thiết lập nguồn dữ liệu từ URL
            mediaPlayer.setOnPreparedListener(mp -> {
                isPrepared = true;
                seekBar.setMax(mediaPlayer.getDuration()); // Thiết lập độ dài tối đa cho SeekBar
                btnPlay.setEnabled(true);
                mediaPlayer.start();
                updateVIew(currentSong.getName());
                handler.post(updateSeekBarRunnable); // Bắt đầu cập nhật SeekBar
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayerState", "Error: " + what + ", Extra: " + extra);
                handleMediaPlayerError();
                return true;
            });

            mediaPlayer.prepareAsync(); // Chuẩn bị MediaPlayer bất đồng bộ
        } catch (IOException e) {
            Log.e("MediaPlayerState", "IOException: " + e.getMessage());
            Toast.makeText(this, "Error initializing media player.", Toast.LENGTH_SHORT).show();
        }
    }

    private void playMusic() {
        if (mediaPlayer != null && isPrepared) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPlaying = true;
                btnPlay.setImageResource(R.drawable.baseline_pause_24);
                Log.d("MediaPlayerState", "Music started playing.");
            }
        } else {
            Log.e("MediaPlayerState", "MediaPlayer is not prepared yet.");
            Toast.makeText(this, "MediaPlayer is not ready. Please wait.", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlay.setImageResource(R.drawable.baseline_play_arrow_24);
            handler.removeCallbacks(updateSeekBarRunnable); // Dừng cập nhật SeekBar khi tạm dừng
            Log.d("MediaPlayerState", "Music paused.");
        }
    }

    private void handleMediaPlayerError() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            isPrepared = false;
            Toast.makeText(this, "An error occurred. MediaPlayer reset.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            handler.removeCallbacks(updateSeekBarRunnable); // Dừng cập nhật SeekBar khi Activity bị huỷ
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        tvSongTitle = findViewById(R.id.tv_song_title);
        tvArtist = findViewById(R.id.tv_song_artist);
        imageView = findViewById(R.id.imageView);
        seekBar = findViewById(R.id.seek_bar);
        btnPlay = findViewById(R.id.btn_play);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        tvLyric = findViewById(R.id.lyrics);
        tvSongEnd = findViewById(R.id.txtSongEnd);
        tvSongStart = findViewById(R.id.txtSongStart);

//        String songName = getIntent().getStringExtra("song_name");
//        String artist = getIntent().getStringExtra("artist");
//        String imageUrl = getIntent().getStringExtra("song_image");
//        String songUrl = getIntent().getStringExtra("song_url");
//        String lyric = getIntent().getStringExtra("lyric");
//        tvSongTitle.setText(songName);
//        tvArtist.setText(artist);
//        tvLyric.setText(lyric);
//
//
//        Glide.with(this)
//                .load(imageUrl)
//                .into(imageView);

        btnPrev.setOnClickListener(v -> {
            if (CurrentIndex > 0) {
                playSong(CurrentIndex - 1);

            } else {
                Toast.makeText(this, "No previous song", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (CurrentIndex < songlist.size() - 1) {
                playSong(CurrentIndex + 1);

            } else {
                Toast.makeText(this, "No next song", Toast.LENGTH_SHORT).show();
            }
        });

        // Lấy danh sách bài hát từ Intent
        fetchSongsFromIntent();

        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                } else {
                    playMusic();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null && isPrepared && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
