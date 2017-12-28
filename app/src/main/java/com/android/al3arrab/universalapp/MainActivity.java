package com.android.al3arrab.universalapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.al3arrab.universalapp.Dama.DamaActivity;
import com.android.al3arrab.universalapp.MusicPlayer.PlayerActivity;
import com.android.al3arrab.universalapp.Scanner.FullScannerActivity;
import com.android.al3arrab.universalapp.Youtube.YouTubeActivity;
import com.android.al3arrab.universalapp.data.RegisterContract.UserEntry;
import com.android.al3arrab.universalapp.data.RegisterDbHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RegisterDbHelper mDbHelper;
    public static int currentUserID = -1;
    private ListUsersCursorAdapter mCursorAdapter;
    private Uri mCurrentUserUri;
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Class<?> mClss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView fullnameTextView = navigationView.getHeaderView(0).findViewById(R.id.tvFullName);
        TextView emailTextView = navigationView.getHeaderView(0).findViewById(R.id.tvEmail);
        ImageView imgImageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);

        mDbHelper = new RegisterDbHelper(this);
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String count = "SELECT count(*) FROM users";
        Cursor mcursor = database.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        mcursor.close();
        if(icount>0){
            String qwery = "SELECT * FROM users WHERE status LIKE 'active'";
            Cursor cursor = database.rawQuery(qwery, null);

            int idColumnIndex = cursor.getColumnIndex(UserEntry._ID);
            int statusColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_STATUS);
            int nameColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_NAME);
            int surnameColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_SURNAME);
            int emailColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_EMAIL);
            int passwordColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_PASSWORD);
            int ImgColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_IMAGE);

            if (cursor.moveToFirst()) {
                do {
                    final int userID = cursor.getInt(idColumnIndex);
                    final String userStatus = cursor.getString(statusColumnIndex);
                    String userName = cursor.getString(nameColumnIndex);
                    final String userSurname = cursor.getString(surnameColumnIndex);
                    final String userEmail = cursor.getString(emailColumnIndex);
                    final String userPassword = cursor.getString(passwordColumnIndex);
                    final String userImage = cursor.getString(ImgColumnIndex);

                    fullnameTextView.setText(String.format("%s %s", userName, userSurname));
                    emailTextView.setText(userEmail);
                    currentUserID = userID;

                    if (UserEntry.isImageResourceProvided(userImage)){
                        Utility.setPic(this, imgImageView, Uri.parse(userImage));
                    }else {
                        imgImageView.setImageResource(R.mipmap.ic_launcher_round);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();

            mCurrentUserUri = ContentUris.withAppendedId(UserEntry.CONTENT_URI, currentUserID);


        }else {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_AddNewUser:
                Intent intent = new Intent(MainActivity.this, NewUserActivity.class);
                intent.putExtra("CUID", currentUserID);
                startActivity(intent);
                break;
            case R.id.action_ChangeUser:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.list_users);
                dialog.setTitle("Change User");

                ListView usersListView = dialog.findViewById(R.id.UsersList);

                mDbHelper = new RegisterDbHelper(getBaseContext());
                SQLiteDatabase database = mDbHelper.getReadableDatabase();
                String users = "SELECT * FROM users";
                Cursor mcursor = database.rawQuery(users, null);
                mCursorAdapter = new ListUsersCursorAdapter(getBaseContext(), mcursor);
                usersListView.setAdapter(mCursorAdapter);

                usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                        final Dialog dialog1 = new Dialog(dialog.getContext());
                        dialog1.setContentView(R.layout.password_dialog);
                        dialog1.setTitle("Enter Your Password");
                        final EditText pass = dialog1.findViewById(R.id.enterPassword);
                        Button passOK = dialog1.findViewById(R.id.btnPasswordEntered);
                        passOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SQLiteDatabase database = mDbHelper.getReadableDatabase();
                                String qwery = "SELECT password FROM users WHERE _ID = " + id;
                                Cursor cursor = database.rawQuery(qwery, null);
                                int idColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_PASSWORD);
                                String password = "";
                                if (cursor.moveToFirst()) {
                                    do {
                                        password = cursor.getString(idColumnIndex);
                                    } while (cursor.moveToNext());
                                }

                                if (pass.getText().toString().equals(password)){
                                    mDbHelper = new RegisterDbHelper(getBaseContext());
                                    database = mDbHelper.getWritableDatabase();
                                    String strSQL = "UPDATE users SET status = '0' WHERE _ID = "+ currentUserID;
                                    database.execSQL(strSQL);
                                    String strSQL1 = "UPDATE users SET status = 'active' WHERE _ID = "+ id;
                                    database.execSQL(strSQL1);

                                    finish();
                                    startActivity(getIntent());
                                }else {
                                    dialog1.dismiss();
                                }
                            }
                        });
                        dialog1.show();
                    }
                });

                dialog.show();
                break;
            case R.id.action_DeleteUser:
                showDeleteConfirmationDialog();
                break;
            case R.id.action_EditUser:
                Intent intent3 = new Intent(MainActivity.this, EditActivity.class);
                Uri currentUserUri = ContentUris.withAppendedId(UserEntry.CONTENT_URI, currentUserID);
                intent3.setData(currentUserUri);
                startActivity(intent3);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_scanner:
                launchActivity(FullScannerActivity.class);
                /*Intent intentS = new Intent(this, FullScannerActivity.class);
                startActivity(intentS);*/
                break;
            case R.id.nav_music_player:
                Intent intentMP = new Intent(this, PlayerActivity.class);
                startActivity(intentMP);
                break;
            case R.id.nav_youtube:
                Intent intentYT = new Intent(this, YouTubeActivity.class);
                startActivity(intentYT);
                break;
            case R.id.nav_simple_dama:
                Intent intentD = new Intent(this, DamaActivity.class);
                startActivity(intentD);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    private void deleteUser() {
        if (mCurrentUserUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentUserUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_user_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_user_successful),
                        Toast.LENGTH_SHORT).show();

                mDbHelper = new RegisterDbHelper(this);
                SQLiteDatabase database = mDbHelper.getReadableDatabase();
                String count = "SELECT count(*) FROM users";
                Cursor mcursor = database.rawQuery(count, null);
                mcursor.moveToFirst();
                int icount = mcursor.getInt(0);
                mcursor.close();
                if(icount>0){
                    database = mDbHelper.getReadableDatabase();
                    String strSQL1 = "SELECT _ID FROM users ORDER BY _ID ASC LIMIT 1";
                    Cursor cursor = database.rawQuery(strSQL1, null);
                    int idColumnIndex = cursor.getColumnIndex(UserEntry._ID);
                    int newUserID = -1;
                    if (cursor.moveToFirst()) {
                        do {
                            newUserID = cursor.getInt(idColumnIndex);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    database = mDbHelper.getWritableDatabase();
                    String strSQL2 = "UPDATE users SET status = 'active' WHERE _ID = "+ newUserID;
                    database.execSQL(strSQL2);
                    finish();
                    startActivity(getIntent());
                }else {
                    Intent intent = new Intent(this, RegisterActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteUser();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
