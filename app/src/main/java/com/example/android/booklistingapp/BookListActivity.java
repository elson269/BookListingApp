package com.example.android.booklistingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity {

    public static final String LOG_TAG = BookListActivity.class.getSimpleName();
    String searchValue;
    private static final String GOOGLE_BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";
    public static BookAdapter adapter;
    ListView booksListView;
    TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);
        searchValue = getIntent().getStringExtra("searchable");
        adapter = new BookAdapter(BookListActivity.this, new ArrayList<Book>());
        booksListView = (ListView) findViewById(R.id.list);
        booksListView.setAdapter(adapter);
        BookAsyncTask task = new BookAsyncTask();
        task.execute();
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {
        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            URL url = createUrl(GOOGLE_BOOKS_REQUEST_URL + searchValue + "&maxResults=40");
            String jsonResponse = " ";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
            }
            ArrayList<Book> bookList = extractFeatureFromJson(jsonResponse);
            return bookList;
        }

        @Override
        protected void onPostExecute(final ArrayList<Book> books) {
            if (books == null) {
                booksListView.setEmptyView(findViewById(R.id.emptyView));
                emptyTextView = (TextView) findViewById(R.id.emptyView);
                emptyTextView.setText(getString(R.string.reminder_no_return_result_text));
            } else {
                adapter.clear();
                adapter.addAll(books);
                adapter.notifyDataSetChanged();
            }
        }

        private URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            if (url == null) {
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Errors response code:" + urlConnection.getResponseCode());
                }
            } catch (IOException e) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {

                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private ArrayList<Book> extractFeatureFromJson(String bookJson) {
            ArrayList<Book> books = new ArrayList<>();
            try {
                JSONObject rootJsonResponse = new JSONObject(bookJson);
                int totalItems = rootJsonResponse.getInt("totalItems");
                if (totalItems == 0) {
                    return null;
                } else {
                    JSONArray itemsArray = rootJsonResponse.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemObject = itemsArray.getJSONObject(i);

                        JSONObject volumeInfoObject = itemObject.getJSONObject("volumeInfo");
                        String title = volumeInfoObject.getString("title");

                        String description;
                        if (volumeInfoObject.has("description")) {
                            description = volumeInfoObject.getString("description");
                        } else {
                            description = "Book description is not available.";
                        }

                        String[] authorStringArray;
                        if (volumeInfoObject.has("authors")) {
                            JSONArray authorsArray = volumeInfoObject.getJSONArray("authors");
                            authorStringArray = jsonArrayStringToString(authorsArray);
                        } else {
                            authorStringArray = new String[1];
                            authorStringArray[0] = "Authors Unavailable!";
                        }

                        JSONObject imageLinksObject = volumeInfoObject.getJSONObject("imageLinks");
                        String imageLink = imageLinksObject.getString("smallThumbnail");
                        Bitmap bmp = null;
                        try {
                            bmp = BitmapFactory.decodeStream(createUrl(imageLink).openConnection().getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        JSONObject saleInfoObject = itemObject.getJSONObject("saleInfo");
                        String listPrice;
                        if (saleInfoObject.has("retailPrice")) {
                            JSONObject listPriceObject = saleInfoObject.getJSONObject("listPrice");
                            String amount = listPriceObject.getString("amount");
                            String currencyCode = listPriceObject.getString("currencyCode");
                            listPrice = currencyCode + ": " + amount;
                        } else {
                            listPrice = "Price Unavailable";
                        }
                        books.add(new Book(title, authorStringArray, description, listPrice, bmp));
                    }
                    return books;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            }
            return null;
        }

        public String[] jsonArrayStringToString(JSONArray jsonArray) {
            String[] authorString = new String[jsonArray.length()];
            for (int j = 0; j < jsonArray.length(); j++) {
                try {
                    authorString[j] = jsonArray.get(j).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < authorString.length - 1; i++) {
                authorString[i] = authorString[i] + ", ";
            }
            return authorString;
        }
    }
}