package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Demo on 2017-09-09.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="inventory.db";
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE="CREATE TABLE "+InventoryContract.InventoryEntry.TABLE_NAME+" ("
                + InventoryContract.InventoryEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME+ " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER+ " TEXT NOT NULL , "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY+ " INTEGER NOT NULL DEFAULT 0, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE+" INTEGER NOT NULL DEFAULT 0, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMG+" BLOB NOT NULL);";
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
