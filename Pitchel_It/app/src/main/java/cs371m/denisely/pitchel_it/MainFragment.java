package cs371m.denisely.pitchel_it;

import java.util.*;
import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.adobe.creativesdk.aviary.internal.headless.utils.MegaPixels;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Denise on 11/22/2016.
 */

public class MainFragment extends Fragment implements CarouselAdapter.CarouselClickListener{
    protected View myRootView;

    final int PICK_IMAGE = 100;
    final int EDIT_IMAGE_SUCCESS = 200;
    final int TAKE_PHOTO = 300;
    final int EDIT_AFTER_IMPORT_SUCCESS = 400;

    final int REQUEST_READWRITE_STORAGE = 1;

    File destination = new File(Environment.getExternalStorageDirectory(), "Pictures" + File.separator + "Pitchel It/");
    File newDestination;
    File[] listFiles;
    boolean isDirectory = false;

    private RecyclerView carousel;
    CarouselAdapter carouselAdapter;
    ArrayList<File> carouselFiles;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbname;

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
        }

        Button takePhoto = (Button) v.findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(v1 -> {
            // Getting new file path
            // If the Pitchel-It folder isn't there yet, create it
            if (!isDirectory){
                destination.mkdirs();
                isDirectory = true;
            }

            newDestination = getNewDestination();

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

        // Set up carousel recycler view
        // TODO: fix spacing on carousel.. kinda funky sometimes
        if(isDirectory){
            carouselFiles = getCarouselFiles();
            carousel = (RecyclerView) v.findViewById(R.id.carousel_rv);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false);
            carouselAdapter = new CarouselAdapter(carouselFiles);
            carouselAdapter.setCarouselClickListener(this);
            carousel.setLayoutManager(layoutManager);
            carousel.setAdapter(carouselAdapter);
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    public File getNewDestination(){
        Random rand = new Random();
        int val1 = rand.nextInt(9999);
        int val2 = rand.nextInt(9999);

        String newPhotoName = "photo-" + val1 + "_" + val2 + ".jpg";
        return new File(destination.toString(), newPhotoName);
    }

    public ArrayList<File> getCarouselFiles(){
        listFiles = destination.listFiles();
        File[] result = new File[]{};
        if (listFiles != null && listFiles.length > 0){
            if (listFiles.length < 6){
                result = Arrays.copyOfRange(listFiles, 0, listFiles.length);
            } else {
                result = Arrays.copyOfRange(listFiles, listFiles.length-6, listFiles.length);
            }
        }
        ArrayUtils.reverse(result);
        return new ArrayList<>(Arrays.asList(result));
    }

    public ArrayList<File> updateCarouselFiles(){
        listFiles = destination.listFiles();
        if (carouselFiles.size() == 6){
            carouselFiles.remove(carouselFiles.size()-1);
        }
        carouselFiles.add(0, listFiles[listFiles.length - 1]);
        return carouselFiles;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("request_code", "REQUEST CODE IS: " + requestCode);
        Log.d("result_code", "RESULT CODE IS: " + resultCode);
        Log.e("null_data", "Warning, null data from intent");
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            // Just picked image from device gallery
            // If the Pitchel-It folder isn't there yet, create it
            if (!isDirectory){
                destination.mkdirs();
                isDirectory = true;
            }

            newDestination = getNewDestination();
            startEditActivity(data.getData(), newDestination, true);

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

            // If the Pitchel-It folder isn't there yet, create it
            if (!isDirectory){
                destination.mkdirs();
                isDirectory = true;
            }

            startEditActivity(Uri.fromFile(newDestination), newDestination, false);

        } else if (requestCode == EDIT_IMAGE_SUCCESS && resultCode == RESULT_OK && data != null) {
            Log.d("edit image success", "Edit image success.");
            updateCarouselFiles();
            //Update the carousel with new image
            carousel.getAdapter().notifyDataSetChanged();

            // TODO: add to firebase
            String userName = user.getEmail().replaceAll("\\.", "@");

            dbname = FirebaseDatabase.getInstance().getReference(userName);

            PhotoObject photo = new PhotoObject("", new LatLng(-34, 151));

            String file_path = data.getData().getPath();
            String againFUCK = file_path.replace(".", "-");
            String convertFilePath = againFUCK.replace("/", "*");

            String key = dbname.child(convertFilePath).push().getKey();
            dbname.child(convertFilePath).setValue(photo);

            Intent intent = new Intent(getContext(), OneImage.class);
            intent.putExtra("thumbnail_path", data.getData().getPath());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
//        } else if (requestCode == EDIT_AFTER_IMPORT_SUCCESS){
//            Log.d("edit image success", "Import Edit image success.");
//            updateCarouselFiles();
//            //Update the carousel with new image
//            carousel.getAdapter().notifyDataSetChanged();
//
//            // TODO: Fix bug of the image not showing up after import photo
//            Intent intent = new Intent(getContext(), OneImage.class);
//            intent.putExtra("thumbnail_path", data.getData().getPath());
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getContext().startActivity(intent);
//        }
    }

    public void startEditActivity(Uri data, File newDest, Boolean import_photo){
        Intent imageEditorIntent = new AdobeImageIntent.Builder(myRootView.getContext())
                .setData(data)
                .withOutput(newDest)
                .withOutputFormat(Bitmap.CompressFormat.JPEG) // output format
                .withOutputSize(MegaPixels.Mp5) // output size
                .withOutputQuality(90) // output quality.. idk what this int means
                .build();

        if(import_photo)
            startActivityForResult(imageEditorIntent, EDIT_AFTER_IMPORT_SUCCESS);
        else
            startActivityForResult(imageEditorIntent, EDIT_IMAGE_SUCCESS);

    }

    @Override
    public void onCarouselItemClicked(File picture) {
        // Start new OneImage Activity
        newDestination = getNewDestination();
        startEditActivity(Uri.fromFile(picture), newDestination, false);

    }
}
