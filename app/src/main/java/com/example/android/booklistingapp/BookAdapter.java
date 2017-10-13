package com.example.android.booklistingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        final Book currentBook = getItem(position);

        TextView titleTextView = (TextView) convertView.findViewById(R.id.book_title);
        titleTextView.setText(currentBook.getTitle());

        TextView authorTextView = (TextView) convertView.findViewById(R.id.book_author);
        authorTextView.setText(currentBook.getAuthor());

        TextView priceTextView = (TextView) convertView.findViewById(R.id.book_price);
        priceTextView.setText(currentBook.getPrice());

        TextView descriptionTextView = (TextView) convertView.findViewById(R.id.book_description);
        descriptionTextView.setText(currentBook.getDescription());

        ImageView bookCoverImageView = (ImageView) convertView.findViewById(R.id.book_image);
        bookCoverImageView.setImageBitmap(currentBook.getImageBitmap());

        return convertView;
    }

}