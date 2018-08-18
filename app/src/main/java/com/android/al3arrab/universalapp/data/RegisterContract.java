package com.android.al3arrab.universalapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public final class RegisterContract {

    private RegisterContract() {}

    public static final String CONTENT_AUTHORITY = "com.android.al3arrab.universalapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USERS = "users";

    public static final class UserEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        public final static String TABLE_NAME = "users";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_USER_STATUS = "status";
        public final static String COLUMN_USER_CODE = "code";
        public final static String COLUMN_USER_NAME ="name";
        public final static String COLUMN_USER_SURNAME = "surname";
        public final static String COLUMN_USER_EMAIL = "email";
        public final static String COLUMN_USER_PASSWORD = "password";
        public final static String COLUMN_USER_IMAGE = "image";

        public static boolean isValidInfo(String info) {
            if (!TextUtils.isEmpty(info)) {
                return true;
            }
            return false;
        }

        public static Uri currentUserUri(int id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static boolean isImageResourceProvided(String imgResource) {
            if (!TextUtils.isEmpty(imgResource)) {
                return true;
            }
            return false;
        }
    }
}