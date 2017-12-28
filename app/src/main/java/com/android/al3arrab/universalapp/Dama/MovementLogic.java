package com.android.al3arrab.universalapp.Dama;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.al3arrab.universalapp.MainActivity;
import com.android.al3arrab.universalapp.R;

import static com.android.al3arrab.universalapp.Dama.DamaActivity.IsAllInside;
import static com.android.al3arrab.universalapp.Dama.DamaActivity.gameFinished;
import static com.android.al3arrab.universalapp.Dama.DamaActivity.shapes;
import static com.android.al3arrab.universalapp.Dama.DamaActivity.slotPos;
import static com.android.al3arrab.universalapp.Dama.DamaActivity.slots;
import static com.android.al3arrab.universalapp.Dama.DamaActivity.whichTurn;
import static com.android.al3arrab.universalapp.Dama.DamaActivity.shapesBlue;
import static com.android.al3arrab.universalapp.Dama.DamaActivity.shapesGreen;
import static com.android.al3arrab.universalapp.Dama.DamaActivity.win;

/**
 * Created by Adel on 2/22/2017.
 */

public class MovementLogic implements View.OnTouchListener {
    /**
     * Callback used to indicate when the drag is finished
     */
    private interface OnDragActionListener {
        /**
         * Called when drag event is started
         *
         * @param view The view dragged
         */
        void onDragStart(View view);

        /**
         * Called when drag event is completed
         *
         * @param view The view dragged
         */
        void onDragEnd(View view);
    }

    private View mView;
    private View mParent;
    private boolean isDragging;
    private boolean isInitialized = false;

    private int width;
    private float xWhenAttached;
    private float maxLeft;
    private float maxRight;
    private float dX;
    private float previousX;

    private int height;
    private float yWhenAttached;
    private float maxTop;
    private float maxBottom;
    private float dY;
    private float previousY;

    Shape shapeInQuestion;
    Slots slotInQuestion;

    private OnDragActionListener mOnDragActionListener;

    public MovementLogic(View view) {
        this(view, (View) view.getParent(), null);
    }

    public MovementLogic(View view, View parent) {
        this(view, parent, null);
    }

    public MovementLogic(View view, OnDragActionListener onDragActionListener) {
        this(view, (View) view.getParent(), onDragActionListener);
    }

    public MovementLogic(View view, View parent, OnDragActionListener onDragActionListener) {
        initListener(view, parent);
        setOnDragActionListener(onDragActionListener);
    }

    private void setOnDragActionListener(OnDragActionListener onDragActionListener) {
        mOnDragActionListener = onDragActionListener;
    }

    private void initListener(View view, View parent) {
        mView = view;
        mParent = parent;
        isDragging = false;
        isInitialized = false;
    }

    private void updateBounds() {
        updateViewBounds();
        updateParentBounds();
        isInitialized = true;
    }

    private void updateViewBounds() {
        width = mView.getWidth();
        xWhenAttached = mView.getX();
        dX = 0;

        height = mView.getHeight();
        yWhenAttached = mView.getY();
        dY = 0;
    }

