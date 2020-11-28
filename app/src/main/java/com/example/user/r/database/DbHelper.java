package com.example.user.r.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TABLE_CREATE;
    final private static Integer VERSION = 1;
    final private Context mContext;

    public static final String ID = "id";
    public static final String NAME = "naziv";
    public static final String LOKACIJA = "lokacija";
    public static final String TABLE_NAME = "restorani";

    static {
        TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT NOT NULL, " +
                LOKACIJA + " TEXT NOT NULL)" ;

    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    public DbHelper(Context context) {
        this(context, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    void deleteDatabase1() {
        mContext.deleteDatabase(TABLE_NAME);
    }
}
