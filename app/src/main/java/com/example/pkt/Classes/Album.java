package com.example.pkt.Classes;

public class Album {
    String imageAlbum, name;

    public Album() {
    }

    public Album(String imageAlbum, String name) {
        this.imageAlbum = imageAlbum;
        this.name = name;
    }

    public String getImageAlbum() {
        return imageAlbum;
    }

    public void setImageAlbum(String imageAlbum) {
        this.imageAlbum = imageAlbum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Album{" +
                "imageAlbum='" + imageAlbum + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
