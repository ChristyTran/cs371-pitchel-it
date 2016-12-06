package cs371m.denisely.pitchel_it;

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
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Denise on 11/6/2016.
 */

public class GalleryActivity extends FragmentActivity implements OnMapReadyCallback,
    GalleryAdapter.TriggerMapUpdate {
    // TODO: change submit button to cute icon

    File[] listFile;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbname;
    GalleryAdapter.TriggerMapUpdate triggerMapUpdate;

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
        galleryAdapter.setUpdateMapListener(this);
        recyclerView.setAdapter(galleryAdapter);

        EditText searchbar = (EditText)findViewById(R.id.search_bar);
//        Button button = (Button)findViewById(R.id.search_gallery_button);
        ImageButton button = (ImageButton) findViewById(R.id.search_gallery_button);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String userName = "";
        if(user == null){
            searchbar.setVisibility(View.GONE);
            button.setVisibility(View.INVISIBLE);
            mapFragment.getView().setVisibility(View.INVISIBLE);
        } else {
            userName = user.getEmail().replaceAll("\\.", "@");

        }

        // Remove periods from user name and get reference in database
//        String userName = user.getEmail().replaceAll("\\.", "@");
        dbname = FirebaseDatabase.getInstance().getReference(userName);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag_to_search = searchbar.getText().toString();
                searchByTag(tag_to_search);

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
                        List<File> filesArrayList = new ArrayList<File>();
                        for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                            //Getting the data from snapshot
                            String tempstring = photoSnapshot.getKey().replace("@", ".");
                            String convertFilePath = tempstring.replace("*", "/");

                            filesArrayList.add(new File(convertFilePath));
                        }
                        File[] files = filesArrayList.toArray(new File[filesArrayList.size()]);

                        GalleryAdapter galleryAdapter = new GalleryAdapter(files, getApplicationContext());
                        galleryAdapter.setUpdateMapListener(GalleryActivity.this);
                        recyclerView.setAdapter(galleryAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.d("onCancelled", "Name query cancelled");
                    }
                });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (user != null) {
            mMap = googleMap;
            mMap.clear();

            Query query = dbname
                    .orderByChild("coordinates");
            if (query == null){
                Log.d("Query", "is null");
            } else {
                Log.d("Query", "is not null");
            }
            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("dataSnapshot size", Long.toString(dataSnapshot.getChildrenCount()));
                            for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                                //Getting the data from snapshot
                                HashMap<String, Object> firebaseObjMap = (HashMap<String,Object>)photoSnapshot.getValue();
                                HashMap<String, Object> coordinatesObjMap = (HashMap<String,Object>)firebaseObjMap.get("coordinates");

                                try {
                                    LatLng coordinates = new LatLng((double) coordinatesObjMap.get("latitude"), (double) coordinatesObjMap.get("longitude"));
                                    Log.d("Size of photoSnapShot", Long.toString(photoSnapshot.getChildrenCount()));

                                    Log.d("Latitude in onmapready", Double.toString(coordinates.latitude));
                                    Log.d("Longitude in onmapready", Double.toString(coordinates.longitude));
                                    mMap.addMarker(new MarkerOptions().position(coordinates).title("marker"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
                                } catch (Exception e){
                                    Log.d("coordinatesObjMap", "probably null");
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            Log.d("onCancelled", "Name query cancelled");
                        }
                    });
        }
    }

    @Override
    public void updateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
}
