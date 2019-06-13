package com.example.pocket;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> implements View.OnLongClickListener {

    private static final String TAG = "SongList";

    private ActionMode mActionMode;
    private ArrayList<SongInfo> mSongList;
    private OnItemClickListener mListener;

    public SongAdapter(ArrayList<SongInfo> mSongList) {
        this.mSongList = mSongList;
    }


    @Override
    public boolean onLongClick(View view) {
        if (mActionMode != null) {
            return false;
        }
        //Toast.makeText(this)

        return true;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
        return new SongViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int i) {
        if (mSongList.isEmpty()) {
            Log.i(TAG, "Empty");
        }
        SongInfo currentItem = mSongList.get(i);

        Uri uri = ContentUris.withAppendedId(currentItem.getsArtworkUriNotification(),
                currentItem.getAlbum_id());
        Picasso.get().load(uri).error(R.drawable.ic_empty_music).into(songViewHolder.mImageView);

        songViewHolder.mTextView1.setText(currentItem.getName());
        songViewHolder.mTextView2.setText(currentItem.getArtist());
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        /*public TextView mTextView3;*/

        public SongViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.album_art);
            mTextView1 = itemView.findViewById(R.id.song_name);
            mTextView2 = itemView.findViewById(R.id.artist_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
