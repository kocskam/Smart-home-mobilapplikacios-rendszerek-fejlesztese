package com.example.project.database;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class ShoppingDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "shopping.db";
    private static final int DATABASE_VERSION = 1;

    public ShoppingDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

}
