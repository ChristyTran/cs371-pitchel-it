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

//        // Remove periods from user name and get reference in database
//        String userName = user.getEmail().replaceAll("\\.", "@");
//        dbname = FirebaseDatabase.getInstance().getReference(userName);

        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag_to_search = searchbar.getText().toString();
//                searchForTag(tag_to_search);

                // TODO: do a query for tags
                // Hide keyboard after submitting
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null){
            searchbar.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }


    }

    public void fetchImages(){
        File dir = new File(Environment.getExternalStorageDirectory(), "Pictures" + File.separator + "Pitchel It");
        if (dir.isDirectory()){
            listFile = dir.listFiles();
            Log.d("files", "List file length in fetchImages(): " + listFile.length);
        }
    }


//    public void searchForTag(final String tag) {
//        if (dbname == null) {
//            Log.d("Tag", "userDB is null!");
//            return;
//        }
//
//        dbname.child("photo").orderByChild("tag").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                PhotoObject photo = dataSnapshot.getValue(PhotoObject.class);
//                System.out.println(dataSnapshot.getKey() + " has tag: " + photo.tag);
//            }
//        });
//
//    }


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
