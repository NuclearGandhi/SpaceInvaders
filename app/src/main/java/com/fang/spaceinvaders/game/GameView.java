package com.fang.spaceinvaders.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fang.spaceinvaders.game.util.Board;

public class GameView extends SurfaceView implements Runnable {

    private static int sPixelWidth;
    private static int sPixelHeight;
    private static final int FPS = 60;

    volatile boolean playing;
    private Thread gameThread = null;

    private Paint mPaint;
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;

    private SpaceInvaders mGame;

    @SuppressWarnings("SuspiciousNameCombination")
    public GameView(Context context) {
        super(context);
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        sPixelWidth = displayMetrics.widthPixels;
        sPixelHeight = displayMetrics.heightPixels;
        if (sPixelWidth < sPixelHeight) {
            sPixelWidth = displayMetrics.heightPixels;
            sPixelHeight = displayMetrics.widthPixels;
        }
        prepareGame(context);
    }

    private void prepareGame(Context context) {
        mGame = new SpaceInvaders(new Board(sPixelWidth, sPixelHeight), context);
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        mGame.nextFrame();
    }

    private void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.BLACK);
            mGame.drawObjects(mCanvas, mPaint);
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void control() {
        try {
            Thread.sleep(1000 / FPS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        //when the game is paused
        //setting the variable to false
        playing = false;
        try {
            //stopping the thread
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        //when the game is resumed
        //starting the thread again
        playing = true;
        gameThread = new Thread(this);
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
    }

    private int mPrimaryPointerId;
    private int mPendingPointerId;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionIndex() >= 2) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                mGame.release();
                break;
            case MotionEvent.ACTION_DOWN:
                mPrimaryPointerId = event.getPointerId(event.getActionIndex());
                focusOnPointer(event, mPrimaryPointerId);
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                int pointerId = event.getPointerId(event.getActionIndex());
                if (getTouchLocation(event, pointerId) == SpaceInvaders.TOUCH_TOP)
                    mGame.touch(SpaceInvaders.TOUCH_TOP);
                mPendingPointerId = pointerId;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                int pointerId = event.getPointerId(event.getActionIndex());
                if (pointerId == mPrimaryPointerId && event.getPointerCount() > 1) {
                    if (getTouchLocation(event, mPendingPointerId) == SpaceInvaders.TOUCH_TOP) mGame.release();
                    else focusOnPointer(event, mPendingPointerId);
                    mPrimaryPointerId = mPendingPointerId;
                }
                mPendingPointerId = mPrimaryPointerId;
                performClick();
                break;
            }
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void focusOnPointer(MotionEvent event, int pointerId) {
        mGame.touch(getTouchLocation(event, pointerId));
    }

    private @SpaceInvaders.TouchLocation
    int getTouchLocation(MotionEvent event, int pointerId) {
        int actionIndex = event.findPointerIndex(pointerId);
        if (actionIndex >= event.getPointerCount()) return SpaceInvaders.TOUCH_INVALID;
        float x = event.getX(actionIndex);
        float y = event.getY(actionIndex);

        if (y < sPixelHeight / 2) return SpaceInvaders.TOUCH_TOP;
        if (x < sPixelWidth / 2) return SpaceInvaders.TOUCH_LEFT;
        else return SpaceInvaders.TOUCH_RIGHT;
    }
}