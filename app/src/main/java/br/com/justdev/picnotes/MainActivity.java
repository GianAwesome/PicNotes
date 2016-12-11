package br.com.justdev.picnotes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int TAKE_PICTURE_REQUEST = 1;

    private String curPicturePath;
    private Uri curPictureUri;

    private DrawView mDrawView;

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private DrawerLayout mDrawerLayout;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        PicNotes.setContext(this);

        Paint mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);

        ViewGroup layout = (ViewGroup) findViewById((R.id.mainLayout));
        mDrawView = new DrawView(this, mPaint);
        mDrawView.setLayoutParams(new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(mDrawView);


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

        mNavItems.add(new NavItem("Compartilhar", "", 0));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }

            /*
            * Called when a particular item from the navigation drawer
            * is selected.
            * */
            private void selectItemFromDrawer(int position) {
                mDrawerList.setItemChecked(position, true);
                setTitle(mNavItems.get(position).mTitle);

                // Close the drawer
                mDrawerLayout.closeDrawer(mDrawerPane);

                Log.d(getResources().getString(R.string.app_name), "drawer item " + position);

                switch(position){
                    case 0:
                        File path = saveCanvasToFile();
                        // Verifica se arquivo foi criado antes de compartilhar
                        if (path != null) {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(PicNotes.context, "br.com.justdev.picnotes.fileprovider", path));
                            shareIntent.setType("image/jpeg");
                            startActivity(shareIntent);
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

            // Virar imagem
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap bitSource = BitmapFactory.decodeFile(curPicturePath);
            Bitmap bitmap = Bitmap.createBitmap(bitSource, 0, 0, bitSource.getWidth(), bitSource.getHeight(), matrix, true);

            mDrawView.setPictureBitmap(new BitmapDrawable(getResources(), bitmap));
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
                imageFileName,
                ".jpg",
                storageDir
        );

        Log.d(getResources().getString(R.string.app_name), "temp file created at " + image.getAbsolutePath());

        // Save a file: path for use with ACTION_VIEW intents
        curPicturePath = image.getAbsolutePath();
        return image;
    }

    protected File saveCanvasToFile() {

        try {
            File f = createImageFile();
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            mDrawView.getBitmap().compress(Bitmap.CompressFormat.PNG, 90, out);
            return f;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // Usado para definir cada item do menu
    // Baseado em http://codetheory.in/android-navigation-drawer/
    class NavItem {
        String mTitle;
        String mSubtitle;
        int mIcon;

        public NavItem(String title, String subtitle, int icon) {
            mTitle = title;
            mSubtitle = subtitle;
            mIcon = icon;
        }
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText( mNavItems.get(position).mTitle );
            subtitleView.setText( mNavItems.get(position).mSubtitle );
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }
}