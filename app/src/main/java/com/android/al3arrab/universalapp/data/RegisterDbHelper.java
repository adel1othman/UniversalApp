package com.android.al3arrab.universalapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.al3arrab.universalapp.data.RegisterContract.UserEntry;

public class RegisterDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = RegisterDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;

    public RegisterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_USERS_TABLE =  "CREATE TABLE " + UserEntry.TABLE_NAME + " ("
                + UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UserEntry.COLUMN_USER_STATUS + " TEXT NOT NULL, "
                + UserEntry.COLUMN_USER_CODE + " TEXT, "
                + UserEntry.COLUMN_USER_NAME + " TEXT NOT NULL, "
                + UserEntry.COLUMN_USER_SURNAME + " TEXT NOT NULL, "
                + UserEntry.COLUMN_USER_EMAIL + " TEXT NOT NULL, "
                + UserEntry.COLUMN_USER_PASSWORD + " TEXT NOT NULL, "
                + UserEntry.COLUMN_USER_IMAGE + " TEXT);";


        db.execSQL(SQL_CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}