package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.inventoryapp.data.InventoryDbHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CatalogActivity extends AppCompatActivity {

    /**
     * Database helper that will provide us access to the database
     */
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FloatingActionButton to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        mDbHelper = new InventoryDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the Inventory database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER,
                InventoryEntry.COLUMN_PRODUCT_IMAGE};

        // Perform a query on the pets table
        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,   // The table to query
                projection,                  // The columns to return
                null,               // The columns for the WHERE clause
                null,            // The values for the WHERE clause
                null,                // Don't group the rows
                null,                 // Don't filter by row groups
                null);               // The sort order

    }

    /**
     * Helper method to insert hardcoded product data into the database.
     * For debugging purposes only.
     */
    private void insertProduct() {
        // Get the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Get image from drawable
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.delmonte_image);
        // Convert Bitmap to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte imageInByte[] = stream.toByteArray();

        // Create a ContentValues object where column names are the keys,
        // and product attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Delmonte");
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 10);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 200);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, "Jumia");
        values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE, imageInByte);

        // Insert a new row for the product in the database, returning the ID for that new row.
        // The first argument for db.insert is the inventory table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a new row when
        // there are no values).
        // The third argument is the ContentValues object containing the info of the product.
        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);
        // Close database connection
        db.close();
    }
}
