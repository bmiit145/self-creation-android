package com.example.pr_3;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BASE_URL = "http://localhost:8000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Volley RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        findViewById(R.id.btnGetProducts).setOnClickListener(v -> getProducts(requestQueue));
        findViewById(R.id.btnAddProduct).setOnClickListener(v -> addProduct(requestQueue));
        findViewById(R.id.btnDeleteProduct).setOnClickListener(v -> deleteProduct(requestQueue));
    }

    private void getProducts(RequestQueue requestQueue) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    Toast.makeText(MainActivity.this, "Products: " + response.toString(), Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error
                    Toast.makeText(MainActivity.this, "Error retrieving products", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error retrieving products: " + error.toString());
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void addProduct(RequestQueue requestQueue) {
        JSONObject product = new JSONObject();
        try {
            product.put("Product_Name", "Sample Product");
            product.put("Price", 10.99);
            product.put("Quantity", 50);
            product.put("Purchase_Date", "2024-04-06");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL,
                product,
                response -> {
                    // Handle successful response
                    Toast.makeText(MainActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error
                    Toast.makeText(MainActivity.this, "Error adding product", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error adding product: " + error.toString());
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void deleteProduct(RequestQueue requestQueue) {
        int productIdToDelete = 1; // Example product ID to delete

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                BASE_URL + "?id=" + productIdToDelete,
                null,
                response -> {
                    // Handle successful response
                    Toast.makeText(MainActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error
                    Toast.makeText(MainActivity.this, "Error deleting product", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting product: " + error.toString());
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
