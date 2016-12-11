package br.com.justdev.picnotes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by JustGian on 10/12/2016.
 */

/* Baseado no exemplo FingerPaint do Android */

public class DrawView extends View {
    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private BitmapDrawable pictureBitmap;
    private Rect mRect, mCanvasRect;

    public DrawView(Context c, Paint paint) {
        super(c);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = paint;
        pictureBitmap = null;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvasRect = mCanvas.getClipBounds();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (pictureBitmap == null)
            canvas.drawColor(0xFFAAAAAA);
        else
            canvas.drawBitmap(pictureBitmap.getBitmap(), mRect, mCanvasRect, mBitmapPaint);
            //pictureBitmap.draw(canvas);
            //canvas.drawBitmap(pictureBitmap, 0, 0, mPaint);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 1;
    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            //mPath.moveTo(mX, mY);
            mPath.lineTo(x, y);
            //mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
    public void setPictureBitmap(BitmapDrawable b){
        pictureBitmap = b;

        mRect = new Rect(0, 0, b.getBitmap().getWidth(), b.getBitmap().getHeight());

        Rect imageBounds = mCanvas.getClipBounds();
        pictureBitmap.setBounds(imageBounds);
    }
    public Bitmap getBitmap(){
        Bitmap bmOverlay = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(pictureBitmap.getBitmap(), mRect, mCanvasRect, mBitmapPaint);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        return bmOverlay;
        //return overlay(this.pictureBitmap.getBitmap(), this.mBitmap);
    }
}