package br.com.justdev.picnotes;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by icg1 on 03/01/2016.
 */
public class FileUriUtil {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir;

        // Check if external media is mounted

        if(! Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            PicNotes.logE("external storage not mounted");
            return null;
        }

        // Check if external media can be written
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            PicNotes.logE("external storage cannot be written");
            return null;
        }

        PicNotes.logD("external storage state: " + Environment.getExternalStorageState());

        mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                //Environment.getExternalStorageDirectory(),
                PicNotes.findResource().getString(R.string.app_name)
        );

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        /*for (File f : PicNotes.context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES)){
            PicNotes.logD(f.getPath());
        }*/

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                PicNotes.logE("failed to create directory " + mediaStorageDir.getAbsolutePath());
                return null;
            }
        }

        File mediaFile = null;

        try {
            if (type == MEDIA_TYPE_IMAGE){
                mediaFile = newImageFile(mediaStorageDir);
            }else{
                // in case we use videos someday
            }
        }catch(IOException ex){
            PicNotes.logE("failed to save file");
        }

        return mediaFile;
    }

    // creates a new stance of file in the parameter folder
    private static File newImageFile(File mediaStorageDir) throws IOException {
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = File.createTempFile("IMG_" + timeStamp, ".jpg",
                                            mediaStorageDir);

        return mediaFile;
    }

    // creates a new stance of file in the default public storage directory
    private static File newImageFile() throws IOException {
        return newImageFile(new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                PicNotes.findResource().getString(R.string.app_name)));
    }
}
