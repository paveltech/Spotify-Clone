package com.codingwithmitch.audiostreamer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.audiostreamer.R;
import com.codingwithmitch.audiostreamer.models.Artist;
import com.codingwithmitch.audiostreamer.pojo.SongItem;

import java.util.ArrayList;

public class PlaylistRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "PlaylistRecyclerAdapter";

    private ArrayList<SongItem> songItemArrayList = new ArrayList<>();
    private Context mContext;
    private IMediaSelector mIMediaSelector;
    private int mSelectedIndex;

    public PlaylistRecyclerAdapter(Context context, ArrayList<SongItem> songItemArrayList, IMediaSelector mediaSelector) {
        Log.d(TAG, "PlaylistRecyclerAdapter: called.");
        this.songItemArrayList = songItemArrayList;
        this.mContext = context;
        this.mIMediaSelector = mediaSelector;
        mSelectedIndex = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_playlist_list_item, null);
        ViewHolder vh = new ViewHolder(view, mIMediaSelector);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((ViewHolder)viewHolder).title.setText(songItemArrayList.get(i).getTitle());
        ((ViewHolder)viewHolder).artist.setText(songItemArrayList.get(i).getAlbum());

        if(i == mSelectedIndex){
            ((ViewHolder)viewHolder).title.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        }
        else{
            ((ViewHolder)viewHolder).title.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return songItemArrayList.size();
    }

    public void setSelectedIndex(int index){
        mSelectedIndex = index;
        notifyDataSetChanged();
    }

    public int getSelectedIndex(){
        return mSelectedIndex;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title, artist;
        private IMediaSelector iMediaSelector;

        public ViewHolder(@NonNull View itemView, IMediaSelector categorySelector) {
            super(itemView);
            title = itemView.findViewById(R.id.media_title);
            artist = itemView.findViewById(R.id.media_artist);
            iMediaSelector = categorySelector;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iMediaSelector.onMediaSelected(getAdapterPosition());
        }
    }

    public interface IMediaSelector{
        void onMediaSelected(int position);
    }

}