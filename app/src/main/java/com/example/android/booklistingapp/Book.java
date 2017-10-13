package com.example.android.booklistingapp;

import android.graphics.Bitmap;

public class Book {
    private String title;
    private String[] author;
    private String description;
    private String price;
    private Bitmap image;

    public Book(String title, String[] author , String description, String price, Bitmap image) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        StringBuilder authorStringBuilder = new StringBuilder();
        for (String s : author) {
            authorStringBuilder.append(s);
        }
        return authorStringBuilder.toString();
    }

    public String getDescription( ) { return description; }

    public String getPrice( ) { return price; }

    public Bitmap getImageBitmap ( ) { return image; }
}