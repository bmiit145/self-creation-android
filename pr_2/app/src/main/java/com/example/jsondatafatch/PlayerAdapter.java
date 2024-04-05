package com.example.jsondatafatch;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class PlayerAdapter extends ArrayAdapter<Player> {

    private Context context;
    private ArrayList<Player> players;

    public PlayerAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
        this.context = context;
        this.players = players;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        Player currentPlayer = players.get(position);

        TextView textView = convertView.findViewById(R.id.textView);
        ImageView imageView = convertView.findViewById(R.id.imageView);

        textView.setText(currentPlayer.name + " - " + currentPlayer.country + " (" + currentPlayer.city + ")");
        imageView.setImageBitmap(currentPlayer.image);

        return convertView;
    }
}
