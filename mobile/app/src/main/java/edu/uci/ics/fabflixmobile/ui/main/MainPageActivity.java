package edu.uci.ics.fabflixmobile.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

public class MainPageActivity extends AppCompatActivity {

    private Button searchButton;
    private EditText text;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "34.209.143.181";
    private final String port = "8443";
    private final String domain = "cs122b-fall22-team-20";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);
        searchButton = findViewById(R.id.button);

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search());
    }

    @SuppressLint("SetTextI18n")
    private void search(){
        // use the same network queue across our application
        //final RequestQueue queue = NetworkManager.sharedManager(this).queue;

//        final StringRequest searchRequest = new StringRequest(Request.Method.GET, baseURL + "/api/search", response -> {
//            // TODO: should parse the json response to redirect to appropriate functions
//            //  upon different response value.
//            Log.d("search.success", response);
//
//            //initialize the activity(page)/destination
//            Intent listPage = new Intent(MainPageActivity.this, MovieListActivity.class);
//            //without starting the activity/page, nothing would happen
//            listPage.putExtra("movies", response);
//            listPage.putExtra("page", "0");
//            listPage.putExtra("search", text.getText().toString());
//
//            startActivity(listPage);
//            //Complete and destroy login activity once successful
//        },
//                error -> {
//                    // error
//                    Log.d("search.error", error.toString());
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // POST request form data
//                final Map<String, String> params = new HashMap<>();
//                params.put("searchTitle", message.getText().toString()); // ask philip about search params
//                params.put("limitNum", "20");
//                return params;
//            }
//        };
//        // important: queue.add is where the login request is actually sent
//        queue.add(searchRequest);
        Intent listPage = new Intent(MainPageActivity.this, MovieListActivity.class);
        listPage.putExtra("queryType", "search");
        listPage.putExtra("sort", "title");
        listPage.putExtra("ratingSort", "DESC");
        listPage.putExtra("titleSort", "ASC");
        listPage.putExtra("limitNum", "20");
        listPage.putExtra("offset", "0");
        listPage.putExtra("searchTitle", text.getText().toString());
        //without starting the activity/page, nothing would happen
        startActivity(listPage);
    }
}
