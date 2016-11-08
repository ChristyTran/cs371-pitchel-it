package cs371m.denisely.pitchel_it;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Denise on 11/6/2016.
 */

public class GalleryActivity extends Activity {

//    ArrayList<String> pictures = new ArrayList<String>();
    ArrayList<Integer> pictures = new ArrayList<>();
    File[] listFile;

    RecyclerView recyclerView;

    public void onCreate(Bundle savedInstancestate){
        super.onCreate(savedInstancestate);
        setContentView(R.layout.gallery_page);

        Intent intent = getIntent();

//        fetchImages();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        GalleryAdapter galleryAdapter = new GalleryAdapter(pictures);
        recyclerView.setAdapter(galleryAdapter);
        fetchDummyImages();
//        fetchImages();
    }

//    public void fetchImages(){
//        File file = new File(android.os.Environment.getExternalStorageDirectory(), "/DCIM/Pitchel-It");
//        if (file.isDirectory()){
//            listFile = file.listFiles();
//
//            for (int i = 0; i < listFile.length; i++){
//                pictures.add(listFile[i].getAbsolutePath());
//            }
//        }
//        System.out.println(pictures.size());
//    }

    public void fetchDummyImages(){
        pictures.add(R.drawable.logo);
        pictures.add(R.drawable.background);
    }
}
