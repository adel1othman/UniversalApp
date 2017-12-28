package com.android.al3arrab.universalapp.Dama;

import android.graphics.Point;
import android.widget.ImageView;

/**
 * Created by Adel on 6/17/2017.
 */

public class Shape{

    private String color;
    private ImageView imageView;
    private float slotX, slotY;
    private String column;
    private int row;

    public Shape(String Color, ImageView ImageViewID, float SlotX, float SlotY, String Column, int Row) {
        color = Color;
        imageView = ImageViewID;
        slotX = SlotX;
        slotY = SlotY;
        column = Column;
        row = Row;
    }

    String getColor() {
        return color;
    }

    ImageView getImageView(){
        return imageView;
    }

    public float getSlotX() {
        return slotX;
    }

    public float getSlotY() {
        return slotY;
    }

    public void setSlotX(float slotX) {
        this.slotX = slotX;
    }

    public void setSlotY(float slotY) {
        this.slotY = slotY;
    }

    public int getRow() {
        return row;
    }

    public String getColumn() {
        return column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
