package com.android.al3arrab.universalapp.Dama;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.al3arrab.universalapp.MainActivity;
import com.android.al3arrab.universalapp.R;

public class DamaActivity extends AppCompatActivity {

    public static Shape[] shapes;
    public static Shape[] shapesBlue;
    public static Shape[] shapesGreen;
    public static ImageView[] images;
    public static boolean IsAllInside;
    public static boolean[] slotPos = new boolean[9];
    public static String whichTurn;
    public static String[] win;
    public static boolean gameFinished;
    RelativeLayout mainLayout;
    public static Slots[] slots;
    Point[] points;
    int mainX, mainY, mainWidth, mainHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dama);

        mainLayout = findViewById(R.id.activity_main);

        win = new String[6];
        win[0] = "111";
        win[1] = "222";
        win[2] = "333";
        win[3] = "aaa";
        win[4] = "bbb";
        win[5] = "ccc";

        slots = new Slots[9];
        /*slots[0] = new Slots("a", 1, "available", 103, 389);
        slots[1] = new Slots("b", 1, "available", 494, 389);
        slots[2] = new Slots("c", 1, "available", 883, 389);
        slots[3] = new Slots("a", 2, "available", 70, 655);
        slots[4] = new Slots("b", 2, "available", 494, 655);
        slots[5] = new Slots("c", 2, "available", 914, 655);
        slots[6] = new Slots("a", 3, "available", 45, 966);
        slots[7] = new Slots("b", 3, "available", 494, 966);
        slots[8] = new Slots("c", 3, "available", 946, 966);*/

        images = new ImageView[6];
        points = new Point[6];
        shapes = new Shape[6];

        final View main = findViewById(R.id.damaMain);
        ViewTreeObserver vto = mainLayout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onPreDraw() {

                mainLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                mainX = (int)mainLayout.getX();
                mainY = (int)mainLayout.getY();
                mainWidth = mainLayout.getWidth();
                mainHeight = mainLayout.getHeight();

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, 90);
                ImageView imageView = new ImageView(getBaseContext());
                imageView.setLayoutParams(layoutParams);

                double slot1X = main.getWidth()/9.07182;
                double slot1Y = main.getHeight()/3.93008;
                double slot2X = main.getWidth()/2.0 - layoutParams.width/2;
                double slot2Y = main.getHeight()/3.93008;
                double slot3X = main.getWidth()/1.223;
                double slot3Y = main.getHeight()/4.07198;
                double slot4X = main.getWidth()/15.42857;
                double slot4Y = main.getHeight()/2.41832;
                double slot5X = main.getWidth()/2.0 - layoutParams.width/2;
                double slot5Y = main.getHeight()/2.41832;
                double slot6X = main.getWidth()/1.18162;
                double slot6Y = main.getHeight()/2.41832;
                double slot7X = main.getWidth()/24;
                double slot7Y = main.getHeight()/1.63975;
                double slot8X = main.getWidth()/2.0 - layoutParams.width/2;
                double slot8Y = main.getHeight()/1.63975;
                double slot9X = main.getWidth()/1.14165;
                double slot9Y = main.getHeight()/1.63975;

                Log.d("TAG", "onPreDraw: " + slot1Y);
                Log.d("TAG", "onPreDraw: " + slot1Y);

                slots[0] = new Slots("a", 1, "available", (float)slot1X, (float)slot1Y);
                slots[1] = new Slots("b", 1, "available", (float)slot2X, (float)slot2Y);
                slots[2] = new Slots("c", 1, "available", (float)slot3X, (float)slot3Y);
                slots[3] = new Slots("a", 2, "available", (float)slot4X, (float)slot4Y);
                slots[4] = new Slots("b", 2, "available", (float)slot5X, (float)slot5Y);
                slots[5] = new Slots("c", 2, "available", (float)slot6X, (float)slot6Y);
                slots[6] = new Slots("a", 3, "available", (float)slot7X, (float)slot7Y);
                slots[7] = new Slots("b", 3, "available", (float)slot8X, (float)slot8Y);
                slots[8] = new Slots("c", 3, "available", (float)slot9X, (float)slot9Y);

                points[0] = new Point(mainX, mainY);
                points[1] = new Point(mainWidth/2 - 50, mainY);
                points[2] = new Point(mainWidth - 100, mainY);
                points[3] = new Point(mainX, mainHeight - 90);
                points[4] = new Point(mainWidth/2 - 50, mainHeight - 90);
                points[5] = new Point(mainWidth - 100, mainHeight - 90);

                images[0] = new ImageView(getBaseContext());
                images[1] = new ImageView(getBaseContext());
                images[2] = new ImageView(getBaseContext());
                images[3] = new ImageView(getBaseContext());
                images[4] = new ImageView(getBaseContext());
                images[5] = new ImageView(getBaseContext());

                images[0].setImageResource(R.drawable.blue);
                images[1].setImageResource(R.drawable.blue);
                images[2].setImageResource(R.drawable.blue);
                images[3].setImageResource(R.drawable.green);
                images[4].setImageResource(R.drawable.green);
                images[5].setImageResource(R.drawable.green);

                images[0].setLayoutParams(layoutParams);
                images[1].setLayoutParams(layoutParams);
                images[2].setLayoutParams(layoutParams);
                images[3].setLayoutParams(layoutParams);
                images[4].setLayoutParams(layoutParams);
                images[5].setLayoutParams(layoutParams);

                images[0].setX(points[0].x);
                images[0].setY(points[0].y);
                images[1].setX(points[1].x);
                images[1].setY(points[1].y);
                images[2].setX(points[2].x);
                images[2].setY(points[2].y);
                images[3].setX(points[3].x);
                images[3].setY(points[3].y);
                images[4].setX(points[4].x);
                images[4].setY(points[4].y);
                images[5].setX(points[5].x);
                images[5].setY(points[5].y);

                shapes[0] = new Shape("blue", images[0], images[0].getX(), images[0].getY(), "null", 0);
                shapes[1] = new Shape("blue", images[1], images[1].getX(), images[1].getY(), "null", 0);
                shapes[2] = new Shape("blue", images[2], images[2].getX(), images[2].getY(), "null", 0);
                shapes[3] = new Shape("green", images[3], images[3].getX(), images[3].getY(), "null", 0);
                shapes[4] = new Shape("green", images[4], images[4].getX(), images[4].getY(), "null", 0);
                shapes[5] = new Shape("green", images[5], images[5].getX(), images[5].getY(), "null", 0);

                shapesBlue = new Shape[3];
                shapesGreen = new Shape[3];

                shapesBlue[0] = shapes[0];
                shapesBlue[1] = shapes[1];
                shapesBlue[2] = shapes[2];
                shapesGreen[0] = shapes[3];
                shapesGreen[1] = shapes[4];
                shapesGreen[2] = shapes[5];

                for (int i=0; i<6; i++){
                    mainLayout.addView(shapes[i].getImageView());
                    shapes[i].getImageView().setOnTouchListener(new MovementLogic(shapes[i].getImageView()));
                }

                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    };
            showEndGameDialog(discardButtonClickListener);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                };
        showEndGameDialog(discardButtonClickListener);
    }

    private void showEndGameDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_exit);
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

    public void NewGame(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
