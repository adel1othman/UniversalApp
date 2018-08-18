package com.android.al3arrab.universalapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.al3arrab.universalapp.data.RegisterContract.UserEntry;

public class UserCursorAdapter extends CursorAdapter {

    private Context myContext;
    final String LOG_TAG = UserCursorAdapter.class.getSimpleName();

    public UserCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.nav_header_main, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        myContext = context;

        TextView fullnameTextView = view.findViewById(R.id.tvFullName);
        TextView emailTextView = view.findViewById(R.id.tvEmail);
        ImageView imgImageView = view.findViewById(R.id.imageView);

        int idColumnIndex = cursor.getColumnIndex(UserEntry._ID);
        int statusColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_STATUS);
        int nameColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_NAME);
        int surnameColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_SURNAME);
        int emailColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_EMAIL);
        int ImgColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_IMAGE);

        final int userID = cursor.getInt(idColumnIndex);
        final String userStatus = cursor.getString(statusColumnIndex);
        final String userName = cursor.getString(nameColumnIndex);
        final String userSurname = cursor.getString(surnameColumnIndex);
        final String userEmail = cursor.getString(emailColumnIndex);
        final String userImage = cursor.getString(ImgColumnIndex);

        if (userStatus.equals("active")){
            fullnameTextView.setText(String.format("%s! %s!", userName, userSurname));
            emailTextView.setText(userEmail);

            if (UserEntry.isImageResourceProvided(userImage)){
                Utility.setPic(context, imgImageView, Uri.parse(userImage));
            }else {
                imgImageView.setImageResource(R.mipmap.ic_launcher_round);
            }
        }
    }
}
