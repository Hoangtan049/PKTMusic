package com.example.pkt;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Favorite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Favorite extends Fragment {
    RecyclerView recyclerView;

    MyAdaper myAdaper;
    TextView textview;
    ArrayList<ListSong> list;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Favorite() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Favorite.
     */
    // TODO: Rename and change types and number of parameters
    public static Favorite newInstance(String param1, String param2) {
        Favorite fragment = new Favorite();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private void loadFavoriteSongs() {

        if (list.isEmpty()) {

            recyclerView.setVisibility(View.GONE);
            textview.setVisibility(View.VISIBLE);
        } else {

            recyclerView.setVisibility(View.VISIBLE);
            textview.setVisibility(View.GONE);
        }


        myAdaper.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = view.findViewById(R.id.listmusicfavorite);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        textview = view.findViewById(R.id.no_favorite_text);
        list = new ArrayList<>();
        myAdaper = new MyAdaper(getContext(), list);
        recyclerView.setAdapter(myAdaper);


        DatabaseReference favoriteRef = FirebaseDatabase.getInstance().getReference("favorites");
        favoriteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListSong song = dataSnapshot.getValue(ListSong.class); // Chuyển đổi đối tượng
                    if (song != null && Boolean.TRUE.equals(song.getFavorite())) {
                        list.add(song); // Thêm vào danh sách hiển thị
                    }
                    Log.d("FavoriteFragment", "Loaded song: " + song.getName());
                }

                loadFavoriteSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });



        return view;
    }
}