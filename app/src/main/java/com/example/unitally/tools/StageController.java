package com.example.unitally.tools;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

import com.example.unitally.R;

public class StageController implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener {

    private OnSwipeListener mListener;
    private GestureDetector mGestureDetector;
    private View mView;
    private Context mContext;

    public static final int UP      = 1,
                            DOWN    = 2,
                            RIGHT   = 3,
                            LEFT    = 4,
                            CANCELED = -1;

    private final float Y_EXECUTE_DISTANCE = 300;   // User travel in order to execute vertical swipe
    private final float X_EXECUTE_DISTANCE = 500;   // User travel in order to execute horizontal swipe
    private final float ACTION_BUFFER = 50;         // Radial distance before direction is determined

    private double mDistance;
    private Direction mDirection;

    public StageController(Context context,
                           @NonNull View view,
                           @NonNull OnSwipeListener listener) {

        mContext = context;
        mListener = listener;
        mGestureDetector = new GestureDetector(context, this);
        mView = view;
        mView.setOnTouchListener(this);

        mDistance = 0;
        mDirection = null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId() == mView.getId()) {
            mGestureDetector.onTouchEvent(motionEvent);

            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mView.animate().x(0);
                mView.animate().y(0);
                mDirection = null;
                return false;
            }
            else
                return true;
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float v, float v1) {
        float x1 = e1.getRawX();
        float y1 = e1.getRawY();

        float x2 = e2.getRawX();
        float y2 = e2.getRawY();

        trackLocation(x1, y1, x2, y2);

        if(mDirection != null) {
            return actionCompleted();
        }
        else {
            return false;
        }
    }

/**
 * Determines whether the User has successfully completed an action.
 *
 * @return True if and only if user has completed an action.
 */
    private boolean actionCompleted() {
        // User has swiped up
        if(mDirection == Direction.up && mDistance >= Y_EXECUTE_DISTANCE) {
            mListener.onSwipe(UP);
            return true;
        }
        // User has swiped right
        else if(mDirection == Direction.right && mDistance >= X_EXECUTE_DISTANCE) {
            mListener.onSwipe(RIGHT);
            return true;
        }
        // User has swiped down
        else if(mDirection == Direction.down && mDistance >= Y_EXECUTE_DISTANCE) {
            mListener.onSwipe(DOWN);
            return true;
        }
        // User has swiped left
        else if(mDirection == Direction.left && mDistance >= X_EXECUTE_DISTANCE) {
            mListener.onSwipe(LEFT);
            return true;
        }
        else {
            mListener.onSwipe(CANCELED);
            return false;
        }
    }

/**
 * Animates mView vertically. Follows user cursor only in the Y direction.
 */
    private void ScrollVertically() {
        float distance = (float)mDistance;
        mView.animate().x(0);
        if (mDirection == Direction.down) {
            mView.animate().y(distance).setDuration(0).start();
        } else if(mDirection == Direction.up) {
            mView.animate().y(-distance).setDuration(0).start();
        }
    }

/**
 * Animates mView horizontally. Follows user cursor only in the X direction.
 */
    private void ScrollHorizontally() {
        float distance = (float)mDistance;
        mView.animate().y(0);
        if (mDirection == Direction.right) {
            mView.animate().x(distance).setDuration(0).start();
        } else if(mDirection == Direction.left){
            mView.animate().x(-distance).setDuration(0).start();
        }
    }
/*------------------------------------------------------------------------------------------------*/
/*                                    Tracking Methods                                            */
/*------------------------------------------------------------------------------------------------*/

    /**
     * Phase one, tracks user movement by utilizing an interior buffer zone where the distance is
     * measured as a radial parameter(XY) until the user has committed to a direction(up, right,
     * down, left). At which point the method goes into phase two, where distance is only measured in
     * the X or Y direction.
     *
     * (***PASS RAW COORDINATE POINTS)
     * @param x1 initial x-coordinate cursor down position
     * @param y1 initial x-coordinate cursor down position
     * @param x2 current x-coordinate cursor position
     * @param y2 current x-coordinate cursor position
     */
    private void trackLocation(float x1, float y1, float x2, float y2) {
        //Distance from original press
        float dx;
        float dy;

        // (PHASE 1)
        // Awaiting Direction Parameters
        if(mDistance < ACTION_BUFFER) {
            dx = x2 - x1;
            dy = y2 - y1;
            mDistance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy,2));
            mDirection = null;
        }
        else if (mDirection == null) {
            mDirection = getDirection(x1, y1, x2, y2);
        }

        // (PHASE 2)
        // Determine how far user travels in the given direction
        else if (mDirection == Direction.left || mDirection == Direction.right) {
            dx = x2 - x1;
            mDistance = Math.sqrt((Math.pow(dx,2))) - ACTION_BUFFER;
            ScrollHorizontally();
        }
        else if (mDirection == Direction.up || mDirection == Direction.down) {
            dy = y2 - y1;
            mDistance = Math.sqrt((Math.pow(dy,2))) - ACTION_BUFFER;
            ScrollVertically();
        }
    }

    // TODO: Write a way to get View off screen once user has executed an action
    private boolean tossOut(float distance) {
        Animation animation;
        if (mDirection == Direction.left) {

        }
        else if (mDirection == Direction.right) {

        }
        else if(mDirection == Direction.down){

        }
        else if(mDirection == Direction.up) {

        }

        return mView.getAnimation().hasEnded();
    }


/*------------------------------------------------------------------------------------------------*/
/*                                    Direction Methods                                           */
/*------------------------------------------------------------------------------------------------*/
/**
 * Thanks to fernandohur on StackOverFlow.
 * https://stackoverflow.com/questions/13095494/how-to-detect-swipe-direction-between-left-right-and-up-down
 */
    private Direction getDirection(float x1, float y1, float x2, float y2){
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.fromAngle(angle);
    }

    /**
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

/*------------------------------------------------------------------------------------------------*/
/*                                Unused Interface Calls                                          */
/*------------------------------------------------------------------------------------------------*/
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
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
        return false;
    }
}
