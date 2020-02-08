package com.example.unitally.tools;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class StageController implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener {

    private OnSwipeListener mListener;
    private GestureDetector mGestureDetector;
    private View mView;

    public static final int UP      = 1,
                            DOWN    = 2,
                            RIGHT   = 3,
                            LEFT    = 4,
                            CANCELED = -1;

    public StageController(Context context,
                           @NonNull View view,
                           @NonNull OnSwipeListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, this);
        mView = view;
        mView.setOnTouchListener(this);
    }

    private int mTestCounter = 0;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d("TouchDetected", "TouchDetected:" + ++mTestCounter);
        if(view.getId() == mView.getId()) {
            mGestureDetector.onTouchEvent(motionEvent);
            return true;
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float v, float v1) {
        float x1 = e1.getX();
        float y1 = e1.getY();

        float x2 = e2.getX();
        float y2 = e2.getY();

        Direction direction = getDirection(x1,y1,x2,y2);
        return onSwipe(direction);
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
//        float x1 = e1.getX();
//        float y1 = e1.getY();
//
//        float x2 = e2.getX();
//        float y2 = e2.getY();
//
//        Direction direction = getDirection(x1,y1,x2,y2);
//        return onSwipe(direction);
        return false;
    }

    private boolean onSwipe(Direction direction){
        if(direction == Direction.up) {
           mListener.onSwipe(UP);
           return true;
        }
        else if(direction == Direction.right) {
            mListener.onSwipe(RIGHT);
            return true;
        }
        else if(direction == Direction.down) {
            mListener.onSwipe(DOWN);
            return true;
        }
        else if(direction == Direction.left) {
            mListener.onSwipe(LEFT);
            return true;
        }
        else {
            mListener.onSwipe(CANCELED);
            return false;
        }
    }

    private Direction getDirection(float x1, float y1, float x2, float y2){
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.fromAngle(angle);
    }

    /**
     *
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    private double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1-y2,x2-x1) + Math.PI;
        return (rad*180/Math.PI + 180)%360;
    }


    private enum Direction{
        up,
        down,
        left,
        right;

        /**
         * Returns a direction given an angle.
         * Directions are defined as follows:
         *
         * Up: [45, 135]
         * Right: [0,45] and [315, 360]
         * Down: [225, 315]
         * Left: [135, 225]
         *
         * @param angle an angle from 0 to 360 - e
         * @return the direction of an angle
         */
        private static Direction fromAngle(double angle){
            if(inRange(angle, 45, 135)){
                return Direction.up;
            }
            else if(inRange(angle, 0,45) || inRange(angle, 315, 360)){
                return Direction.right;
            }
            else if(inRange(angle, 225, 315)){
                return Direction.down;
            }
            else{
                return Direction.left;
            }

        }

        /**
         * @param angle an angle
         * @param init the initial bound
         * @param end the final bound
         * @return returns true if the given angle is in the interval [init, end).
         */
        private static boolean inRange(double angle, float init, float end){
            return (angle >= init) && (angle < end);
        }
    }

    public interface OnSwipeListener {
        void onSwipe(int direction);
    }
}
