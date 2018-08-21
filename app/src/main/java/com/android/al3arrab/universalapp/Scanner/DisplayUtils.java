package com.android.al3arrab.universalapp.Scanner;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtils {
    public static Point getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (wm != null) {
            display = wm.getDefaultDisplay();
        }
        Point screenResolution = new Point();
        if (display != null) {
            display.getSize(screenResolution);
        }

        return screenResolution;
    }

    public static int getScreenOrientation(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (wm != null) {
            display = wm.getDefaultDisplay();
        }

        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if (display != null) {
            if(display.getWidth()==display.getHeight()){
                orientation = Configuration.ORIENTATION_SQUARE;
            } else{
                if(display.getWidth() < display.getHeight()){
                    orientation = Configuration.ORIENTATION_PORTRAIT;
                }else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                }
            }
        }
        return orientation;
    }

}
