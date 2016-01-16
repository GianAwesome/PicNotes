package br.com.justdev.picnotes;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Created by icg1 on 03/01/2016.
 */
public class PicNotes extends Application {
    protected static Context context;

    public static void setContext(Context _context){
        context = _context.getApplicationContext();
    }

    public static void logD(String msg){
        Log.d(context.getResources().getString(R.string.app_name), msg);
    }

    public static void logE(String msg){
        Log.e(context.getResources().getString(R.string.app_name), msg);
    }

    public static Resources findResource(){
        return context.getResources();
    }
}