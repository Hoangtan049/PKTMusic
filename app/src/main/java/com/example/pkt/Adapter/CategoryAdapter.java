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
import com.example.pkt.Classes.Category;
import com.example.pkt.R;
import com.example.pkt.View.SongByCategoryActivity;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    Context context;
    ArrayList<Category> catelist;

    public CategoryAdapter(Context context, ArrayList<Category> catelist) {
        this.context = context;
        this.catelist = catelist;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.itemartist, parent, false);

        return new CategoryAdapter.CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        Category category = catelist.get(position);
        holder.textViewCate.setText(category.getName());
        Glide.with(context)
                .load(category.getImageCate())
                .into(holder.imageViewCate);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SongByCategoryActivity.class);
            intent.putExtra("category_name", category.getName());
            intent.putExtra("category_image", category.getImageCate());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        return catelist.size();
    }

    public void searchDatalist(ArrayList<Category> searchlist) {
        catelist = searchlist;
        notifyDataSetChanged();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCate;
        TextView textViewCate;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCate = itemView.findViewById(R.id.artistsName);
            imageViewCate = itemView.findViewById(R.id.imgartist);
        }
    }
}
