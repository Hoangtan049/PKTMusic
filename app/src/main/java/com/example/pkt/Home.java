package com.example.pkt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;

    ArrayList<Album> albumList;
    AlbumAdapter albumAdapter;
    MyAdaper myAdaper;
    ArrayList<ListSong> list;
    ImageSlider mainslider;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Slider
        mainslider = (ImageSlider) view.findViewById(R.id.image_slider);
        final List<SlideModel> remoteimages = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("ListSong")
                .orderByChild("View") // Sắp xếp theo trường 'views'
                .limitToLast(3) // Chỉ lấy 3 bài hát có lượt xem cao nhất
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ListSong> topSongs = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            ListSong song = data.getValue(ListSong.class);
                            if (song != null) {
                                topSongs.add(song);
                            }
                        }

                        // Đảo ngược danh sách để bài hát có view cao nhất đứng đầu
                        Collections.reverse(topSongs);

                        // Thêm bài hát vào slider
                        for (ListSong song : topSongs) {
                            remoteimages.add(new SlideModel(
                                    song.getImage(),  // URL hình ảnh
                                    song.getName(),   // Tên bài hát
                                    ScaleTypes.CENTER_CROP
                            ));
                        }

                        // Cập nhật slider
                        mainslider.setImageList(remoteimages, ScaleTypes.CENTER_CROP);
                        mainslider.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onItemSelected(int position) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Bạn đã chọn: " + topSongs.get(position).getName(),
                                        Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void doubleClick(int position) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Lỗi khi lấy danh sách bài hát: " + error.getMessage());
                    }
                });
        //ListSong
        recyclerView = view.findViewById(R.id.listmusic);
        databaseReference = FirebaseDatabase.getInstance().getReference("ListSong");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        myAdaper = new MyAdaper(getContext(), list);
        recyclerView.setAdapter(myAdaper);
        CardView cardViewArtist = view.findViewById(R.id.cardview_artist);
        CardView cardViewCate = view.findViewById(R.id.cardview_category);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListSong listSong = dataSnapshot.getValue(ListSong.class);
                    if (listSong != null) {
                        listSong.setKey(dataSnapshot.getKey());
                        list.add(listSong);

                        Log.d("FirebaseData", "ListSong Object: " + listSong);

                    }

                }

                myAdaper.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Click Ca Sĩ
        cardViewArtist.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đã nhấn vào Ca Sĩ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), ArtistListActivity.class); // Đổi Activity theo nhu cầu
            startActivity(intent);
        });
        // Click THể Loại
        cardViewCate.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đã nhấn vào Thể Loại", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), CategoryListActivity.class);
            startActivity(intent);
        });
        // Album
        RecyclerView recyclerView1 = view.findViewById(R.id.listalbum);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Album");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                albumList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Album album = dataSnapshot.getValue(Album.class);
                    if (album != null) {
                        albumList.add(album);
                        Log.d("FirebaseData1", "List Album Object: " + album);
                    }
                }
                if (albumList.size() > 0) {
                    albumAdapter = new AlbumAdapter(getContext(), albumList);
                    recyclerView1.setAdapter(albumAdapter);
                    albumAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }


}