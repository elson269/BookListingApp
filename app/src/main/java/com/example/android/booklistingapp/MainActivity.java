package com.example.android.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.search_button1);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSearchInput().isEmpty()) {
                    displayMessage(getString(R.string.reminder_no_input_text));
                } else {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                    if (isConnected) {
                        Intent bookListIntent = new Intent(MainActivity.this, BookListActivity.class);
                        bookListIntent.putExtra("searchable", getSearchInput());
                        startActivity(bookListIntent);
                    } else {
                        Toast.makeText(MainActivity.this,getString(R.string.reminder_no_network),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public String getSearchInput() {
        EditText searchInput = (EditText) findViewById(R.id.search_input);
        return searchInput.getText().toString().replaceAll(" ", "+").trim();
    }

    public void displayMessage(String reminderText) {
        TextView messageToDisplay = (TextView) findViewById(R.id.reminder_text);
        messageToDisplay.setText(reminderText);
    }
}