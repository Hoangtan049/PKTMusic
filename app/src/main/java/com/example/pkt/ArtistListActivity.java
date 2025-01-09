package com.example.pkt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;

public class ArtistListActivity extends AppCompatActivity {
    RecyclerView artistRecyclerView;
    ArrayList<Artist> artistList;
    SearchView searchView;
    ArtistAdapter artistAdapter;

    private String removeAccents(String text) {
        if (text == null) {
            return null;
        }
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public void searchList(String text) {
        ArrayList<Artist> searchList = new ArrayList<>();
        String normalizedText = removeAccents(text);
        for (Artist artist : artistList) {
            String normalizedName = removeAccents(artist.getName());

            if (
                    normalizedName.toLowerCase().contains(normalizedText.toLowerCase())

            ) {
                searchList.add(artist);
            }
        }
        artistAdapter.searchDatalist(searchList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list2);
        searchView = findViewById(R.id.search_bar_artist);
        searchView.clearFocus();
        // Khởi tạo RecyclerView
        artistRecyclerView = findViewById(R.id.listartist);
        artistRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        artistRecyclerView.setLayoutManager(gridLayoutManager);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Artist");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                artistList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Artist artist = dataSnapshot.getValue(Artist.class);
                    if (artist != null) {
                        artistList.add(artist);
                        Log.d("ArtistList", "Artist list added: " + artist);
                    }
                }
                Log.d("ArtistList", "Total artists: " + artistList.size());
                if (artistList.size() > 0) {
                    artistAdapter = new ArtistAdapter(ArtistListActivity.this, artistList);
                    artistRecyclerView.setAdapter(artistAdapter);
                    artistAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ArtistListActivity.this, "Không có dữ liệu Artist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ArtistListActivity.this, "Không thể tải danh sách Artist", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage());
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
