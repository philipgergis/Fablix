package edu.uci.ics.fabflixmobile.ui.movielist;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleMovieActivity extends AppCompatActivity {
    private TextView name;
    private TextView year;
    private TextView director;
    private TextView genres;
    private TextView stars;

    private final String host = "34.209.143.181";
    private final String port = "8443";
    private final String domain = "cs122b-fall22-team-20";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get JSON Info
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        setContentView(R.layout.singlemovie);
        name = findViewById(R.id.singleMovieTitle);
        year = findViewById(R.id.singleMovieYear);
        director = findViewById(R.id.singleMovieDirector);
        genres = findViewById(R.id.singleMovieGenres);
        stars = findViewById(R.id.singleMovieStars);

        Intent current = getIntent();
        Bundle extras = current.getExtras();

        String urlToUse = baseURL + "/api/single-movie?" +
                "id=" + extras.get("movie_id");

        final JsonArrayRequest displayRequest = new JsonArrayRequest(Request.Method.GET, urlToUse, null, response -> {
            // TODO: should parse the json response to redirect to appropriate functions
            //  upon different response value.
            Log.d("single.success", String.valueOf(response));
            Log.d("single.url", urlToUse);

            try {
                JSONObject j = response.getJSONObject(0);
                name.setText("Movie title: " + j.getString("movie_title"));
                year.setText("Movie year: " + j.getString("movie_year"));
                director.setText("Movie director: " +j.getString("movie_director"));
                genres.setText("Movie genres: " +j.getString("movie_genre"));
                stars.setText("Movie stars: " +j.getString("movie_star"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                });
        queue.add(displayRequest);
    }
}
