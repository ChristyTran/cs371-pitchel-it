package cs371m.denisely.pitchel_it;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Denise on 11/6/2016.
 */

public class GalleryActivity extends Activity {

    File[] listFile;

    RecyclerView recyclerView;

    public void onCreate(Bundle savedInstancestate){
        super.onCreate(savedInstancestate);
        setContentView(R.layout.gallery_page);
        
        fetchImages();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        GalleryAdapter galleryAdapter = new GalleryAdapter(listFile);
        recyclerView.setAdapter(galleryAdapter);
    }

    public void fetchImages(){
        File dir = new File(Environment.getExternalStorageDirectory(), "Pictures" + File.separator + "Pitchel It");
        if (dir.isDirectory()){
            listFile = dir.listFiles();
        }
    }

}
