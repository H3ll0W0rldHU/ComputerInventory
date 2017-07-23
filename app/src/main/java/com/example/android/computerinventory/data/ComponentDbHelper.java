package com.example.android.computerinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.computerinventory.data.ComponentContract.ComponentEntry;

public class ComponentDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "computerinventory.db";

    public ComponentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_COMPONENTS_TABLE = "CREATE TABLE " + ComponentEntry.TABLE_NAME +
                " (" + ComponentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ComponentEntry.COLUMN_COMPONENT_NAME + " TEXT NOT NULL,"
                + ComponentEntry.COLUMN_COMPONENT_PRICE + " INTEGER NOT NULL,"
                + ComponentEntry.COLUMN_COMPONENT_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
                + ComponentEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL,"
                + ComponentEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL,"
                + ComponentEntry.COLUMN_COMPONENT_IMAGE + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_COMPONENTS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ComponentEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


}
