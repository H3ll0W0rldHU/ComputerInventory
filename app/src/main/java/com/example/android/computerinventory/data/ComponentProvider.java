package com.example.android.computerinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.computerinventory.data.ComponentContract.ComponentEntry;


public class ComponentProvider extends ContentProvider {

    public static final String LOG_TAG = ComponentProvider.class.getSimpleName();

    private static final int COMPONENTS = 100;

    private static final int COMPONENT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(com.example.android.computerinventory.data.ComponentContract.CONTENT_AUTHORITY, ComponentContract.PATH_COMPONENTS, COMPONENTS);

        sUriMatcher.addURI(com.example.android.computerinventory.data.ComponentContract.CONTENT_AUTHORITY, ComponentContract.PATH_COMPONENTS + "/#", COMPONENT_ID);
    }

    private com.example.android.computerinventory.data.ComponentDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new com.example.android.computerinventory.data.ComponentDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case COMPONENTS:
                cursor = database.query(ComponentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COMPONENT_ID:

                selection = ComponentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ComponentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COMPONENTS:
                return insertComponent(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertComponent(Uri uri, ContentValues values) {
        String name = values.getAsString(ComponentEntry.COLUMN_COMPONENT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Component requires a name");
        }

        Integer price = values.getAsInteger(ComponentEntry.COLUMN_COMPONENT_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Component requires a valid price");
        }

        Integer quantity = values.getAsInteger(ComponentEntry.COLUMN_COMPONENT_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Component requires a valid quantity");
        }

        String supplierName = values.getAsString(ComponentEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Component requires a supplier name");
        }

        String supplierEmail = values.getAsString(ComponentEntry.COLUMN_SUPPLIER_EMAIL);
        if (supplierEmail == null) {
            throw new IllegalArgumentException("Component requires a supplier email");
        }

        // No need to check the image, as it isn't mandatory.

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ComponentEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }


        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COMPONENTS:
                return updateComponent(uri, contentValues, selection, selectionArgs);
            case COMPONENT_ID:

                selection = ComponentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateComponent(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateComponent(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ComponentEntry.COLUMN_COMPONENT_NAME)) {
            String name = values.getAsString(ComponentEntry.COLUMN_COMPONENT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Component requires a name");
            }
        }

        if (values.containsKey(ComponentEntry.COLUMN_COMPONENT_PRICE)) {
            Integer price = values.getAsInteger(ComponentEntry.COLUMN_COMPONENT_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Component requires a valid price");
            }
        }

        if (values.containsKey(ComponentEntry.COLUMN_COMPONENT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ComponentEntry.COLUMN_COMPONENT_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Component requires a valid quantity");
            }
        }

        if (values.containsKey(ComponentEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(ComponentEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Component requires a supplier name");
            }
        }
        if (values.containsKey(ComponentEntry.COLUMN_SUPPLIER_EMAIL)) {
            String supplierEmail = values.getAsString(ComponentEntry.COLUMN_SUPPLIER_EMAIL);
            if (supplierEmail == null) {
                throw new IllegalArgumentException("Component requires a supplier email");
            }
        }

        // No need to check the image, as it isn't mandatory.

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ComponentEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COMPONENTS:
                rowsDeleted = database.delete(ComponentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COMPONENT_ID:
                selection = ComponentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ComponentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COMPONENTS:
                return ComponentEntry.CONTENT_LIST_TYPE;
            case COMPONENT_ID:
                return ComponentEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}