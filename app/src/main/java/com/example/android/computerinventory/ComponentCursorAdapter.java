package com.example.android.computerinventory;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.computerinventory.data.ComponentContract.ComponentEntry;


public class ComponentCursorAdapter extends CursorAdapter {


    public ComponentCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    public static class ComponentViewHolder {
        public final TextView mNameTextView;
        public final TextView mPriceQuantityTextView;
        public final Button mSaleButton;


        public ComponentViewHolder(View view) {
            mNameTextView = (TextView) view.findViewById(R.id.name);
            mPriceQuantityTextView = (TextView) view.findViewById(R.id.price_quantity);
            mSaleButton = (Button) view.findViewById(R.id.sale_button);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        ComponentViewHolder componentViewHolder = new ComponentViewHolder(view);
        view.setTag(componentViewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ComponentViewHolder componentViewHolder = (ComponentViewHolder) view.getTag();

        int nameColumnIndex = cursor.getColumnIndex(ComponentEntry.COLUMN_COMPONENT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ComponentEntry.COLUMN_COMPONENT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ComponentEntry.COLUMN_COMPONENT_QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(ComponentEntry._ID);

        final String name = cursor.getString(nameColumnIndex);
        final int price = cursor.getInt(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final int id = cursor.getInt(idColumnIndex);

        String priceQuantityConcatenated = "";

        switch (quantity) {
            case 0:
                priceQuantityConcatenated = "$" + price + ", not in stock.";
                break;

            case 1:
                priceQuantityConcatenated = "$" + price + ", only 1 left.";
                break;

            default:
                priceQuantityConcatenated = "$" + price + ", " + quantity + " pcs left.";
                break;
        }

        componentViewHolder.mNameTextView.setText(name);
        componentViewHolder.mPriceQuantityTextView.setText(priceQuantityConcatenated);


        componentViewHolder.mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    Uri componentUri = ContentUris.withAppendedId(ComponentEntry.CONTENT_URI, id);
                    ContentValues values = new ContentValues();
                    values.put(ComponentEntry.COLUMN_COMPONENT_QUANTITY, quantity - 1);
                    int rowsUpdated = context.getContentResolver().update(componentUri, values, null, null);
                    if (rowsUpdated <= 0) {
                        Toast.makeText(context, context.getString(R.string.toast_error_sale),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.toast_error_quantity),
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

}
