package com.example.pocket;

import android.app.Notification;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.pocket.ChannelClass.CHANNEL_ID;
import static com.example.pocket.MainActivity.POSITION;
import static com.example.pocket.MainActivity.SONG_ARRAY_KEY;

public class SongService extends Service {

    public static final int NOTIFICATION_ID = 501;
    private static final String TAG = "SongService";

    private int pos;
    private MediaPlayer player = new MediaPlayer();
    private ArrayList<SongInfo> exList;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        exList = intent.getParcelableArrayListExtra(SONG_ARRAY_KEY);
        pos = intent.getIntExtra(POSITION, 0);

        playMusic();

        return START_NOT_STICKY;
    }

    private void playMusic() {
        final SongInfo nowPlaying = exList.get(pos);
        final String name = nowPlaying.getName();
        final String artist = nowPlaying.getArtist();
        final String input = nowPlaying.getData();
        long album_id = nowPlaying.getAlbum_id();

        try {
            player.setDataSource(input);
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    player.reset();
                    nextSong();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Cannot Play This Song", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }

        int sec1 = (player.getDuration() / 1000) % 60;
        int min1 = (player.getDuration() / 1000) / 60;
        final String tot = String.format(Locale.getDefault(), "%02d:%02d", min1, sec1);

        Uri uri = ContentUris.withAppendedId(nowPlaying.getsArtworkUriNotification(),
                album_id);

        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.ic_empty_music)
                .error(R.drawable.ic_empty_music)
                .into(new Target() {
                    Bitmap bitmap_icon;
                    Notification notification;

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap_icon = bitmap;
                        loadNotification();
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Log.i(TAG, "onBitmapFailed: ");
                        loadNotification();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Log.i(TAG, "onPrepareLoad: ");
                    }

                    private void loadNotification() {
                        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setContentTitle(name)
                                .setContentText(artist)
                                .setSmallIcon(R.drawable.ic_audiotrack)
                                .setLargeIcon(bitmap_icon)
                                .addAction(R.drawable.ic_pause_small, "Pause", null)
                                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                                        .setShowActionsInCompactView(0))
                                .setSubText(tot)
                                .build();
                        startForeground(NOTIFICATION_ID, notification);
                    }

                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }

    public void nextSong() {
        if (pos == exList.size() - 1)
            pos = 0;
        else
            pos++;
        player.reset();
        playMusic();
    }
}
