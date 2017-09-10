package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;

import static android.R.attr.button;
import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static java.security.AccessController.getContext;

/**
 * Created by Demo on 2017-09-09.
 */

public class ProductCursorAdaptor extends CursorAdapter {

    private static final String LOG_TAG = ProductCursorAdaptor.class.getSimpleName();

    public ProductCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        int idIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int id = cursor.getInt(idIndex);
        final Uri uri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI , id);

        Button decrementButton = (Button) view.findViewById(R.id.product_quantity_decremint_button);
        //decrementButton.setTag();
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementQuantity(context, cursor, uri);
            }
        });

        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText("$" + productPrice + ".00");
        quantityTextView.setText(productQuantity);

    }


    private void decrementQuantity(Context context, Cursor cursor, Uri uri) {
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY));
        int newQuantity = quantity - 1;
        String name = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME));
        Log.e(LOG_TAG, "name: " + name);
        Log.e(LOG_TAG, "quantity: " + quantity);
        Log.e(LOG_TAG, "newquantity: " + newQuantity);
        Log.e(LOG_TAG, "URI: " + uri);

        if (newQuantity < 0) {
            Toast.makeText(context, context.getResources().getString(R.string.decrement_error), Toast.LENGTH_SHORT).show();

        } else {
            ContentValues values = new ContentValues();

            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantity);
            int rowAffected = context.getContentResolver().update(uri, values, null, null);
            context.getContentResolver().notifyChange(uri, null);

            if (rowAffected == 0) {
                Toast.makeText(context, context.getString(R.string.decrement_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
