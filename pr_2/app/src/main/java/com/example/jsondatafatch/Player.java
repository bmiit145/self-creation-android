
package com.example.jsondatafatch;

import android.graphics.Bitmap;

public class Player {

    public String name;
    public String country;
    public String city;
    public Bitmap image;

    public Player(String name, String country, String city, Bitmap image) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.image = image;
    }
}
