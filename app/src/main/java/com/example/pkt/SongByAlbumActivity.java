package com.example.pkt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;

public class SongByAlbumActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ListSong> albumList;
    MyAdaper songAdapter;
    TextView textView;
    SearchView searchView;
    ImageView imageView;

    private String removeAccents(String text) {
        if (text == null) {
            return null;
        }
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public void searchList(String text) {
        ArrayList<ListSong> searchList = new ArrayList<>();
        String normalizedText = removeAccents(text);
        for (ListSong listSong : albumList) {
            String normalizedName = removeAccents(listSong.getName());
            String normalizedArtist = removeAccents(listSong.getArtist());
            if (
                    normalizedName.toLowerCase().contains(normalizedText.toLowerCase()) ||
                            normalizedArtist.toLowerCase().contains(normalizedText.toLowerCase())
            ) {
                searchList.add(listSong);
            }
        }
        songAdapter.searchDatalist(searchList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_by_album);
        searchView = findViewById(R.id.search_bar_album);
        recyclerView = findViewById(R.id.listsongbyalbum);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageView = findViewById(R.id.album_image);
        textView = findViewById(R.id.album_name);
        String namealbum = getIntent().getStringExtra("album_name");
        String imagealbum = getIntent().getStringExtra("album_image");
        textView.setText(namealbum);
        Glide.with(this).load(imagealbum).into(imageView);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListSong");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                albumList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListSong listSong = dataSnapshot.getValue(ListSong.class);
                    if (listSong != null && listSong.getAlbum().contains(namealbum)) {
                        listSong.setKey(dataSnapshot.getKey());
                        albumList.add(listSong);
                    }
                }
                if (albumList.size() > 0) {
                    songAdapter = new MyAdaper(SongByAlbumActivity.this, albumList);
                    recyclerView.setAdapter(songAdapter);
                } else {
                    Toast.makeText(SongByAlbumActivity.this, "Không có bài hát nào thuộc album  này.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchList(s);
                return true;
            }
        });
    }
}