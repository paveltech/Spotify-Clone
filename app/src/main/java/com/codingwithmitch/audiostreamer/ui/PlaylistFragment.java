package com.codingwithmitch.audiostreamer.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codingwithmitch.audiostreamer.R;
import com.codingwithmitch.audiostreamer.adapters.PlaylistRecyclerAdapter;
import com.codingwithmitch.audiostreamer.api.ApiClient;
import com.codingwithmitch.audiostreamer.api.ApiInterface;
import com.codingwithmitch.audiostreamer.models.Artist;
import com.codingwithmitch.audiostreamer.pojo.SongItem;
import com.codingwithmitch.audiostreamer.pojo.SongResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlaylistFragment extends Fragment implements
        PlaylistRecyclerAdapter.IMediaSelector
{

    private static final String TAG = "PlaylistFragment";

    // UI Components
    private RecyclerView mRecyclerView;
    protected static final String basePath = "http://storage.googleapis.com/automotive-media/";

    // Vars
    private PlaylistRecyclerAdapter mAdapter;
    public ApiInterface apiInterface;
    private ArrayList<MediaMetadataCompat> mMediaList = new ArrayList<>();
    private ArrayList<SongItem> songItemArrayList = new ArrayList<>();
    private IMainActivity mIMainActivity;
    private String mSelectedCategory;
    //private Artist mSelectArtist;
    private SongItem mSelectedMedia;

    public static PlaylistFragment newInstance(String category, Artist artist){
        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        args.putParcelable("artist", artist);
        playlistFragment.setArguments(args);
        return playlistFragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            mIMainActivity.setActionBarTitle("Hello");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            //mSelectedCategory = getArguments().getString("category");
            //mSelectArtist = getArguments().getParcelable("artist");
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        apiInterface = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        retrieveMedia();
        mIMainActivity.setActionBarTitle("Hello");
        Toast.makeText(getActivity(), "fragment", Toast.LENGTH_SHORT).show();


        return view;
    }

    private void retrieveMedia(){
        mIMainActivity.showPrgressBar();

        Toast.makeText(getActivity(), "api call ", Toast.LENGTH_SHORT).show();

        apiInterface.getSongs().enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                if (response.isSuccessful()){
                    songItemArrayList = response.body().getMusic();
                    setAdapterIn(response.body().getMusic());
                    for(SongItem songItem: response.body().getMusic()){
                        addToMediaList(songItem);
                    }
                    updateDataSet();
                }
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {

            }
        });
    }

    private void setAdapterIn(ArrayList<SongItem> songItemArrayList){
        mAdapter = new PlaylistRecyclerAdapter(getActivity(), songItemArrayList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void addToMediaList(SongItem songItem) {
        MediaMetadataCompat media = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, ""+songItem.getDuration())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songItem.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songItem.getTitle())
                //.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, document.getString(getString(R.string.field_media_url)))
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, "https://vod.rockerzs.com/music/numb/master.m3u8")
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, songItem.getSource())
                //.putString(MediaMetadataCompat.METADATA_KEY_DATE, document.getDate(getString(R.string.field_date_added)).toString())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, basePath+songItem.getImage())
                .build();


        mMediaList.add(media);
    }

    private void updateDataSet(){
        mIMainActivity.hideProgressBar();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity) getActivity();
    }

    @Override
    public void onMediaSelected(int position) {
        mIMainActivity.getMyApplicationInstance().setMediaItems(mMediaList);

        mSelectedMedia = songItemArrayList.get(position);

        mAdapter.setSelectedIndex(position);
        mIMainActivity.onMediaSelected(
                ""+mSelectedMedia.getDuration(), // playlist_id = artist_id
                mMediaList.get(position),
                position);
        //saveLastPlayedSongProperties();
    }

    public void updateUI(SongItem songItem){
        mAdapter.setSelectedIndex(getIndexOfItem(songItem));
        mSelectedMedia = songItem;
        //saveLastPlayedSongProperties();
    }


    public int getIndexOfItem(SongItem mediaItem){
        String item = ""+mediaItem.getDuration();
        for(int i = 0; i<mMediaList.size(); i++ ){
            if(mMediaList.get(i).getDescription().getMediaId().equals(item)){
                return i;
            }
        }
        return -1;
    }
}















