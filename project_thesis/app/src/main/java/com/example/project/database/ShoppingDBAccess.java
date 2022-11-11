package com.example.project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.project.model.GroceryItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingDBAccess {

    private static final String TABLE_NAME = "groceries";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "grocery_name";
    private static final String COLUMN_AMOUNT = "grocery_amount";
    private static final String COLUMN_UNIT = "grocery_unit";
    private static final String COLUMN_DESCRIPTION = "grocery_description";

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static ShoppingDBAccess instance;
    Cursor c = null;

    private ShoppingDBAccess(Context context) {
        this.openHelper = new ShoppingDBHandler(context);
    }

    public static ShoppingDBAccess getInstance(Context context) {
        if (instance == null) {
            instance = new ShoppingDBAccess(context);
        }
        return instance;
    }

    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null) {
            this.db.close();
        }
    }

    //CRUD
    //Create
    public long addGrocery(GroceryItem groceryItem) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, groceryItem.name);
        cv.put(COLUMN_AMOUNT, groceryItem.amount);
        cv.put(COLUMN_UNIT, groceryItem.unit);
        cv.put(COLUMN_DESCRIPTION, groceryItem.description);
        return db.insert(TABLE_NAME, null, cv);
    }

    //ReadAll
    public List<GroceryItem> getGroceries() {
        List<GroceryItem> groceries = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                GroceryItem groceryItem = new GroceryItem(Integer.parseInt(c.getString(0)),
                        c.getString(1), Float.parseFloat(c.getString(2)),
                        c.getString(3), c.getString(4));
                groceries.add(groceryItem);
            }
            while (c.moveToNext());
        }
        c.close();
        return groceries;
    }

    //Update
    public void updateGrocery(GroceryItem groceryItem) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, groceryItem.name);
        cv.put(COLUMN_AMOUNT, groceryItem.amount);
        cv.put(COLUMN_UNIT, groceryItem.unit);
        cv.put(COLUMN_DESCRIPTION, groceryItem.description);
        db.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(groceryItem.id)});
    }

    //Delete
    public void deleteGrocery(int id) {
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
    }

}
