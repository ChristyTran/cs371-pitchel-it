package cs371m.denisely.pitchel_it;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.adobe.creativesdk.aviary.internal.headless.utils.MegaPixels;

import java.io.File;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Denise on 11/22/2016.
 */

public class MainFragment extends Fragment {
    // TODO: Make this faster.. so slow
    protected View myRootView;

    static final int PICK_IMAGE = 100;
    static final int EDIT_IMAGE_SUCCESS = 200;
    static final int TAKE_PHOTO = 300;
    static final int REQUEST_READWRITE_STORAGE = 1;

    File destination = new File(Environment.getExternalStorageDirectory(), "Pictures" + File.separator + "Pitchel It/");
    File newDestination;
    boolean isDirectory = false;
    File[] listFiles;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.main_fragment, container, false);
        myRootView = v;
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = myRootView;

        if (destination.isDirectory()){
            isDirectory = true;
        }

        int permissionCheck1 = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READWRITE_STORAGE);

            // On new installation with Pitchel It folder already present,
            // Horizontal scroll view doesn't populate the first time, even if
            // given permission. Need to refresh it somehow?
        }


        Button takePhoto = (Button) v.findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(v1 -> {
            // Getting new file path
            if (!isDirectory){ // If the Pitchel-It folder isn't there yet, create it
                destination.mkdirs();
                isDirectory = true;
            }

            // Setting up destination aka new file name
//            listFiles = destination.listFiles();
//            int num = 0;
//            if (listFiles != null && listFiles.length != 0) {
//                String[] temp = listFiles[listFiles.length - 1].toString().split("/");
//                num = Integer.parseInt(temp[temp.length - 1].substring(temp[temp.length - 1].indexOf('-') + 1, temp[temp.length - 1].indexOf('.'))) + 1;
//            }

            Random rand = new Random();
            int val1 = rand.nextInt(9999);
            int val2 = rand.nextInt(9999);

            String newPhotoName = "photo-" + val1 + "_" + val2 + ".jpg";
            newDestination = new File(destination.toString(), newPhotoName);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newDestination));

            startActivityForResult(cameraIntent, TAKE_PHOTO);
        });

        Button importPhoto = (Button) v.findViewById(R.id.importPhotoButton);
        importPhoto.setOnClickListener(v12 -> {
            // http://viralpatel.net/blogs/pick-image-from-galary-android-app/
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        });

        Button galleryButton = (Button) v.findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(v13 -> {
            Intent intent = new Intent(v13.getContext(), GalleryActivity.class);
            startActivity(intent);
        });

        //http://stackoverflow.com/questions/32109917/how-to-create-a-horizontal-list-of-pictures-with-title-in-androidW
        listFiles = destination.listFiles();

        if (listFiles != null && listFiles.length > 0) {
            LinearLayout layout = (LinearLayout) v.findViewById(R.id.image_container);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int count = 6;
            if (listFiles.length < 6) {
                count = listFiles.length;
            }

            for (int i = 0; i < count; i++) {
                layoutParams.setMargins(15, 0, 15, 20);
                layoutParams.gravity = Gravity.CENTER;
                ImageView imageView = new ImageView(v.getContext());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("request_code", "REQUEST CODE IS: " + requestCode);
        Log.d("result_code", "RESULT CODE IS: " + resultCode);
        Log.e("null_data", "Warning, null data from intent");
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            // Just picked image from device gallery
            /* 1) Get uri of that image */
            Uri imageUri = data.getData();

             /* 2) Create a new Intent for imageEditor & set picked image*/
            if (!isDirectory){ // If the Pitchel-It folder isn't there yet, create it
                destination.mkdirs();
                isDirectory = true;
            }

            // Setting up destination aka new file name
//            listFiles = destination.listFiles();
//            int num = 0;
//            if (listFiles != null && listFiles.length != 0) {
//                String[] temp = listFiles[listFiles.length - 1].toString().split("/");
//                num = Integer.parseInt(temp[temp.length - 1].substring(temp[temp.length - 1].indexOf('-') + 1, temp[temp.length - 1].indexOf('.'))) + 1;
//            }

            Random rand = new Random();
            int val1 = rand.nextInt(9999);
            int val2 = rand.nextInt(9999);

            String newPhotoName = "photo-" + val1 + "_" + val2 + ".jpg";
            newDestination = new File(destination.toString(), newPhotoName);

            Intent imageEditorIntent = new AdobeImageIntent.Builder(myRootView.getContext())
                    .setData(imageUri)
                    .withOutput(newDestination)
                    .withOutputFormat(Bitmap.CompressFormat.JPEG) // output format
                    .withOutputSize(MegaPixels.Mp5) // output size
                    .withOutputQuality(90) // output quality
                    .build();

             /* 3) Start the Image Editor with request code 1 */
            startActivityForResult(imageEditorIntent, EDIT_IMAGE_SUCCESS);
        } else if (requestCode == TAKE_PHOTO ){ // && resultCode == RESULT_OK && data != null
            Log.d("take photo result", "newDestination in TAKE_PHOTO result is: " + newDestination.toString());
            File dest = new File(newDestination.toString());
            // Rescan gallery. If image is taken and back is pressed during editing (photo taken
            // wasn't edited), should still save that taken image to the gallery & device's gallery
            // http://stackoverflow.com/questions/4144840/how-can-i-refresh-the-gallery-after-i-inserted-an-image-in-android
            MediaScannerConnection.scanFile(myRootView.getContext(), // Refresh device's gallery
                    new String[] { dest.toString() }, null,
                    (path, uri) -> {
                        Log.d("ExternalStorage", "Scanned " + path + ":");
                        Log.d("ExternalStorage", "-> uri=" + uri);
                    });

            Uri imageBitmap = Uri.fromFile(newDestination);

            if (!isDirectory){ // If the Pitchel-It folder isn't there yet, create it
                destination.mkdirs();
                isDirectory = true;
            }

            Intent imageEditorIntent = new AdobeImageIntent.Builder(myRootView.getContext())
                    .setData(imageBitmap)
                    .withOutput(dest)

                    .withOutputFormat(Bitmap.CompressFormat.JPEG) // output format
                    .withOutputSize(MegaPixels.Mp5) // output size
                    .withOutputQuality(90) // output quality
                    .build();

            startActivityForResult(imageEditorIntent, EDIT_IMAGE_SUCCESS);
        }
    }
}
