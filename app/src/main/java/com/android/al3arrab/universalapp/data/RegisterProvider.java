package com.android.al3arrab.universalapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.al3arrab.universalapp.R;
import com.android.al3arrab.universalapp.data.RegisterContract.UserEntry;

public class RegisterProvider extends ContentProvider {

    public static final String LOG_TAG = RegisterProvider.class.getSimpleName();

    private static final int USERS = 100;
    private static final int USER_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(RegisterContract.CONTENT_AUTHORITY, RegisterContract.PATH_USERS, USERS);

        sUriMatcher.addURI(RegisterContract.CONTENT_AUTHORITY, RegisterContract.PATH_USERS + "/#", USER_ID);
    }

    private RegisterDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new RegisterDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                cursor = database.query(UserEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case USER_ID:
                selection = UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(UserEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.unknown_uri) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return insertUser(uri, contentValues);
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.insert_not_supported) + uri);
        }
    }

    private Uri insertUser(Uri uri, ContentValues values) {
        String name = values.getAsString(UserEntry.COLUMN_USER_NAME);
        if (name == null) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.missed_name));
        }

        String surname = values.getAsString(UserEntry.COLUMN_USER_SURNAME);
        if (surname == null) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.missed_surname));
        }

        String email = values.getAsString(UserEntry.COLUMN_USER_EMAIL);
        if (email == null) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.missed_email));
        }

        String password = values.getAsString(UserEntry.COLUMN_USER_PASSWORD);
        if (password == null) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.missed_password), Toast.LENGTH_SHORT).show();
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(UserEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, getContext().getResources().getString(R.string.insert_user_failed) + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return updateUser(uri, contentValues, selection, selectionArgs);
            case USER_ID:
                selection = UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateUser(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.update_user_ex) + uri);
        }
    }

    private int updateUser(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(UserEntry.COLUMN_USER_NAME)) {
            String name = values.getAsString(UserEntry.COLUMN_USER_NAME);
            if (name == null) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.missed_name), Toast.LENGTH_SHORT).show();
            }
        }

        if (values.containsKey(UserEntry.COLUMN_USER_SURNAME)) {
            String surname = values.getAsString(UserEntry.COLUMN_USER_SURNAME);
            if (surname == null) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.missed_surname), Toast.LENGTH_SHORT).show();
            }
        }

        if (values.containsKey(UserEntry.COLUMN_USER_EMAIL)) {
            String email = values.getAsString(UserEntry.COLUMN_USER_EMAIL);
            if (email == null) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.missed_email), Toast.LENGTH_SHORT).show();
            }
        }

        if (values.containsKey(UserEntry.COLUMN_USER_PASSWORD)) {
            String password = values.getAsString(UserEntry.COLUMN_USER_PASSWORD);
            if (password == null) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.missed_password), Toast.LENGTH_SHORT).show();
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                rowsDeleted = database.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case USER_ID:
                selection = UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.delete_not_supported) + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return UserEntry.CONTENT_LIST_TYPE;
            case USER_ID:
                return UserEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(getContext().getResources().getString(R.string.get_type_ex_1) + uri
                        + getContext().getResources().getString(R.string.get_type_ex_2) + match);
        }
    }
}