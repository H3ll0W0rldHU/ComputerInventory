package com.example.android.computerinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.computerinventory.data.ComponentContract.ComponentEntry;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COMPONENT_LOADER = 0;

    ComponentCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView componentListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        componentListView.setEmptyView(emptyView);

        mCursorAdapter = new ComponentCursorAdapter(this, null);
        componentListView.setAdapter(mCursorAdapter);

        componentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentComponentUri = ContentUris.withAppendedId(ComponentEntry.CONTENT_URI, id);

                intent.setData(currentComponentUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(COMPONENT_LOADER, null, this);
    }

    private void insertComponent() {

        ContentValues values = new ContentValues();
        values.put(ComponentEntry.COLUMN_COMPONENT_NAME, "Udacity Z5200 motherboard");
        values.put(ComponentEntry.COLUMN_COMPONENT_PRICE, 300);
        values.put(ComponentEntry.COLUMN_COMPONENT_QUANTITY, 23);
        values.put(ComponentEntry.COLUMN_SUPPLIER_NAME, "Udacity Wholesale");
        values.put(ComponentEntry.COLUMN_SUPPLIER_EMAIL, "wholesale@udacity.com");
        values.put(ComponentEntry.COLUMN_COMPONENT_IMAGE, "android.resource://com.example.android.computerinventory/drawable/motherboard");

        Uri newUri = getContentResolver().insert(ComponentEntry.CONTENT_URI, values);
    }

    private void deleteAllComponents() {
        int rowsDeleted = getContentResolver().delete(ComponentEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from component database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertComponent();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllComponents();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ComponentEntry._ID,
                ComponentEntry.COLUMN_COMPONENT_NAME,
                ComponentEntry.COLUMN_COMPONENT_PRICE,
                ComponentEntry.COLUMN_COMPONENT_QUANTITY,};

        return new CursorLoader(this,
                ComponentEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}