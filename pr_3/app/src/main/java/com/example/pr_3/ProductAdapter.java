package com.example.pr_3;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Context mContext;
    private ArrayList<Product> mProductList;

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        super(context, 0, productList);
        mContext = context;
        mProductList = productList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_product, parent, false);
        }

        Product currentProduct = mProductList.get(position);

        TextView productNameTextView = listItem.findViewById(R.id.productNameTextView);
        productNameTextView.setText(currentProduct.productName);

        TextView priceTextView = listItem.findViewById(R.id.priceTextView);
        priceTextView.setText("Price: " + currentProduct.price);

        TextView quantityTextView = listItem.findViewById(R.id.quantityTextView);
        quantityTextView.setText("Quantity: " + currentProduct.quantity);

        TextView dateTextView = listItem.findViewById(R.id.dateTextView);
        dateTextView.setText("Purchase Date: " + currentProduct.getFormattedDate());

        // for id retrival
        listItem.setTag(currentProduct.id);

        return listItem;
    }
}

