package com.android.al3arrab.universalapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.al3arrab.universalapp.data.RegisterContract.UserEntry;

public class EditActivity extends AppCompatActivity {

    public static final String PROFILE_IMAGE_KEY = "PROFILE_IMAGE_KEY";

    private Uri mCurrentUserUri;
    Cursor cursor;
    ContentResolver contentResolver;
    private EditText mNameEditText, mSurnameEditText, mEmailEditText, mPassEditText;
    private ImageView myImg;
    private Uri imgUri;

    String userName, userSurname, userEmail, userPassword, userImage;
    private String imagePath = "";
    public static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = this.getIntent();
        mCurrentUserUri = intent.getData();

        String[] projection = {
                UserEntry._ID,
                UserEntry.COLUMN_USER_STATUS,
                UserEntry.COLUMN_USER_NAME,
                UserEntry.COLUMN_USER_SURNAME,
                UserEntry.COLUMN_USER_EMAIL,
                UserEntry.COLUMN_USER_PASSWORD,
                UserEntry.COLUMN_USER_IMAGE};

        contentResolver = this.getContentResolver();

        cursor = contentResolver.query(
                mCurrentUserUri,
                projection,
                null,
                null,
                null);

        mNameEditText = findViewById(R.id.edtTxtName);
        mSurnameEditText = findViewById(R.id.edtTxtSurname);
        mEmailEditText = findViewById(R.id.edtTxtEmail);
        mPassEditText = findViewById(R.id.edtTxtPass);
        myImg = findViewById(R.id.myImg);

        int idColumnIndex = cursor.getColumnIndex(UserEntry._ID);
        int statusColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_STATUS);
        int nameColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_NAME);
        int surnameColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_SURNAME);
        int emailColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_EMAIL);
        int passwordColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_PASSWORD);
        int ImgColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_IMAGE);

        while (cursor.moveToNext()){
            final String userStatus = cursor.getString(statusColumnIndex);
            userName = cursor.getString(nameColumnIndex);
            userSurname = cursor.getString(surnameColumnIndex);
            userEmail = cursor.getString(emailColumnIndex);
            userPassword = cursor.getString(passwordColumnIndex);
            userImage = cursor.getString(ImgColumnIndex);

            mNameEditText.setText(userName);
            mSurnameEditText.setText(userSurname);
            mEmailEditText.setText(String.valueOf(userEmail));
            mPassEditText.setText(userPassword);

            if (UserEntry.isImageResourceProvided(userImage)){
                Utility.setPic(this, myImg, Uri.parse(userImage));
            }else {
                myImg.setImageResource(R.mipmap.ic_launcher_round);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void ChoosePicBtn(View v)
    {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            Utility.verifyStoragePermissions(this);
        } else {
            Intent imageIntent = new Intent();
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(imageIntent,
                    "Select Image"), PICK_IMAGE_REQUEST);
        }
    }

    public void saveChanges(){
        String newNameString = mNameEditText.getText().toString().trim();
        String newSurnameString = mSurnameEditText.getText().toString().trim();
        String newEmailString = mEmailEditText.getText().toString().trim();
        String newPasswordString = mPassEditText.getText().toString().trim();

        ContentValues values = new ContentValues();

        if ((TextUtils.isEmpty(newNameString) && TextUtils.isEmpty(newSurnameString) &&
                TextUtils.isEmpty(newEmailString) && TextUtils.isEmpty(newPasswordString)) || (userName.equals(newNameString) &&
                userSurname.equals(newSurnameString) && userEmail.equals(newEmailString) && userPassword.equals(newPasswordString) &&
                userImage.equals(imagePath))) {
            return;
        }

        if (TextUtils.isEmpty(newNameString)){
            Toast.makeText(this, getString(R.string.nameRquired), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(UserEntry.COLUMN_USER_NAME, newNameString);

        if (TextUtils.isEmpty(newSurnameString)){
            Toast.makeText(this, getString(R.string.surnameRquired), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(UserEntry.COLUMN_USER_SURNAME, newSurnameString);

        if (TextUtils.isEmpty(newEmailString) || !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmailString).matches()){
            Toast.makeText(this, getString(R.string.emailRquired), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(UserEntry.COLUMN_USER_EMAIL, newEmailString);

        if (TextUtils.isEmpty(newPasswordString)){
            Toast.makeText(this, getString(R.string.passwordRquired), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(UserEntry.COLUMN_USER_PASSWORD, newPasswordString);

        if (!TextUtils.isEmpty(imagePath)){
            values.put(UserEntry.COLUMN_USER_IMAGE, imagePath);
        }

        values.put(UserEntry.COLUMN_USER_STATUS, "active");

        int rowsUpdated = contentResolver.update(mCurrentUserUri, values, null, null);

        if (rowsUpdated == 0) {
            Toast.makeText(this, getString(R.string.user_register_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.edit_user_successful), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_save:
                saveChanges();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.action_cancel:
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                break;
        }

        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                imgUri = uri;
                imagePath = uri.toString();
                Log.d("imagePath", "onActivityCreated URI: " + imagePath);
                Utility.setPic(this, myImg, uri);
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
}