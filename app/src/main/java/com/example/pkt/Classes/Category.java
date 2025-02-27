package com.example.pkt.Classes;

public class Category {
    String imageCate, name;


    public Category() {
    }

    public Category(String imageCate, String name) {
        this.imageCate = imageCate;
        this.name = name;
    }

    public String getImageCate() {
        return imageCate;
    }

    public void setImageCate(String imageCate) {
        this.imageCate = imageCate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "imageCate='" + imageCate + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
