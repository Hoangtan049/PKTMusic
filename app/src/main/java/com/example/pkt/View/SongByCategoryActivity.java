package com.example.pkt.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pkt.Adapter.MyAdaper;
import com.example.pkt.Classes.ListSong;
import com.example.pkt.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;

public class SongByCategoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ListSong> List;
    SearchView searchView;
    MyAdaper songAdapter;
    TextView textView;
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
        setContentView(R.layout.activity_song_by_category);
        textView = findViewById(R.id.category_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = findViewById(R.id.category_image);
        searchView = findViewById(R.id.search_bar_category);
        recyclerView = findViewById(R.id.listsongbycategory);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String category = getIntent().getStringExtra("category_name");
        String image = getIntent().getStringExtra("category_image");
        textView.setText(category);
        Glide.with(this).load(image).into(imageView);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListSong");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListSong listSong = dataSnapshot.getValue(ListSong.class);
                    if (listSong != null && listSong.getCategory().contains(category)) {
                        listSong.setKey(dataSnapshot.getKey());
                        List.add(listSong);
                    }
                }
                if (List.size() > 0) {
                    songAdapter = new MyAdaper(SongByCategoryActivity.this, List);
                    recyclerView.setAdapter(songAdapter);
                } else {
                    Toast.makeText(SongByCategoryActivity.this, "Không có bài hát nào của thể loại  này.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SongByCategoryActivity.this, "Không thể tải dữ liệu.", Toast.LENGTH_SHORT).show();
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
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}