package com.frost.cloudalbums.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frost.cloudalbums.R;
import com.frost.cloudalbums.model.Album;
import com.frost.cloudalbums.util.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>{

    private List<Album> albumList;
    private Context context;
    private ItemClickListener itemClickListener;

    public AlbumAdapter(Context context, List<Album> albumList, ItemClickListener itemClickListener) {
        this.context = context;
        this.albumList = albumList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.title.setText(album.getTitle());
        Picasso.with(context).load(album.getUser().getAvatarUrl()).fit().centerCrop()
                .transform(new CircleTransform()).into(holder.icon);

        Picasso.with(context).load(album.getArtworkUrl()).fit().centerCrop()
                .placeholder(R.drawable.ic_library_music_black_72px).into(holder.albumCover);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.albumCover)
        ImageView albumCover;
        @Bind(R.id.icon)
        ImageView icon;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.albumClicked(v, getLayoutPosition());
        }
    }

    public interface ItemClickListener {
        void albumClicked(View v, int position);
    }
}
