package cs371m.denisely.pitchel_it;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;


import java.io.File;

import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.CameraSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.ui.activities.CameraPreviewActivity;
import ly.img.android.ui.activities.CameraPreviewBuilder;
import ly.img.android.ui.utilities.PermissionRequest;

public class MainActivity extends Activity implements PermissionRequest.Response {

    public static int CAMERA_PREVIEW_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SettingsList settingsList = new SettingsList();
        settingsList
                // Set custom camera export settings
                .getSettingsModel(CameraSettings.class)
                .setExportDir(Directory.DCIM, "Pitchel-It")
                .setExportPrefix("camera_")
                // Set custom editor export settings
                .getSettingsModel(EditorSaveSettings.class)
                .setExportDir(Directory.DCIM, "Pitchel-It")
                .setExportPrefix("result_")
                .setSavePolicy(
                        EditorSaveSettings.SavePolicy.RETURN_ALWAYS_ONLY_OUTPUT
                );

        Button editPhoto = (Button) findViewById(R.id.editButton);
        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CameraPreviewBuilder(MainActivity.this)
                        .setSettingsList(settingsList)
                        .startActivityForResult(MainActivity.this, CAMERA_PREVIEW_RESULT);
            }
        });

        Button importPhoto = (Button) findViewById(R.id.importPhotoButton);
        importPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {
            String resultPath =
                    data.getStringExtra(CameraPreviewActivity.RESULT_IMAGE_PATH);
            String sourcePath =
                    data.getStringExtra(CameraPreviewActivity.SOURCE_IMAGE_PATH);

            if (resultPath != null) {
                // Scan result file
                File file =  new File(resultPath);
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                scanIntent.setData(contentUri);
                sendBroadcast(scanIntent);
            }

            if (sourcePath != null) {
                // Scan camera file
                File file =  new File(sourcePath);
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                scanIntent.setData(contentUri);
                sendBroadcast(scanIntent);
            }

            Toast.makeText(this, "Image Save on: " + resultPath, Toast.LENGTH_LONG).show();
        }
    }

    //Important for Android 6.0 and above permisstion request, don't forget this!
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionGranted() {

    }

    @Override
    public void permissionDenied() {
        // The Permission was rejected by the user, so the Editor was not opened because it can not save the result image.
        // TODO for you: Show a Hint to the User
    }
}
