package br.com.justdev.picnotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static br.com.justdev.picnotes.FileUriUtil.getOutputMediaFileUri;

public class MainActivity extends AppCompatActivity {

    private static final int TAKE_PICTURE_REQUEST = 1;

    private String curPicturePath;
    private Uri curPictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        PicNotes.setContext(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Log.d(getResources().getString(R.string.app_name), "camera start");

                //fileUri = getOutputMediaFileUri(FileUriUtil.MEDIA_TYPE_IMAGE); // create file to save the image
                //camera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set image file name

                if (camera.resolveActivity(getPackageManager()) != null) {// returns an activity component that can handle the intent

                    File pictureFile = null;
                    try {
                        pictureFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(getResources().getString(R.string.app_name), "could not save picture");
                        return;
                    }

                    if (pictureFile != null) {
                        curPictureUri = FileProvider.getUriForFile(PicNotes.context, "br.com.justdev.picnotes.fileprovider", pictureFile);
                        camera.putExtra(MediaStore.EXTRA_OUTPUT, curPictureUri);
                        startActivityForResult(camera, TAKE_PICTURE_REQUEST);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(getResources().getString(R.string.app_name), "verify picture status");

        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK){
            // handle picture taken by camera

            Log.d(getResources().getString(R.string.app_name), "picture ok");

            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get(curPicturePath);

            // show image
            ImageView imgView = (ImageView) findViewById(R.id.pictureView);

            // Virar imagem
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap bitSource = BitmapFactory.decodeFile(curPicturePath);
            Bitmap bitmap = Bitmap.createBitmap(bitSource, 0, 0, bitSource.getWidth(), bitSource.getHeight(), matrix, true);

            imgView.setImageBitmap(bitmap);

            //imgView.setImageURI(curPictureUri);
            //imgView.setRotation(90);

            // Altera rotação dependendo da orientação da imagem, mas talvez não seja necessário
            try {
                ExifInterface exif = new ExifInterface(curPicturePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d(getResources().getString(R.string.app_name), "picture orientation = " + orientation);
                switch(orientation){
                    case 3:
                        break;
                    case 6:
                        break;
                    case 8:
                        break;
                }
            } catch (IOException ex) {
                Log.e(getResources().getString(R.string.app_name), "could not load EXIF interface");
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PN_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        Log.d(getResources().getString(R.string.app_name), "creating temp file");

        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // sufix
                storageDir      // dir
        );

        Log.d(getResources().getString(R.string.app_name), "temp file created at " + image.getAbsolutePath());

        // Save a file: path for use with ACTION_VIEW intents
        curPicturePath = image.getAbsolutePath();
        return image;
    }
}