package com.example.jsondatafatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.ArrayList;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {



    private ListView listView;
    private ArrayList<Player> players;
    private PlayerAdapter adapter;
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        listView = findViewById(R.id.listView);
        players = new ArrayList<>();
        adapter = new PlayerAdapter(this, players);
        listView.setAdapter(adapter);

        // Instantiate the RequestQueue.
        requestQueue = Volley.newRequestQueue(this);

        fetchDataFromUrl();

    }

//    private void fetchDataFromUrl() throws IOException, JSONException {
//            HttpURLConnection connection = null;
//                BufferedReader reader = null;
//
//        URL url = new URL("https://demonuts.com/Demonuts/JsonTest/Tennis/json_parsing.php");
//        connection = (HttpURLConnection) url.openConnection();
//        connection.connect();
//
//        InputStream stream = connection.getInputStream();
//        reader = new BufferedReader(new InputStreamReader(stream));
//        StringBuilder buffer = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            buffer.append(line).append("\n");
//        }
//        String jsonString = buffer.toString();
//
//        JSONObject jsonObject = new JSONObject(jsonString);
//        JSONArray jsonArray = jsonObject.getJSONArray("data");
//
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject playerObject = jsonArray.getJSONObject(i);
//            String name = playerObject.getString("name");
//            String country = playerObject.getString("country");
//            String city = playerObject.getString("city");
//            String imgUrl = playerObject.getString("imgURL");
//
//            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imgUrl).getContent());
//            players.add(new Player(name, country, city, bitmap));
//        }
//    }


    private void fetchDataFromUrl() {
        String url = "https://demonuts.com/Demonuts/JsonTest/Tennis/json_parsing.php";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject playerObject = dataArray.getJSONObject(i);
                                String name = playerObject.getString("name");
                                String country = playerObject.getString("country");
                                String city = playerObject.getString("city");
                                String imgUrl = playerObject.getString("imgURL");

                                loadImage(imgUrl, name, country, city);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }


    private void loadImage(String imgUrl, final String name, final String country, final String city) {
        ImageRequest imageRequest = new ImageRequest(
                imgUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        players.add(new Player(name, country, city, response));
                        adapter.notifyDataSetChanged();
                    }
                },
                0,
                0,
                null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        // Add the request to the RequestQueue.
        requestQueue.add(imageRequest);
    }
}
