package com.example.pkt.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pkt.Classes.Album;
import com.example.pkt.R;
import com.example.pkt.View.SongByAlbumActivity;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    Context context;
    ArrayList<Album> albumlist;

    public AlbumAdapter(Context context, ArrayList<Album> albumlist) {
        this.context = context;
        this.albumlist = albumlist;
    }

    @NonNull
    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.itemalbum, parent, false);

        return new AlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.AlbumViewHolder holder, int position) {
        Album album = albumlist.get(position);
        holder.albumName.setText(album.getName());
        Glide.with(context)
                .load(album.getImageAlbum())
                .into(holder.imageAlbum);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SongByAlbumActivity.class);
            intent.putExtra("album_name", album.getName());
            intent.putExtra("album_image", album.getImageAlbum());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return albumlist.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView albumName;
        ImageView imageAlbum;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.textalbum);
            imageAlbum = itemView.findViewById(R.id.imagealbum);
        }
    }
}
