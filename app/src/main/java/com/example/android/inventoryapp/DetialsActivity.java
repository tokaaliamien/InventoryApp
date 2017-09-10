package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;

import java.io.ByteArrayOutputStream;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.widget.ImageView.ScaleType.CENTER_CROP;


public class DetialsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetialsActivity.class.getSimpleName();
    private static final int EXSITING_PRODUCTS_LOADER = 0;
    TextView nameTextView;
    TextView quantityTextView;
    TextView priceTextView;
    TextView supplierTextView;
    ImageView imgView;
    String productSupplier = null;
    String productName = "";
    int productQuantity = 0;
    int productPrice = 0;
    Bitmap img;
    private Uri currentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detials);

        Intent intent = getIntent();
        currentProductUri = intent.getData();
        getLoaderManager().initLoader(EXSITING_PRODUCTS_LOADER, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.send_email);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productSupplier != null) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setType("text/plain");
                    intent.setData(Uri.parse("mailto:" + productSupplier));
                    startActivity(intent);
                }
            }
        });

        Button incrementButton = (Button) findViewById(R.id.increment_button);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuantity();
            }
        });

        Button decrementButton = (Button) findViewById(R.id.decrement_button);
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementQuantity();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detials, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_product:
                showDeleteConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        int rowDeleted = getContentResolver().delete(currentProductUri, null, null);
        if (rowDeleted == 0) {
            Toast.makeText(this, getString(R.string.failed_to_delete_product), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.success_to_delete_product), Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMG};

        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1)
            return;

        if (cursor.moveToFirst()) {
            nameTextView = (TextView) findViewById(R.id.name);
            quantityTextView = (TextView) findViewById(R.id.quantity);
            priceTextView = (TextView) findViewById(R.id.price);
            imgView = (ImageView) findViewById(R.id.img);
            supplierTextView = (TextView) findViewById(R.id.supplier);


            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int imgCoulmnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMG);
            int supplierCoulmnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER);

            productName = cursor.getString(nameColumnIndex);
            productQuantity = cursor.getInt(quantityColumnIndex);
            productPrice = cursor.getInt(priceColumnIndex);
            productSupplier = cursor.getString(supplierCoulmnIndex);
            byte[] imgArray = cursor.getBlob(imgCoulmnIndex);


            nameTextView.setText(productName);
            priceTextView.setText("$" + productPrice + ".00");
            quantityTextView.setText(productQuantity + "");
            supplierTextView.setText(productSupplier);
            if (imgArray != null) {
                img = BitmapFactory.decodeByteArray(imgArray, 0, imgArray.length);
                imgView.setImageBitmap(img);
            } else {
                imgView.setImageResource(R.drawable.no_photo);
            }

            imgView.setScaleType(CENTER_CROP);

        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
        nameTextView.setText("");
        quantityTextView.setText("");
        priceTextView.setText("");
        supplierTextView.setText("");
        imgView.setImageResource(R.drawable.no_photo);

    }

    private void incrementQuantity() {
        Log.e(LOG_TAG, "quantity: " + productQuantity);

        int newQuantity = productQuantity + 1;

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantity);

        int rowAffected = getContentResolver().update(currentProductUri, values, null, null);

        if (rowAffected == 0)
            Toast.makeText(this, getString(R.string.increment_failed), Toast.LENGTH_SHORT).show();

    }


    public void decrementQuantity() {
        Log.e(LOG_TAG, "quantity: " + productQuantity);
        int newQuantity = productQuantity - 1;
        if (newQuantity < 0) {
            Toast.makeText(this, getString(R.string.decrement_error), Toast.LENGTH_SHORT).show();

        } else {
            ContentValues values = new ContentValues();

            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantity);
            int rowAffected = getContentResolver().update(currentProductUri, values, null, null);

            if (rowAffected == 0) {
                Toast.makeText(this, getString(R.string.increment_failed), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
