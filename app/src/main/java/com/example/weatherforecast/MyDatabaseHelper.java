package com.example.weatherforecast;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * @author dhx
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_TABLE_DIARY="create table weather (" +
            "id integer primary key autoincrement, "
            +"province text, " +  "city text, "  +
            "adcode text, " + "weather text," +"temperature text,"+"humidity text,"+
            "reporttime text)";

    private Context mContext;

    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DIARY);
        Toast.makeText(mContext,"create db succeeded!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_TABLE_DIARY);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
