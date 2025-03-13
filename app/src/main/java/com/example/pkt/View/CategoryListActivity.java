package com.example.pkt.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.pkt.Adapter.CategoryAdapter;
import com.example.pkt.Classes.Category;
import com.example.pkt.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;

public class CategoryListActivity extends AppCompatActivity {
    RecyclerView cateRecyclerView;
    ArrayList<Category> cateList;
    SearchView searchView;
    CategoryAdapter cateAdapter;

    private String removeAccents(String text) {
        if (text == null) {
            return null;
        }
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public void searchList(String text) {
        ArrayList<Category> searchList = new ArrayList<>();
        String normalizedText = removeAccents(text);
        for (Category category : cateList) {
            String normalizedName = removeAccents(category.getName());

            if (
                    normalizedName.toLowerCase().contains(normalizedText.toLowerCase())

            ) {
                searchList.add(category);
            }
        }
        cateAdapter.searchDatalist(searchList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        setTitle("Danh sách thể loại");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchView = findViewById(R.id.search_bar_cate);
        cateRecyclerView = findViewById(R.id.listcategory);
        cateRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        cateRecyclerView.setLayoutManager(gridLayoutManager);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cateList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        cateList.add(category);
                    }
                }
                if (cateList.size() > 0) {
                    cateAdapter = new CategoryAdapter(CategoryListActivity.this, cateList);
                    cateRecyclerView.setAdapter(cateAdapter);
                    cateAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CategoryListActivity.this, "Không có dữ liệu Category", Toast.LENGTH_SHORT).show();
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
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}