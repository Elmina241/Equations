package com.example.elmina.equations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by elmina on 18.12.15.
 */
public class DB extends SQLiteOpenHelper {
    public static final int VERSION = 2;
    public static final String DB_NAME = "resultsList.db";

    public interface Results extends BaseColumns {


        String TABLE_NAME = "results";

        String _ID = "id";

        String PLAYER_NAME = "task_name";

        String SCORE_NAME = "score";

        String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PLAYER_NAME + " TEXT, " + SCORE_NAME + " INTEGER" +
                        ")";

    }

    public DB(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Results.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<String[]> read() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                Results._ID,
                Results.PLAYER_NAME,
                Results.SCORE_NAME
        };
        String sortOrder = Results._ID;
        ArrayList<String[]> result = new ArrayList<String[]>();
        Cursor cursor = db.query(Results.TABLE_NAME, projection, null, null, null, null, sortOrder);
        try {
            while (cursor.moveToNext())
                result.add(new String[] {cursor.getString(1), cursor.getString(2)});

        } finally {
            cursor.close();
        }
        return result;
    }

    public void addResult(String name, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Results.PLAYER_NAME, name);
        values.put(Results.SCORE_NAME, score);
        db.insert(Results.TABLE_NAME, null, values);
    }

}
