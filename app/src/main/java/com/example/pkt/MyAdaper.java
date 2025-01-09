package com.example.pkt;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdaper extends RecyclerView.Adapter<MyAdaper.MyViewHolder> {
    Context context;
    ArrayList<ListSong> list;

    public MyAdaper(Context context, ArrayList<ListSong> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ListSong listSong = list.get(position);
        holder.namemusic.setText(listSong.getName());
        holder.artist.setText(listSong.getArtist());
        Glide.with(context)
                .load(listSong.getImage())
                .into(holder.imagemusic);

        // Cập nhật icon theo trạng thái yêu thích
        if (Boolean.TRUE.equals(listSong.getFavorite())) {
            holder.fvrt_btn.setImageResource(R.drawable.baseline_favorite_24); // Icon yêu thích
        } else {
            holder.fvrt_btn.setImageResource(R.drawable.sharp_favorite_border_24); // Icon không yêu thích
        }

        holder.fvrt_btn.setOnClickListener(v -> {
            DatabaseReference favoritesRef = FirebaseDatabase.getInstance()
                    .getReference("favorites")
                    .child(listSong.getName());

            boolean isFavorite = Boolean.TRUE.equals(listSong.getFavorite());

            if (isFavorite) {
                // Nếu đã yêu thích -> Xóa khỏi Favorites
                favoritesRef.removeValue();
                listSong.setFavorite(false);
                Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu chưa yêu thích -> Thêm vào Favorites
                listSong.setFavorite(true);
                favoritesRef.setValue(listSong);
                Toast.makeText(context, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }

            // Cập nhật giao diện
            notifyItemChanged(holder.getAdapterPosition());
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
//            intent.putExtra("song_name", listSong.getName());
//            intent.putExtra("song_url", listSong.getURL());
//            intent.putExtra("song_image", listSong.getImage());
//            intent.putExtra("artist", listSong.getArtist());
//            intent.putExtra("lyric", listSong.getLyrics());
            intent.putParcelableArrayListExtra("song_list", list);
            intent.putExtra("current_song_index",position);
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void searchDatalist(ArrayList<ListSong> searchlist) {
        list = searchlist;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView namemusic, artist;
        ImageView imagemusic;
        ImageButton fvrt_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            namemusic = itemView.findViewById(R.id.namemusics);
            artist = itemView.findViewById(R.id.artists);
            imagemusic = itemView.findViewById(R.id.imgmusic);
            fvrt_btn = itemView.findViewById(R.id.favorite_icon);
        }
    }

}