package com.android.al3arrab.universalapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.al3arrab.universalapp.data.RegisterContract.UserEntry;

public class RegisterActivity extends AppCompatActivity {

    public static final String PROFILE_IMAGE_KEY = "PROFILE_IMAGE_KEY";
    private static final int USER_LOADER = 0;
    UserCursorAdapter mCursorAdapter;

    private EditText mNameEditText, mSurnameEditText, mEmailEditText, mPassEditText;
    private ImageView myImg;
    private String imagePath = "";
    private Uri imgUri;
    public static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mNameEditText = findViewById(R.id.edtTxtName);
        mSurnameEditText = findViewById(R.id.edtTxtSurname);
        mEmailEditText = findViewById(R.id.edtTxtEmail);
        mPassEditText = findViewById(R.id.edtTxtPass);
        myImg = findViewById(R.id.myImg);
    }

    public void BtnRegisterClick(View v)
    {
        String nameString = mNameEditText.getText().toString().trim();
        String surnameString = mSurnameEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();
        String passwordString = mPassEditText.getText().toString().trim();

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(nameString)){
            Toast.makeText(this, getString(R.string.nameRquired), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(UserEntry.COLUMN_USER_NAME, nameString);

        if (TextUtils.isEmpty(surnameString)){
            Toast.makeText(this, getString(R.string.surnameRquired), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(UserEntry.COLUMN_USER_SURNAME, surnameString);

        if (TextUtils.isEmpty(emailString) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()){
            Toast.makeText(this, getString(R.string.emailRquired), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(UserEntry.COLUMN_USER_EMAIL, emailString);

        if (TextUtils.isEmpty(passwordString)){
            Toast.makeText(this, getString(R.string.passwordRquired), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(UserEntry.COLUMN_USER_PASSWORD, passwordString);

        values.put(UserEntry.COLUMN_USER_IMAGE, imagePath);
        values.put(UserEntry.COLUMN_USER_STATUS, "active");

        Uri newUri = this.getContentResolver().insert(UserEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, getString(R.string.user_register_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.user_register_done), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void ChoosePicBtn(View v)
    {
        int result = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            Utility.verifyStoragePermissions(RegisterActivity.this);
        } else {
            Intent imageIntent = new Intent();
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(imageIntent,
                    "Select Image"), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                imgUri = uri;
                imagePath = uri.toString();
                Log.d("myImg", "onActivityCreated URI: " + imagePath);
                Utility.setPic(getBaseContext(), myImg, uri);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(PROFILE_IMAGE_KEY, imgUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null){
            imgUri = savedInstanceState.getParcelable(PROFILE_IMAGE_KEY);

            if (imgUri != null){
                Utility.setPic(getBaseContext(), myImg, imgUri);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /*@Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                UserEntry._ID,
                UserEntry.COLUMN_USER_NAME,
                UserEntry.COLUMN_USER_SURNAME,
                UserEntry.COLUMN_USER_EMAIL,
                UserEntry.COLUMN_USER_IMAGE };

        return new CursorLoader(this,
                UserEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }*/

    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/
}
