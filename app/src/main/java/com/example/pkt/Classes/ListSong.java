package com.example.pkt.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class ListSong implements Parcelable {
    private String Image, Name, Artist, URL, Category, Album, Lyrics, Key;
    private Boolean Favorite;
    int View;

    public ListSong() {
    }
    public ListSong(String image, String name, String artist, String URL, String category, String album, String lyrics, String key, Boolean favorite, int view) {
        Image = image;
        Name = name;
        Artist = artist;
        this.URL = URL;
        Category = category;
        Album = album;
        Lyrics = lyrics;
        Favorite = favorite;
        View = view;
        Key = key;
    }

    // Parcelable implementation
    protected ListSong(Parcel in) {
        Image = in.readString();
        Name = in.readString();
        Artist = in.readString();
        URL = in.readString();
        Category = in.readString();
        Album = in.readString();
        Lyrics = in.readString();
        Favorite = in.readByte() != 0;
        View = in.readInt();
        Key = in.readString();
    }

    public static final Creator<ListSong> CREATOR = new Creator<ListSong>() {
        @Override
        public ListSong createFromParcel(Parcel in) {
            return new ListSong(in);
        }

        @Override
        public ListSong[] newArray(int size) {
            return new ListSong[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Image);
        dest.writeString(Name);
        dest.writeString(Artist);
        dest.writeString(URL);
        dest.writeString(Category);
        dest.writeString(Album);
        dest.writeString(Lyrics);
        dest.writeByte((byte) (Favorite ? 1 : 0));
        dest.writeInt(View);
        dest.writeString(Key);
    }



    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public int getView() {
        return View;
    }

    public void setView(int view) {
        View = view;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public String getLyrics() {
        return Lyrics;
    }

    public void setLyrics(String lyrics) {
        Lyrics = lyrics;
    }

    public Boolean getFavorite() {
        return Favorite;
    }

    public void setFavorite(Boolean favorite) {
        Favorite = favorite;
    }

    @Override
    public String toString() {
        return "ListSong{" +
                "Image='" + Image + '\'' +
                ", Name='" + Name + '\'' +
                ", Artist='" + Artist + '\'' +
                ", URL='" + URL + '\'' +
                ", Category='" + Category + '\'' +
                ", Album='" + Album + '\'' +
                ", Lyrics='" + Lyrics + '\'' +
                ", Key='" + Key + '\'' +
                ", Favorite=" + Favorite +
                ", View=" + View +
                '}';
    }
}
