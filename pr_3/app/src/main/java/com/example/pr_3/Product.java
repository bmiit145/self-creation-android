package com.example.pr_3;

import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class Product {

    public String id;
    public String productName;
    public String price;
    public String quantity;
    public String date;

    public Product(String id, String productName, String price, String quantity, String date) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
    }

    public String getFormattedDate() {
        return formatDate(date);
    }


    public static String formatDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        try {
            Date parsedDate = sdf.parse(date);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }
}
