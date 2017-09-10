package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryDbHelper;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText quantityEditText;
    private EditText priceEditText;
    private EditText supplierEditText;
    private ImageView image;
    private ImageButton button;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        supplierEditText = (EditText) findViewById(R.id.supplier_edit_text);

        button = (ImageButton) findViewById(R.id.take_img_button);
        image = (ImageView) findViewById(R.id.img);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap) data.getExtras().get("data");
        image.setImageBitmap(bitmap);
        button.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                insertProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertProduct() {
        String name = nameEditText.getText().toString().trim();
        String supplier = supplierEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        Integer quantity = null;
        if (!quantityString.isEmpty())
            quantity = Integer.parseInt(quantityString);

        String priceString = priceEditText.getText().toString().trim();
        Integer price = null;
        if (!priceString.isEmpty())
            price = Integer.parseInt(priceString);


        if (name.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.null_name), Toast.LENGTH_SHORT).show();
        } else if (price < 0 || price == null)
            Toast.makeText(this, getString(R.string.negative_price), Toast.LENGTH_SHORT).show();
        else if (quantity < 0 || quantity == null)
            Toast.makeText(this, getString(R.string.negative_quantity), Toast.LENGTH_SHORT).show();
        else if ((!isEmailValid(supplier)) || supplier.isEmpty())
            Toast.makeText(this, getString(R.string.wrong_format_email), Toast.LENGTH_SHORT).show();
        else if (bitmap == null)
            Toast.makeText(this, getString(R.string.null_image), Toast.LENGTH_SHORT).show();
        else {

            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, name);
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, price);
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER, supplier);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] imgArray = null;
            imgArray = bos.toByteArray();
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMG, imgArray);

            Uri rowsInserted = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
            if (rowsInserted == null)
                Toast.makeText(this, getString(R.string.product_insert_failure_message), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.product_insert_success_message), Toast.LENGTH_SHORT).show();

            finish();

        }

    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
