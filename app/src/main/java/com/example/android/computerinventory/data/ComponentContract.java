package com.example.android.computerinventory.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public final class ComponentContract {

    private ComponentContract() {
    }


    public static final String CONTENT_AUTHORITY = "com.example.android.computerinventory";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_COMPONENTS = "components";


    public static final class ComponentEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COMPONENTS);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMPONENTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMPONENTS;

        public final static String TABLE_NAME = "components";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_COMPONENT_NAME = "name";

        public final static String COLUMN_COMPONENT_PRICE = "price";

        public final static String COLUMN_COMPONENT_QUANTITY = "quantity";

        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        public final static String COLUMN_SUPPLIER_EMAIL = "supplier_email";

        public final static String COLUMN_COMPONENT_IMAGE = "image";

        public static Uri buildComponentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
