package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.main.MainPageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {

    private final String host = "34.209.143.181";
    private final String port = "8443";
    private final String domain = "cs122b-fall22-team-20";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    private final ArrayList<Movie> movies = new ArrayList<>();
    private Button prev;
    private Button next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);

        Intent current = getIntent();
        Bundle extras = current.getExtras();

        next.setOnClickListener(view -> nextPage());
        if(Integer.valueOf((String) extras.get("offset")) > 0)
        {
            prev.setOnClickListener(view -> prevPage());
        }
        else
        {
            prev.setVisibility(View.GONE);
        }

        // TODO: this should be retrieved from the backend server

        // Get JSON Info
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        String urlToUse = baseURL + "/api/movie-list?" +
                "queryType=" + extras.get("queryType") +
                "&sort=" + extras.get("sort") +
                "&ratingSort=" + extras.get("ratingSort") +
                "&titleSort=" + extras.get("titleSort") +
                "&limitNum=" + extras.get("limitNum") +
                "&offset=" + extras.get("offset") +
                "&searchTitle=" + extras.get("searchTitle");


        final JsonArrayRequest loginRequest = new JsonArrayRequest(Request.Method.GET, urlToUse, null, response -> {
            // TODO: should parse the json response to redirect to appropriate functions
            //  upon different response value.
            Log.d("list.success", String.valueOf(response));
            Log.d("list.url", urlToUse);

            for(int i = 0; i < response.length(); i++)
            {
                try {
                    JSONObject j = response.getJSONObject(i);
                    Movie temp = new Movie(j.getString("movie_title"), Short.valueOf(j.getString("movie_year")), j.getString("movie_director"), j.getString("movie_genre"), j.getString("movie_star"), j.getString("movie_id"));
                    // Add movies through JSON info
                    movies.add(temp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Display Info on UI
            MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Movie movie = movies.get(position);
                String movieID = (String) movie.getId();
                Intent singleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                singleMoviePage.putExtra("movie_id", movieID);
                startActivity(singleMoviePage);
//                @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            });

        },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                });

        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);

    }

    @SuppressLint("SetTextI18n")
    private void nextPage(){
        Intent i = getIntent();
        i.putExtra("offset", String.valueOf(Integer.valueOf((String) i.getExtras().get("offset")) + 1));
        finish();
        startActivity(i);
    }

    @SuppressLint("SetTextI18n")
    private void prevPage(){
        Intent i = getIntent();
        i.putExtra("offset", String.valueOf(Integer.valueOf((String) i.getExtras().get("offset")) - 1));
        finish();
        startActivity(i);
    }
}
