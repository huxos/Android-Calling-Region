package me.huxos.checkout.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Demo DB Helper
 * Created by cabe on 16/7/15.
 */
public class DemoDBHelper extends SQLiteOpenHelper {
    protected final static String TAG = "DemoDBHelper";
    private final static String DB_NAME = "demo";
    private final static int DB_VERSION = 1;

    private final static String SQL_TABLE_USER = "create table if not exists demo_table (" +
            "demo_id int," +
            "demo_name char(128)" +
            ")";
    private final static String SQL_ALTER_USER = "ALTER TABLE demo_table ADD demo_age int";

    public DemoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION + 3);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.w(TAG, "onCreate:" + SQL_TABLE_USER);
        sqLiteDatabase.execSQL(SQL_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(TAG, "onUpgrade:" + oldVersion + " # " + newVersion + "--->" + SQL_ALTER_USER);
        if(newVersion > DB_VERSION) {
            sqLiteDatabase.execSQL(SQL_ALTER_USER);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.w(TAG, "onOpen");
    }
}
