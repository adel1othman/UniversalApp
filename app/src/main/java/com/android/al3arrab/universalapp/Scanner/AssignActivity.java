package com.android.al3arrab.universalapp.Scanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.al3arrab.universalapp.ListUsersCursorAdapter;
import com.android.al3arrab.universalapp.MainActivity;
import com.android.al3arrab.universalapp.R;
import com.android.al3arrab.universalapp.data.RegisterContract;
import com.android.al3arrab.universalapp.data.RegisterDbHelper;

import static com.android.al3arrab.universalapp.MainActivity.currentUserID;

/**
 * Created by Adel on 12/28/2017.
 */

public class AssignActivity extends AppCompatActivity {

    String result = "";
    private RegisterDbHelper mDbHelper;
    private ListUsersCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_users);

        final ListView usersListView = findViewById(R.id.UsersList);
        result  = getIntent().getStringExtra("ScanResult");

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        mDbHelper = new RegisterDbHelper(getBaseContext());
                        SQLiteDatabase database = mDbHelper.getReadableDatabase();
                        String users = "SELECT * FROM users";
                        Cursor cursor = database.rawQuery(users, null);
                        mCursorAdapter = new ListUsersCursorAdapter(getBaseContext(), cursor);
                        usersListView.setAdapter(mCursorAdapter);

                        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                                final Dialog dialog1 = new Dialog(usersListView.getContext());
                                dialog1.setContentView(R.layout.password_dialog);
                                dialog1.setTitle("Enter Your Password");
                                final EditText pass = dialog1.findViewById(R.id.enterPassword);
                                Button passOK = dialog1.findViewById(R.id.btnPasswordEntered);
                                passOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SQLiteDatabase database = mDbHelper.getReadableDatabase();
                                        String qwery = "SELECT password FROM users WHERE _ID = " + id;
                                        Cursor mcursor = database.rawQuery(qwery, null);
                                        int idColumnIndex = mcursor.getColumnIndex(RegisterContract.UserEntry.COLUMN_USER_PASSWORD);
                                        String password = "";
                                        if (mcursor.moveToFirst()) {
                                            do {
                                                password = mcursor.getString(idColumnIndex);
                                            } while (mcursor.moveToNext());
                                        }

                                        if (pass.getText().toString().equals(password)){
                                            mDbHelper = new RegisterDbHelper(getBaseContext());
                                            database = mDbHelper.getWritableDatabase();
                                            String strSQL = "UPDATE users SET status = '0' WHERE _ID = "+ currentUserID;
                                            database.execSQL(strSQL);
                                            String strSQL1 = "UPDATE users SET status = 'active', code = '" + result + "' WHERE _ID = "+ id;
                                            database.execSQL(strSQL1);

                                            finish();
                                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                            startActivity(intent);
                                        }else {
                                            dialog1.dismiss();
                                        }
                                    }
                                });
                                dialog1.show();
                            }
                        });
                    }
                };
        showAssignCodeToUserDialog("Scanned code " + result + " is not assigned to any user, do you want to assign it now?", discardButtonClickListener);
    }

    private void showAssignCodeToUserDialog(String message,
                                            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, discardButtonClickListener);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
