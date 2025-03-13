package com.example.pkt.Classes;

public class Artist {
    String imageArtist, name;

    public Artist() {
    }
    public Artist(String imageArtist, String name) {
        this.imageArtist = imageArtist;
        this.name = name;
    }


    public String getImageArtist() {
        return imageArtist;
    }

    public void setImageArtist(String imageArtist) {
        this.imageArtist = imageArtist;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Artist{" +
                "imageArtist='" + imageArtist + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
