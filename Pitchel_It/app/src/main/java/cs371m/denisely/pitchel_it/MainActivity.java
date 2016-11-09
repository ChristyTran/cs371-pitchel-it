package cs371m.denisely.pitchel_it;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.adobe.creativesdk.aviary.AdobeImageIntent;

import java.io.File;

import static android.os.Environment.getExternalStorageState;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("TEST******************************************************************");
        System.out.println(Environment.getExternalStorageDirectory());

        Button editPhoto = (Button) findViewById(R.id.editButton);
        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button importPhoto = (Button) findViewById(R.id.importPhotoButton);
        importPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        /* 1) Make a new Uri object (Replace this with a real image on your device) */
//        Uri imageUri = Uri.parse("content://media/external/images/media/####");
                String filename = "IMG_20161102_134135.jpg";
                String path =  Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + filename;
                File f = new File(path);
                Uri imageUri = Uri.fromFile(f);

    /* 2) Create a new Intent */
                Intent imageEditorIntent = new AdobeImageIntent.Builder(MainActivity.this)
                        .setData(imageUri)
                        .build();

    /* 3) Start the Image Editor with request code 1 */
                startActivityForResult(imageEditorIntent, 1);
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
