package cs371m.denisely.pitchel_it;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.adobe.creativesdk.aviary.internal.headless.utils.MegaPixels;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.os.Environment.getExternalStorageState;

public class MainActivity extends AppCompatActivity {

    static final int PICK_IMAGE = 100;
    static final int EDIT_IMAGE_SUCCESS = 200;
    static final int TAKE_PHOTO = 300;
    static final int REQUEST_READWRITE_STORAGE = 1;

    File destination = new File(Environment.getExternalStorageDirectory(), "Pictures" + File.separator + "Pitchel It/");
    File newDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ask for permission to access pictures.
        // Important for when user opens app after installation and Pitchel It folder is already present
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READWRITE_STORAGE);

            // On new installation with Pitchel It folder already present,
            // Horizontal scroll view doesn't populate the first time, even if
            // given permission. Need to refresh it somehow?
        }


        Button takePhoto = (Button) findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting new file path
                if (!destination.isDirectory()){ // If the Pitchel-It folder isn't there yet, create it
                    System.out.println("making directory");
                    destination.mkdirs();
                }

                // Setting up destination aka new file name
                File[] listFiles = destination.listFiles();
                int num = 0;
                if (listFiles != null && listFiles.length != 0) {
                    String[] temp = listFiles[listFiles.length - 1].toString().split("/");
                    num = Integer.parseInt(temp[temp.length - 1].substring(temp[temp.length - 1].indexOf('-') + 1, temp[temp.length - 1].indexOf('.'))) + 1;
                }
                String newPhotoName = "photo-" + num + ".jpg";
                newDestination = new File(destination.toString(), newPhotoName);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newDestination));

                startActivityForResult(cameraIntent, TAKE_PHOTO);
            }
        });

        Button importPhoto = (Button) findViewById(R.id.importPhotoButton);
        importPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // http://viralpatel.net/blogs/pick-image-from-galary-android-app/
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });

        Button galleryButton = (Button) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

        //http://stackoverflow.com/questions/32109917/how-to-create-a-horizontal-list-of-pictures-with-title-in-android
        File[] listFiles = destination.listFiles();
        System.out.println("CREATING HORIZONTAL LIST VIEW");
        System.out.println(destination.isDirectory());

        if (listFiles != null && listFiles.length > 0) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.image_container);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int count = 6;
            if (listFiles.length < 6) {
                count = listFiles.length;
            }

            for (int i = 0; i < count; i++) {
                layoutParams.setMargins(15, 0, 15, 20);
                layoutParams.gravity = Gravity.CENTER;
                ImageView imageView = new ImageView(this);
                imageView.setAdjustViewBounds(true);
                Bitmap myBitmap = GalleryAdapter.scaleBitmapAndKeepRatio(BitmapFactory.decodeFile(listFiles[i].toString()));
                imageView.setImageBitmap(myBitmap);
    //            imageView.setOnClickListener(documentImageListener);
                imageView.setLayoutParams(layoutParams);

                layout.addView(imageView);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            // Just picked image from device gallery
            /* 1) Get uri of that image */
            Uri imageUri = data.getData();

            System.out.println("Does the folder exist? " + destination.isDirectory());
            System.out.println(destination);
             /* 2) Create a new Intent for imageEditor & set picked image*/
            if (!destination.isDirectory()){ // If the Pitchel-It folder isn't there yet, create it
                System.out.println("making directory");
                destination.mkdirs();
            }

            // Setting up destination aka new file name
            File[] listFiles = destination.listFiles();
            int num = 0;
            if (listFiles != null && listFiles.length != 0) {
                String[] temp = listFiles[listFiles.length - 1].toString().split("/");
                num = Integer.parseInt(temp[temp.length - 1].substring(temp[temp.length - 1].indexOf('-') + 1, temp[temp.length - 1].indexOf('.'))) + 1;
            }
            String newPhotoName = "photo-" + num + ".jpg";
            newDestination = new File(destination.toString(), newPhotoName);

            Intent imageEditorIntent = new AdobeImageIntent.Builder(MainActivity.this)
                    .setData(imageUri)
                    .withOutput(newDestination)
                    .withOutputFormat(Bitmap.CompressFormat.JPEG) // output format
                    .withOutputSize(MegaPixels.Mp5) // output size
                    .withOutputQuality(90) // output quality
                    .build();

             /* 3) Start the Image Editor with request code 1 */
            startActivityForResult(imageEditorIntent, EDIT_IMAGE_SUCCESS);
        } else if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK && data != null){
            File dest = new File(newDestination.toString());
            // Rescan gallery. If image is taken and back is pressed during editing (photo taken
            // wasn't edited), should still save that taken image to the gallery & device's gallery
            // http://stackoverflow.com/questions/4144840/how-can-i-refresh-the-gallery-after-i-inserted-an-image-in-android
            MediaScannerConnection.scanFile(this, // Refresh device's gallery
                    new String[] { dest.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("ExternalStorage", "Scanned " + path + ":");
                            Log.d("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            Uri imageBitmap = Uri.fromFile(newDestination);

            if (!destination.isDirectory()){ // If the Pitchel-It folder isn't there yet, create it
                System.out.println("making directory");
                destination.mkdirs();
            }

            // Setting up destination aka new file name
            File[] listFiles = destination.listFiles();
            int num = 0;
            if (listFiles != null && listFiles.length != 0) {
                String[] temp = listFiles[listFiles.length - 1].toString().split("/");
                num = Integer.parseInt(temp[temp.length - 1].substring(temp[temp.length - 1].indexOf('-') + 1, temp[temp.length - 1].indexOf('.'))) + 1;
            }

            Intent imageEditorIntent = new AdobeImageIntent.Builder(MainActivity.this)
                    .setData(imageBitmap)
                    .withOutput(dest)

                    .withOutputFormat(Bitmap.CompressFormat.JPEG) // output format
                    .withOutputSize(MegaPixels.Mp5) // output size
                    .withOutputQuality(90) // output quality
                    .build();

            startActivityForResult(imageEditorIntent, EDIT_IMAGE_SUCCESS);
        }
    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
