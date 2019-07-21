package com.example.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.inventoryapp.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {

    /**
     * Database helper that will provide us access to the database
     */
    private InventoryDbHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        mDbHelper = new InventoryDbHelper(this);

        Button btn = findViewById(R.id.insert_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertProduct();
                displayDatabaseInfo();
            }
        });
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
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER};

        // Perform a query on the pets table
        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,   // The table to query
                projection,                  // The columns to return
                null,               // The columns for the WHERE clause
                null,            // The values for the WHERE clause
                null,                // Don't group the rows
                null,                 // Don't filter by row groups
                null);               // The sort order

        TextView displayView = findViewById(R.id.text_view_inventory);

        try {
            // Create a header in th TextView that looks like this:
            //
            // The inventory table contains <number of rows in Cursor> products.
            //
            //
            displayView.setText("The inventory table contains " + cursor.getCount() + " products.\n\n");
            displayView.append(InventoryEntry._ID + "-" +
                    InventoryEntry.COLUMN_PRODUCT_NAME + "-" +
                    InventoryEntry.COLUMN_PRODUCT_PRICE + "-" +
                    InventoryEntry.COLUMN_PRODUCT_QUANTITY + "-" +
                    InventoryEntry.COLUMN_PRODUCT_SUPPLIER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);

                // Display the values from each column of the current row in the cursor
                // in the TextView
                displayView.append("\n" + currentID + "-" +
                        currentName + "-" +
                        currentPrice + "-" +
                        currentQuantity + "-" +
                        currentSupplier);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded product data into the database.
     * For debugging purposes only.
     */
    private void insertProduct() {
        // Get the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and product attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Delmonte");
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 10);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 200);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, "Jumia");
        values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE, R.drawable.delmonte_image);

        // Insert a new row for the product in the database, returning the ID for that new row.
        // The first argument for db.insert is the inventory table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a new row when
        // there are no values).
        // The third argument is the ContentValues object containing the info of the product.
        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);
    }
}
