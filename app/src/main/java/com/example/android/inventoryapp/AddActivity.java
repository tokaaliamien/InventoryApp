package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddActivity extends AppCompatActivity {

    private final int CAMERA_REQUEST = 0;
    private final int GALLERY_REQUEST = 1;
    private EditText nameEditText;
    private EditText quantityEditText;
    private EditText priceEditText;
    private EditText supplierEditText;
    private ImageView image;
    private ImageButton button;
    private int userChoosenMethod;

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        supplierEditText = (EditText) findViewById(R.id.supplier_edit_text);
        image = (ImageView) findViewById(R.id.img);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST)
                onSelectGalleryResult(data);
            else if (requestCode == CAMERA_REQUEST)
                onSelectCameraReult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        image.setImageBitmap(bm);
    }

    private void onSelectCameraReult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        image.setImageBitmap(thumbnail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                insertProduct();
                return true;
            case R.id.action_add_photo:
                selectImage();
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
        else {

            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, name);
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, price);
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER, supplier);

            Bitmap bitmap;
            BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
            if (image.getDrawable() == null)
                bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.no_photo);
            else
                bitmap = drawable.getBitmap();


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

    private void selectImage() {
        final String[] menuItems = {"Take Photo", "Choose from Gallary", "Canel"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Photo");
        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean permissionGranted = Utility.checkPermission(AddActivity.this);
                switch (item) {
                    case 0:
                        if (permissionGranted)
                            cameraIntent();
                        break;
                    case 1:
                        if (permissionGranted)
                            galleryIntent();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
                userChoosenMethod = item;
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), GALLERY_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switch (userChoosenMethod) {
                        case 0:
                            cameraIntent();
                            break;
                        case 1:
                            galleryIntent();
                            break;
                    }
                } else {
                    Log.e("Add Actitivy", "permission denied");
                }
                break;
        }
    }
}
