package com.example.luke.booksearchgoogle;
import android.text.TextUtils;
import android.util.Log;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class Query {

    private static final String LOG_TAG = MainActivity.class.getName();

    public static List<Book> fetchData(String query) {
        String response = null;
        URL url = createUrl(query);


        try {
            response = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return extractFromJson(response);

    }

    private static URL createUrl(String query) {
        URL url = null;

        try {

            url = new URL("https://www.googleapis.com/books/v1/volumes?q=intitle:" + query);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        } else {
            HttpURLConnection connection = null;
            InputStream stream = null;

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(10000);
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == 200) {

                    stream = connection.getInputStream();
                    jsonResponse = readFromStream(stream);
                } else {
                    Log.e(LOG_TAG, "Error Response Code: " + connection.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
                if (stream != null) {
                    stream.close();
                }
            }

            return jsonResponse;
        }
    }


    private static String readFromStream(InputStream stream) throws IOException {

        StringBuilder builder = new StringBuilder();

        if (stream != null) {

            InputStreamReader streamReader = new InputStreamReader(stream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                builder.append(line);
                line = bufferedReader.readLine();
            }
        }

        return builder.toString();
    }

    private static List<Book> extractFromJson(String responseJson) {

        if (TextUtils.isEmpty(responseJson)) {
            return null;
        }
        List<Book> books = new ArrayList<>();

        try {
            JSONObject volumes = new JSONObject(responseJson);


            if(volumes.has("items")) {
                JSONArray items = volumes.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    JSONObject volume = item.getJSONObject("volumeInfo");
                    String title = volume.getString("title");
                    String author;
                    if (volume.has("authors")) {
                        author = volume.getJSONArray("authors").get(0).toString();
                    } else {
                        author = "";
                    }
                    Book book = new Book(author, title);
                    books.add(book);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return books;

    }

}