    private void updateParentBounds() {
        maxLeft = 0;
        maxRight = maxLeft + mParent.getWidth();

        maxTop = 0;
        maxBottom = maxTop + mParent.getHeight();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isDragging) {
            float[] bounds = new float[4];
            // LEFT
            bounds[0] = event.getRawX() + dX;
            if (bounds[0] < maxLeft) {
                bounds[0] = maxLeft;
            }
            // RIGHT
            bounds[2] = bounds[0] + width;
            if (bounds[2] > maxRight) {
                bounds[2] = maxRight;
                bounds[0] = bounds[2] - width;
            }
            // TOP
            bounds[1] = event.getRawY() + dY;
            if (bounds[1] < maxTop) {
                bounds[1] = maxTop;
            }
            // BOTTOM
            bounds[3] = bounds[1] + height;
            if (bounds[3] > maxBottom) {
                bounds[3] = maxBottom;
                bounds[1] = bounds[3] - height;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:

                    shapeInQuestion.setSlotX(mView.getX());
                    shapeInQuestion.setSlotY(mView.getY());

                    CheckingSlot();

                    if (slotInQuestion == null){
                        shapeInQuestion.getImageView().animate().x(previousX).setDuration(0).start();
                        shapeInQuestion.getImageView().animate().y(previousY).setDuration(0).start();

                        shapeInQuestion.setSlotX(previousX);
                        shapeInQuestion.setSlotY(previousY);
                    }else {
                        if (IsSlotEmpty()){
                            shapeInQuestion.getImageView().animate().x(slotInQuestion.getSlotX()).setDuration(0).start();
                            shapeInQuestion.getImageView().animate().y(slotInQuestion.getSlotY()).setDuration(0).start();

                            shapeInQuestion.setSlotX(slotInQuestion.getSlotX());
                            shapeInQuestion.setSlotY(slotInQuestion.getSlotY());

                            shapeInQuestion.setColumn(slotInQuestion.getColumn());
                            shapeInQuestion.setRow(slotInQuestion.getRow());

                            if (!IsAllInside) {
                                TurnChanger();
                                int allInsideChecking = 0;
                                for (Shape shape : shapes) {
                                    for (int i = 0; i < 9; i++) {
                                        if (shape.getSlotX() == slots[i].getSlotX() && shape.getSlotY() == slots[i].getSlotY()) {
                                            shape.getImageView().setEnabled(false);
                                            allInsideChecking++;
                                            break;
                                        }
                                    }
                                    if (allInsideChecking == 6) {
                                        IsAllInside = true;
                                        TurnChanger();
                                        break;
                                    }
                                }
                            }else {
                                TurnChanger();
                            }

                            WinCheck();
                        }else {
                            shapeInQuestion.getImageView().animate().x(previousX).setDuration(0).start();
                            shapeInQuestion.getImageView().animate().y(previousY).setDuration(0).start();

                            shapeInQuestion.setSlotX(previousX);
                            shapeInQuestion.setSlotY(previousY);
                        }
                        slotInQuestion = null;
                    }

                    onDragFinish();
                    updateParentBounds();

                    break;
                case MotionEvent.ACTION_MOVE:
                    mView.animate().x(bounds[0]).setDuration(0).start();
                    mView.animate().y(bounds[1]).setDuration(0).start();
                    break;
            }
            return true;
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDragging = true;
                    if (!isInitialized) {
                        updateBounds();
                    }
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    previousX = mView.getX();
                    previousY = mView.getY();

                    for (Shape shape:shapes) {
                        if (mView == shape.getImageView()){
                            shapeInQuestion = shape;
                            break;
                        }
                    }

                    if (mOnDragActionListener != null) {
                        mOnDragActionListener.onDragStart(mView);
                    }
                    v.bringToFront();
                    return true;
            }
        }
        return false;
    }

    private void onDragFinish() {
        if (mOnDragActionListener != null) {
            mOnDragActionListener.onDragEnd(mView);
        }

        dX = 0;
        dY = 0;
        isDragging = false;
    }

    public void CheckingSlot(){
        if (!IsAllInside){
            for (Slots slot : slots){
                slot.setAvailability("available");
                if (shapeInQuestion.getSlotX() > slot.getSlotX() - 100 && shapeInQuestion.getSlotX() < slot.getSlotX() + 100
                        && shapeInQuestion.getSlotY() > slot.getSlotY() - 100 && shapeInQuestion.getSlotY() < slot.getSlotY() + 100){
                    slotInQuestion = slot;
                    if (slotInQuestion.getSlotX() == previousX && slotInQuestion.getSlotY() == previousY){
                        slotInQuestion.setAvailability("busy");
                    }
                }

                for (int i=0; i<6; i++){
                    if (slot.getSlotX() == shapes[i].getSlotX() && slot.getSlotY() == shapes[i].getSlotY()){
                        slot.setAvailability("busy");
                        break;
                    }
                }
            }
        }else {
            for (Slots slot : slots) {
                slot.setAvailability("busy");
                if (shapeInQuestion.getSlotX() > slot.getSlotX() - 100 && shapeInQuestion.getSlotX() < slot.getSlotX() + 100
                        && shapeInQuestion.getSlotY() > slot.getSlotY() - 100 && shapeInQuestion.getSlotY() < slot.getSlotY() + 100){
                    slotInQuestion = slot;
                }
            }

            if (shapeInQuestion.getRow() == 1 && shapeInQuestion.getColumn().equals("a")){
                slots[1].setAvailability("available");
                slots[3].setAvailability("available");
            }else if (shapeInQuestion.getRow() == 1 && shapeInQuestion.getColumn().equals("b")){
                slots[0].setAvailability("available");
                slots[2].setAvailability("available");
                slots[4].setAvailability("available");
            }else if (shapeInQuestion.getRow() == 1 && shapeInQuestion.getColumn().equals("c")){
                slots[1].setAvailability("available");
                slots[5].setAvailability("available");
            }else if (shapeInQuestion.getRow() == 2 && shapeInQuestion.getColumn().equals("a")){
                slots[0].setAvailability("available");
                slots[4].setAvailability("available");
                slots[6].setAvailability("available");
            }else if (shapeInQuestion.getRow() == 2 && shapeInQuestion.getColumn().equals("b")){
                slots[1].setAvailability("available");
                slots[3].setAvailability("available");
                slots[5].setAvailability("available");
                slots[7].setAvailability("available");
            }else if (shapeInQuestion.getRow() == 2 && shapeInQuestion.getColumn().equals("c")){
                slots[2].setAvailability("available");
                slots[4].setAvailability("available");
                slots[8].setAvailability("available");
            }else if (shapeInQuestion.getRow() == 3 && shapeInQuestion.getColumn().equals("a")){
                slots[3].setAvailability("available");
                slots[7].setAvailability("available");
            }else if (shapeInQuestion.getRow() == 3 && shapeInQuestion.getColumn().equals("b")){
                slots[4].setAvailability("available");
                slots[6].setAvailability("available");
                slots[8].setAvailability("available");
            }else if (shapeInQuestion.getRow() == 3 && shapeInQuestion.getColumn().equals("c")){
                slots[5].setAvailability("available");
                slots[7].setAvailability("available");
            }

            for (Slots slot : slots){
                for (int i=0; i<6; i++){
                    if (slot.getSlotX() == shapes[i].getSlotX() && slot.getSlotY() == shapes[i].getSlotY()){
                        slot.setAvailability("busy");
                        break;
                    }
                }
            }
        }
    }

    public void TurnChanger(){
        if (shapeInQuestion.getColor().equals("blue")){
            whichTurn = "green";
            shapes[0].getImageView().setEnabled(false);
            shapes[1].getImageView().setEnabled(false);
            shapes[2].getImageView().setEnabled(false);
            shapes[3].getImageView().setEnabled(true);
            shapes[4].getImageView().setEnabled(true);
            shapes[5].getImageView().setEnabled(true);
        }else {
            whichTurn = "blue";
            shapes[3].getImageView().setEnabled(false);
            shapes[4].getImageView().setEnabled(false);
            shapes[5].getImageView().setEnabled(false);
            shapes[0].getImageView().setEnabled(true);
            shapes[1].getImageView().setEnabled(true);
            shapes[2].getImageView().setEnabled(true);
        }
    }

    private boolean IsSlotEmpty(){
        if (slotInQuestion.getAvailability().equals("available")){
            return true;
        }
        return false;
    }

    public void WinCheck(){
        String blueColumn = "", greenColumn = "", blueRow = "", greenRow = "";

        for (Shape shape : shapesBlue){
            blueColumn += shape.getColumn();
            blueRow += shape.getRow();
        }
        for (Shape shape : shapesGreen){
            greenColumn += shape.getColumn();
            greenRow += shape.getRow();
        }

        for (String item:win) {
            if (item.equals(blueColumn) || item.equals(blueRow)){
                Toast.makeText(mParent.getContext(),"Blue WIN",Toast.LENGTH_LONG).show();
                gameFinished = true;
                IsAllInside = false;
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(mParent.getContext(), MainActivity.class);
                                mParent.getContext().startActivity(intent);
                            }
                        };
                showEndGameDialog(discardButtonClickListener);
                //break;
            }
            if (item.equals(greenColumn) || item.equals(greenRow)){
                Toast.makeText(mParent.getContext(),"Green WIN",Toast.LENGTH_LONG).show();
                gameFinished = true;
                IsAllInside = false;
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(mParent.getContext(), MainActivity.class);
                                mParent.getContext().startActivity(intent);
                            }
                        };
                showEndGameDialog(discardButtonClickListener);
                //break;
            }
        }
    }

    private void showEndGameDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mParent.getContext());
        builder.setMessage(R.string.play_again);
        builder.setPositiveButton(R.string.no, discardButtonClickListener);
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                    Intent intent = new Intent(mParent.getContext(), DamaActivity.class);
                    mParent.getContext().startActivity(intent);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void slotAvailable(){
        slotPos[0] = false;
        slotPos[1] = false;
        slotPos[2] = false;
        slotPos[3] = false;
        slotPos[4] = false;
        slotPos[5] = false;
        slotPos[6] = false;
        slotPos[7] = false;
        slotPos[8] = false;

        for (int i=0; i<9; i++){
            if (previousX == slots[i].getSlotX() && previousY == slots[i].getSlotY()){
                if (i == 0){
                    slotPos[1] = true;
                    slotPos[3] = true;
                }else if (i == 1){
                    slotPos[0] = true;
                    slotPos[2] = true;
                    slotPos[4] = true;
                }else if (i == 2){
                    slotPos[1] = true;
                    slotPos[5] = true;
                }else if (i == 3){
                    slotPos[0] = true;
                    slotPos[4] = true;
                    slotPos[6] = true;
                }else if (i == 4){
                    slotPos[1] = true;
                    slotPos[3] = true;
                    slotPos[5] = true;
                    slotPos[7] = true;
                }else if (i == 5){
                    slotPos[2] = true;
                    slotPos[4] = true;
                    slotPos[8] = true;
                }else if (i == 6){
                    slotPos[3] = true;
                    slotPos[7] = true;
                }else if (i == 7){
                    slotPos[4] = true;
                    slotPos[6] = true;
                    slotPos[8] = true;
                }else if (i == 8){
                    slotPos[5] = true;
                    slotPos[7] = true;
                }

                break;
            }
        }

        for (int i=0; i<9; i++){
            if (mView.getX() > slots[i].getSlotX() - 100 && mView.getX() < slots[i].getSlotX() + 100 &&
                    mView.getY() > slots[i].getSlotY() - 100 && mView.getY() < slots[i].getSlotY() + 100){
                if (!slotPos[i]){
                    mView.animate().x(previousX).setDuration(0).start();
                    mView.animate().y(previousY).setDuration(0).start();
                }
                break;
            }
        }
    }
}