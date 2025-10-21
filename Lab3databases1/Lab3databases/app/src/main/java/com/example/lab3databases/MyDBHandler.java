package com.example.lab3databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "products.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "products";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PRICE = "price";

    public MyDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // make table if it doesnt exist
        String createTable = "CREATE TABLE " + TABLE + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT, "
                + COL_PRICE + " DOUBLE)";
        db.execSQL(createTable);

        // adds 2 starter items
        addStarterProduct(db, "Apple", 1.50);
        addStarterProduct(db, "Banana", 0.99);
    }

    private void addStarterProduct(SQLiteDatabase db, String name, double price) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_PRICE, price);
        db.insert(TABLE, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // rebuilds the table if version changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // add new product
    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, product.getProductName());
        cv.put(COL_PRICE, product.getProductPrice());
        db.insert(TABLE, null, cv);
        db.close();
    }

    // get all products
    public List<Product> getAllProducts() {
        return queryProducts(null, null);
    }

    // find products by name (starts with)
    public List<Product> findProductsByName(String name) {
        return queryProducts(name, null);
    }

    // find products by exact price
    public List<Product> findProductsByPrice(double price) {
        return queryProducts(null, price);
    }

    // find by both name and price
    public List<Product> findProductsByNameAndPrice(String name, double price) {
        return queryProducts(name, price);
    }

    // handles all the queries
    private List<Product> queryProducts(String name, Double price) {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        if (name != null && !name.isEmpty() && price != null) {
            String sql = "SELECT * FROM " + TABLE + " WHERE " + COL_NAME + " LIKE ? AND " + COL_PRICE + " = ?";
            c = db.rawQuery(sql, new String[]{name + "%", String.valueOf(price)});
        } else if (name != null && !name.isEmpty()) {
            String sql = "SELECT * FROM " + TABLE + " WHERE " + COL_NAME + " LIKE ?";
            c = db.rawQuery(sql, new String[]{name + "%"});
        } else if (price != null) {
            String sql = "SELECT * FROM " + TABLE + " WHERE " + COL_PRICE + " = ?";
            c = db.rawQuery(sql, new String[]{String.valueOf(price)});
        } else {
            c = db.rawQuery("SELECT * FROM " + TABLE, null);
        }

        if (c.moveToFirst()) {
            do {
                Product p = new Product();
                p.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
                p.setProductName(c.getString(c.getColumnIndexOrThrow(COL_NAME)));
                p.setProductPrice(c.getDouble(c.getColumnIndexOrThrow(COL_PRICE)));
                list.add(p);
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    // delete product by name (case sensitive)
    public void deleteProduct(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, COL_NAME + "=?", new String[]{name});
        db.close();
    }
}
