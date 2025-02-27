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
import com.example.pkt.Classes.Artist;
import com.example.pkt.R;
import com.example.pkt.View.SongsByArtistActivity;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    Context context;
    ArrayList<Artist> artistlist;


    public ArtistAdapter(Context context, ArrayList<Artist> artistlist) {
        this.context = context;
        this.artistlist = artistlist;
    }


    @NonNull
    @Override
    public ArtistAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.itemartist, parent, false);

        return new ArtistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistAdapter.ArtistViewHolder holder, int position) {
        Artist artist = artistlist.get(position);
        holder.artistName.setText(artist.getName());
        Glide.with(context)
                .load(artist.getImageArtist())
                .into(holder.imageartist);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SongsByArtistActivity.class);
            intent.putExtra("artist_name", artist.getName());
            intent.putExtra("artist_image", artist.getImageArtist());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return artistlist.size();
    }

    public void searchDatalist(ArrayList<Artist> searchlist) {
        artistlist = searchlist;
        notifyDataSetChanged();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView artistName;
        ImageView imageartist;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);

            artistName = itemView.findViewById(R.id.artistsName);
            imageartist = itemView.findViewById(R.id.imgartist);
        }
    }
}
