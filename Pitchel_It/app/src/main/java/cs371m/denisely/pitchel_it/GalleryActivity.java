package cs371m.denisely.pitchel_it;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Denise on 11/6/2016.
 */

public class GalleryActivity extends FragmentActivity implements OnMapReadyCallback {
    public interface searchByTagListener {
        void searchByTagCallback(File[] files);
    }
    // TODO: change submit button to cute icon

    File[] listFile;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbname;

    private GoogleMap mMap;

    public void onCreate(Bundle savedInstancestate){
        super.onCreate(savedInstancestate);
        setContentView(R.layout.gallery_page);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fetchImages();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        GalleryAdapter galleryAdapter = new GalleryAdapter(listFile, getApplicationContext());
        recyclerView.setAdapter(galleryAdapter);

        EditText searchbar = (EditText)findViewById(R.id.search_bar);
        Button button = (Button)findViewById(R.id.search_button);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null){
            searchbar.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }

//        // Remove periods from user name and get reference in database
        String userName = user.getEmail().replaceAll("\\.", "@");
        dbname = FirebaseDatabase.getInstance().getReference(userName);

        searchByTagListener listener;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag_to_search = searchbar.getText().toString();
                searchByTag(tag_to_search);

                // TODO: do a query for tags
                // Hide keyboard after submitting
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });


    }

    public void fetchImages(){
        File dir = new File(Environment.getExternalStorageDirectory(), "Pictures" + File.separator + "Pitchel It");
        if (dir.isDirectory()){
            listFile = dir.listFiles();
            Log.d("files", "List file length in fetchImages(): " + listFile.length);
        }
    }

    public void searchByTag(final String search) {
        if (dbname == null) {
            Log.d("TAG", "userDB is null!");
            return;
        }

        Query query = dbname
                .orderByChild("tag").equalTo(search);
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        List<PhotoObject> l = new ArrayList<PhotoObject>();
                        List<File> filesArrayList = new ArrayList<File>();
                        for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                            //Getting the data from snapshot
//                            PhotoObject photo = new PhotoObject((String) photoSnapshot.child("tag").getValue(),
//                                    new LatLng(-34, 151));

                            System.out.println("GalleryActivity "+photoSnapshot.getKey());
//                                    (File) photoSnapshot.child("filepath").getValue());

                            String againFUCK = photoSnapshot.getKey().replace("@", ".");
                            String convertFilePath = againFUCK.replace("*", "/");

                            System.out.println("GalleryActivity " + convertFilePath);

                            filesArrayList.add(new File(convertFilePath));
                        }
                        File[] files = filesArrayList.toArray(new File[filesArrayList.size()]);

                        GalleryAdapter galleryAdapter = new GalleryAdapter(files, getApplicationContext());
                        recyclerView.setAdapter(galleryAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
//                        Log.d(TAG, "Name query cancelled");
                    }
                });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // TODO: Pin all locations to map. Do a query of all locations.

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
