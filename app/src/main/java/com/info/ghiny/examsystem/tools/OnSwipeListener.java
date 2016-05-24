package com.info.ghiny.examsystem.tools;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by GhinY on 23/05/2016.
 */
public class OnSwipeListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public OnSwipeListener (Context context){
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int DISTANCE_THRESHOLD = 100;
        private static final int VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent startPoint, MotionEvent endPoint,
                               float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = endPoint.getY() - startPoint.getY();
                float diffX = endPoint.getX() - startPoint.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > DISTANCE_THRESHOLD
                            && Math.abs(velocityX) > VELOCITY_THRESHOLD) {
                        if (diffX > 0)
                            onSwipeRight();
                        else
                            onSwipeLeft();
                    }
                    result = true;
                }
                else if (Math.abs(diffY) > DISTANCE_THRESHOLD
                        && Math.abs(velocityY) > VELOCITY_THRESHOLD) {
                    if (diffY > 0)
                        onSwipeBottom();
                    else
                        onSwipeTop();
                }
                result = true;

            } catch (Exception exception){
                exception.printStackTrace();
            }

            return result;
        }
    }
}
