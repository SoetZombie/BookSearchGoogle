package com.example.luke.booksearchgoogle;

import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private BookAdapter bookAdapter;
    private SearchView searchView;
    private TextView bookNotFound;
    private String query;
    private ListView bookListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bookNotFound = (TextView) findViewById(R.id.book_not_found);
        searchView = (SearchView) findViewById(R.id.search_bar);
        searchView.setSubmitButtonEnabled(true);
        final List<Book> books = new ArrayList<>();
        bookAdapter = new BookAdapter(this, books);
        bookListView = (ListView) findViewById(R.id.list);
        bookListView.setAdapter(bookAdapter);
         final ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo networkInfo = cm.getActiveNetworkInfo();
         final boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (isConnected) {
            getLoaderManager().initLoader(0, null, this);
            bookNotFound.setVisibility(View.GONE);
        } else {
            bookListView.setVisibility(View.GONE);
            bookNotFound.setVisibility(View.VISIBLE);
            bookNotFound.setText(R.string.not_connected_to_internet);

        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                boolean checkInternet = networkInfo != null && networkInfo.isConnectedOrConnecting();
                bookListView.setVisibility(View.VISIBLE);
                if (checkInternet) {

                    getLoaderManager().restartLoader(0, null, MainActivity.this);
                    bookNotFound.setVisibility(View.GONE);
                    return true;


                } else {
                    bookListView.setVisibility(View.GONE);
                    bookNotFound.setVisibility(View.VISIBLE);
                    bookNotFound.setText(R.string.not_connected_to_internet);


                    return false;
                }
            }

        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        query = searchView.getQuery().toString();

        return new Filler(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        bookAdapter.clear();

        if (books != null && !books.isEmpty()) {
            bookAdapter.addAll(books);
            bookNotFound.setVisibility(View.GONE);

        } else {
            bookListView.setVisibility(View.GONE);
            bookNotFound.setVisibility(View.VISIBLE);
            bookNotFound.setText(R.string.not_existing_book);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        query = savedInstanceState.getString("query");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        query = searchView.getQuery().toString();
        outState.putString("query", query);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        bookAdapter.clear();
    }


}
