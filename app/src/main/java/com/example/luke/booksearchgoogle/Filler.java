package com.example.luke.booksearchgoogle;
import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class Filler extends AsyncTaskLoader<List<Book>> {

    private String queryString;

    public Filler(Context context, String queryString) {
        super(context);
        this.queryString = queryString;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public List<Book> loadInBackground() {

        if (queryString == null) {
            return null;
        }
        List<Book> books = Query.fetchData(queryString);

        return books;
    }
}
