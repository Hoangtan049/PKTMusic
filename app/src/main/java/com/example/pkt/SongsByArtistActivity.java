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

public class SongsByArtistActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ListSong> List;
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
        for (ListSong listSong : List) {
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
        setContentView(R.layout.activity_songs_by_artist);
        textView = findViewById(R.id.profile_name);
        imageView = findViewById(R.id.profile_image);
        searchView = findViewById(R.id.search_bar_song);
        recyclerView = findViewById(R.id.listsongbyartist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String artist = getIntent().getStringExtra("artist_name");
        String image = getIntent().getStringExtra("artist_image");
        textView.setText(artist);
        Glide.with(this).load(image).into(imageView);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListSong");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListSong listSong = dataSnapshot.getValue(ListSong.class);
                    if (listSong != null && listSong.getArtist().contains(artist)) {
                        listSong.setKey(dataSnapshot.getKey());
                        List.add(listSong);
                    }
                }
                if (List.size() > 0) {
                    songAdapter = new MyAdaper(SongsByArtistActivity.this, List);
                    recyclerView.setAdapter(songAdapter);
                } else {
                    Toast.makeText(SongsByArtistActivity.this, "Không có bài hát nào của ca sĩ này.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SongsByArtistActivity.this, "Không thể tải dữ liệu.", Toast.LENGTH_SHORT).show();
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