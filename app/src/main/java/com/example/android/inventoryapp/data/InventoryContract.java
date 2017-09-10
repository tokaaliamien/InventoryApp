package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Demo on 2017-09-09.
 */

public class InventoryContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS="products";

    public final static class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public final static String TABLE_NAME="products";

        public final static String _ID=BaseColumns._ID;
        public final static String COLUMN_INVENTORY_NAME="name";
        public final static String COLUMN_INVENTORY_PRICE="price";
        public final static String COLUMN_INVENTORY_QUANTITY="quantity";
        public final static String COLUMN_INVENTORY_SUPPLIER="supplier";
        public final static String COLUMN_INVENTORY_IMG="img";




    }
}
