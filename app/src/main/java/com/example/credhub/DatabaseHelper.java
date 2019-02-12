package com.example.credhub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jaime García on 08,febrero,2019
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper( Context context, String name, SQLiteDatabase.CursorFactory factory, int version ) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL(Constantes.CREATE_TABLE);
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL(Constantes.DROP_TABLE);
        onCreate(db);
    }
}
