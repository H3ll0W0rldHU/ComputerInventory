package com.example.android.computerinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.squareup.picasso.Picasso;

import com.example.android.computerinventory.data.ComponentContract.ComponentEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.example.android.computerinventory.data.ComponentProvider.LOG_TAG;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_COMPONENT_LOADER = 0;
    private static final int IMAGE_REQUEST_CODE = 0;

    private Uri mCurrentComponentUri;

    private EditText mComponentNameEditText;
    private String mComponentName;
    private EditText mPriceEditText;
    private ImageView mImageView;
    private Uri imageUri;
    private Button mChangePictureButton;
    private TextView mQuantityTextView;
    private Button mMinusQuantity;
    private int mCurrentQuantity;
    private Button mPlusQuantity;
    private EditText mSupplierNameEditText;
    private EditText mSupplierEmailEditText;
    private String mSupplierName;
    private String mSupplierEmail;

    private boolean mComponentHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mComponentHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentComponentUri = intent.getData();

        if (mCurrentComponentUri == null) {

            setTitle(getString(R.string.editor_activity_title_new_component));

            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_component));

            getLoaderManager().initLoader(EXISTING_COMPONENT_LOADER, null, this);
        }

        mComponentNameEditText = (EditText) findViewById(R.id.edit_component_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_component_price);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mChangePictureButton = (Button) findViewById(R.id.change_image_button);
        mQuantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        mMinusQuantity = (Button) findViewById(R.id.quantity_minus_button);
        mPlusQuantity = (Button) findViewById(R.id.quantity_plus_button);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierEmailEditText = (EditText) findViewById(R.id.edit_supplier_email);


        mComponentNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mChangePictureButton.setOnTouchListener(mTouchListener);
        mMinusQuantity.setOnTouchListener(mTouchListener);
        mPlusQuantity.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierEmailEditText.setOnTouchListener(mTouchListener);
    }


    private void saveComponent() {
        String componentNameString = mComponentNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierEmailString = mSupplierEmailEditText.getText().toString().trim();

        mSupplierEmail = supplierEmailString;

        if (TextUtils.isEmpty(componentNameString) || TextUtils.isEmpty(priceString) ||
                mCurrentQuantity == 0 || TextUtils.isEmpty(supplierNameString) || TextUtils.isEmpty(supplierEmailString) || imageUri == null) {
            Toast.makeText(this, getString(R.string.please_fill_all_fields),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ComponentEntry.COLUMN_COMPONENT_NAME, componentNameString);
        values.put(ComponentEntry.COLUMN_COMPONENT_PRICE, priceString);
        values.put(ComponentEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(ComponentEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString);
        values.put(ComponentEntry.COLUMN_COMPONENT_QUANTITY, mCurrentQuantity);
        values.put(ComponentEntry.COLUMN_COMPONENT_IMAGE, imageUri.toString());

        if (mCurrentComponentUri == null) {

            Uri newUri = getContentResolver().insert(ComponentEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_component_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_component_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentComponentUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_component_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_component_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentComponentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveComponent();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mComponentHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mComponentHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ComponentEntry._ID,
                ComponentEntry.COLUMN_COMPONENT_NAME,
                ComponentEntry.COLUMN_COMPONENT_PRICE,
                ComponentEntry.COLUMN_COMPONENT_QUANTITY,
                ComponentEntry.COLUMN_SUPPLIER_NAME,
                ComponentEntry.COLUMN_SUPPLIER_EMAIL,
                ComponentEntry.COLUMN_COMPONENT_IMAGE};

        return new CursorLoader(this,
                mCurrentComponentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }


        if (cursor.moveToFirst()) {

            int componentNameColumnIndex = cursor.getColumnIndex(ComponentEntry.COLUMN_COMPONENT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ComponentEntry.COLUMN_COMPONENT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ComponentEntry.COLUMN_COMPONENT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ComponentEntry.COLUMN_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(ComponentEntry.COLUMN_SUPPLIER_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(ComponentEntry.COLUMN_COMPONENT_IMAGE);

            String name = cursor.getString(componentNameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            mCurrentQuantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            String image = cursor.getString(imageColumnIndex);

            mComponentNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityTextView.setText(Integer.toString(mCurrentQuantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierEmailEditText.setText(supplierEmail);

            if (TextUtils.isEmpty(image)) {
                mImageView.setImageResource(R.drawable.media);
            } else {
                imageUri = Uri.parse(image);
                Picasso.with(this).load(imageUri).into(mImageView);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mComponentNameEditText.setText("");
        mPriceEditText.setText("");
        mImageView.setImageResource(R.drawable.media);
        mQuantityTextView.setText("");
        mSupplierNameEditText.setText("");
        mSupplierEmailEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteComponent();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteComponent() {
        if (mCurrentComponentUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentComponentUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_component_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_component_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    public void decreaseQuantity(View view) {
        if (mCurrentQuantity > 0) {
            mCurrentQuantity--;
            displayQuantity();
        }
    }

    public void increaseQuantity(View view) {
        mCurrentQuantity++;
        displayQuantity();
    }

    public void displayQuantity() {
        mQuantityTextView.setText(Integer.toString(mCurrentQuantity));
    }

    public void orderMore(View view) {
        mComponentName = mComponentNameEditText.getText().toString().trim();
        mSupplierName = mSupplierNameEditText.getText().toString().trim();
        mSupplierEmail = mSupplierEmailEditText.getText().toString().trim();

        if (!mSupplierEmail.equals("") && !mSupplierName.equals("") && !mComponentName.equals("") && mSupplierEmail != null && mSupplierName != null && mComponentName != null) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mSupplierEmail});
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_start) + " " + mSupplierName + ",\n" + getString(R.string.email_body_1) + " " + mComponentName + "\n\n" + getString(R.string.email_body_2));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, getString(R.string.email_error_message),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void changeImage(View view) {
        Intent intent = new Intent();

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select image"), IMAGE_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && (resultCode == RESULT_OK)) {
            try {
                imageUri = data.getData();

                int takeFlags = data.getFlags();
                takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                try {
                    getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                mImageView.setImageBitmap(getBitmapFromUri(imageUri));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }
}