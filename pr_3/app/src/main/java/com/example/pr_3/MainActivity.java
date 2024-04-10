package com.example.pr_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String URL = "https://android-api-eosin.vercel.app/products";
    Button btnSave;
    EditText txtname, txtprice, txtqty, txtdate;
    ListView lstProduct;
    ArrayAdapter<String> adapter;

    // Variables to hold the selected item's data
    String selectedId, selectedName, selectedPrice, selectedQty, selectedDate;
    RequestQueue queue ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(getApplicationContext());
        Control();
        RequestJSON();
        ButtonClick();
        // Register ListView for context menu
        registerForContextMenu(lstProduct);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.update) {
            // Get selected item's data
            Product selectedProduct = (Product) lstProduct.getItemAtPosition(info.position);
            selectedId = selectedProduct.id;
            selectedName = selectedProduct.productName;
            selectedPrice = selectedProduct.price;
            selectedQty = selectedProduct.quantity;
            selectedDate = Product.formatDate(selectedProduct.date);

            // Fill form with selected item's data
            txtname.setText(selectedName);
            txtprice.setText(selectedPrice);
            txtqty.setText(selectedQty);
            txtdate.setText(selectedDate);

            // Show update button
            btnSave.setText("Update");

            return true;
        } else if (item.getItemId() == R.id.delete) {

            Product selectedProduct = (Product) lstProduct.getItemAtPosition(info.position);
            String selectedItemId = selectedProduct.id;
            // Make a request to the delete API
            deleteItem(selectedItemId);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void RequestJSON() {
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            ArrayList<Product> productList = new ArrayList<Product>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject dataObject = jsonArray.getJSONObject(i);
                                String productId = dataObject.getString("_id");
                                String productName = dataObject.getString("Product_Name");
                                String price = String.valueOf(dataObject.getDouble("Price"));
                                String quantity = String.valueOf(dataObject.getInt("Quantity"));
                                String purchaseDate = dataObject.getString("Purchase_Date");
                                Product product = new Product(productId , productName , price , quantity , purchaseDate);
                                productList.add(product);
                            }

                            ProductAdapter adapter = new ProductAdapter(getApplicationContext(), productList);
                            lstProduct.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void ButtonClick() {

        txtdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtname.getText().toString();
                String price = txtprice.getText().toString();
                String qty = txtqty.getText().toString();
                String date = txtdate.getText().toString();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Product_Name", name);
                    jsonObject.put("Price", price);
                    jsonObject.put("Quantity", qty);
                    jsonObject.put("Purchase_Date", date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Check if btnSave is set to "Update"
                if (btnSave.getText().toString().equals("Update")) {
                    String updateURL = URL + "/" + selectedId;
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, updateURL, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Handle successful response
                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                                    RequestJSON();
                                    btnSave.setText("Save");
                                    clearForm();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Handle error response
                                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                }
                            });

                    queue.add(request);
                }

                else {
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Handle successful response
                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                                    clearForm();
                                    RequestJSON();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Handle error response
                                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                    queue.add(request);
                }
            }
        });
    }

    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        if (!txtdate.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = dateFormat.parse(txtdate.getText().toString());
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Create a DatePickerDialog and set the initial date to the current date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the selected date in txtdate EditText in yyyy-MM-dd format
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        txtdate.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void deleteItem(String itemId) {
        String deleteURL = URL + "/" + itemId; // Assuming itemId is appended to the delete URL

        StringRequest request = new StringRequest(Request.Method.DELETE, deleteURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response after deletion
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        // Refresh the list after deletion
                        RequestJSON();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);
    }

    private void Control() {
        btnSave = findViewById(R.id.btnSave);
        txtname = findViewById(R.id.txtProduct);
        txtdate = findViewById(R.id.txtDate);
        txtprice = findViewById(R.id.txtPrice);
        txtqty = findViewById(R.id.txtQty);
        lstProduct = findViewById(R.id.lstProduct);
    }

    private void clearForm() {
        txtname.setText("");
        txtprice.setText("");
        txtqty.setText("");
        txtdate.setText("");
    }
}
