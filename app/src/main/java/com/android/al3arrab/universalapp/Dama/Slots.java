package com.android.al3arrab.universalapp.Dama;

public class Slots {
    private String column;
    private int row;
    private String availability;
    private float slotX, slotY;

    public Slots(String Column, int Row, String Availability, float SlotX, float SlotY){
        column = Column;
        row = Row;
        availability = Availability;
        slotX = SlotX;
        slotY = SlotY;
    }

    public String getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public float getSlotX() {
        return slotX;
    }

    public float getSlotY() {
        return slotY;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
