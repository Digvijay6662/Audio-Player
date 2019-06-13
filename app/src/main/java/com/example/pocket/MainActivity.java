package com.example.pocket;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    public static final String TAG = "MainActivity";

    public static final String POSITION = "current_position";
    public static final String SONG_ARRAY_KEY = "song_array";
    final public static Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private SongAdapter mAdapter;
    private boolean boolIsPlaying = false;
    private ImageView mAlbumArt;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAlbumArt = findViewById(R.id.album_art_large);
        mTextView = findViewById(R.id.bottom_song_name);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No Permission,Requesting..", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }

        checkPlaying("onCreate");
        getSongListAndPopulate();
    }

    public void openPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu);
        popup.show();
    }

    private void getSongListAndPopulate() {
        final ArrayList<SongInfo> songList = new ArrayList<>();
        String[] req = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA
                , MediaStore.Audio.AudioColumns.ALBUM_ID};
        Uri uri = Uri.parse("content://media/external/audio/media");
        ContentResolver r = getContentResolver();
        Cursor cur = r.query(uri, req, null, null, MediaStore.Audio.Media.TITLE);
        //Toast.makeText(this, "total " + cur.getCount(), Toast.LENGTH_SHORT).show();
        try {
            while (cur.moveToNext()) {
                String name = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                long album_id = cur.getLong(cur.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));

                Log.i(TAG, "getSongListAndPopulate: " + album_id);

                songList.add(new SongInfo(name, artist, data, album_id));
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }

        checkPlaying("getSongListAndPopulate");

        RecyclerView mRecyclerView = findViewById(R.id.mainview);
        mRecyclerView.hasFixedSize();
        mAdapter = new SongAdapter(songList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                checkPlaying("mAdapter.setOnItemClickListener");

                Intent serviceIntent = new Intent(getBaseContext(), SongService.class);
                serviceIntent.putExtra(POSITION, position);
                serviceIntent.putExtra(SONG_ARRAY_KEY, songList);
                startService(serviceIntent);
                boolIsPlaying = true;
                mAdapter.notifyItemChanged(position);

                SongInfo si = songList.get(position);
                Uri uri = ContentUris.withAppendedId(sArtworkUri,
                        si.getAlbum_id());
                Picasso.get().load(uri).into(mAlbumArt);

                mTextView.setText(si.getName());

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (cur != null)
            cur.close();
    }

    private void checkPlaying(String uniqueID) {
        if (boolIsPlaying) {
            Log.i(TAG, "checkPlaying: " + uniqueID);
            Intent serviceIntent = new Intent(getBaseContext(), SongService.class);
            stopService(serviceIntent);
            boolIsPlaying = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    getSongListAndPopulate();
                }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "Item 1 Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                Toast.makeText(this, "Item 2 Clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    public void stopMusic(View view) {
        checkPlaying("pause button");
    }
}
