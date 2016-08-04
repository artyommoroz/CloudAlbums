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
import com.frost.cloudalbums.model.Track;
import com.frost.cloudalbums.util.TimeConverter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder>{

    private Album album;
    private List<Track> trackList;
    private Context context;
    private ItemClickListener itemClickListener;

    public TrackAdapter(Context context, Album album, ItemClickListener itemClickListener) {
        this.context = context;
        this.album = album;
        this.trackList = album.getTrackList();
        this.itemClickListener = itemClickListener;
    }

    public TrackAdapter(Context context, List<Track> trackList, ItemClickListener itemClickListener) {
        this.context = context;
        this.trackList = trackList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        Track track = trackList.get(position);
        holder.title.setText(track.getTitle());
        if (track.getDuration() != 0) {
            holder.duration.setText(String.valueOf(TimeConverter.millisToMMSS(track.getDuration())));
            String artworkUrl = album == null ? track.getArtworkUrl() : album.getArtworkUrl();
            Picasso.with(context).load(artworkUrl).fit().centerCrop()
                    .placeholder(R.drawable.ic_library_music_black_72px).into(holder.icon);
        }
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }


    public class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.duration)
        TextView duration;

        public TrackViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.trackClicked(v, getLayoutPosition());
        }
    }

    public interface ItemClickListener {
        void trackClicked(View v, int position);
    }
}
