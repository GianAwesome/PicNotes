package br.com.justdev.picnotes;

import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by JustGian on 11/12/2016.
 */
public class DrawViewTest {
    DrawView view;
    Paint paint;

    @Before
    public void create() throws Exception {
        paint = new Paint();
        view = new DrawView(null, paint);
    }

    @Test
    public void touch_start() throws Exception {
        view.touch_start(1, 1);
        assertEquals(view.mX, 1, 0.5);
        assertEquals(view.mY, 1, 0.5);
    }

    @Test
    public void touch_move() throws Exception {
        view.touch_start(5, 6);
        assertEquals(view.mX, 5, 0.5);
        assertEquals(view.mY, 6, 0.5);
    }
}