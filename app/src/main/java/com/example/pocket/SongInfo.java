package com.example.pocket;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class SongInfo implements Parcelable {

    final public static Uri sArtworkUriNotification = Uri
            .parse("content://media/external/audio/albumart");
    public static final Creator<SongInfo> CREATOR = new Creator<SongInfo>() {
        @Override
        public SongInfo createFromParcel(Parcel in) {
            return new SongInfo(in);
        }

        @Override
        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };
    private String name;
    private String artist;
    private String data;
    private long getAlbum_id;

    public SongInfo(String name, String artist, String data, long getAlbum_id) {
        this.name = name;
        this.artist = artist;
        this.data = data;
        this.getAlbum_id = getAlbum_id;
    }

    private SongInfo(Parcel in) {
        name = in.readString();
        artist = in.readString();
        data = in.readString();
    }

    public Uri getsArtworkUriNotification() {
        return sArtworkUriNotification;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getData() {
        return data;
    }

    public long getAlbum_id() {
        return getAlbum_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(name);
        dest.writeString(artist);
        dest.writeString(data);
    }
}
