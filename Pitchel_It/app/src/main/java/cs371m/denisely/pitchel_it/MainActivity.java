package cs371m.denisely.pitchel_it;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static android.os.Environment.getExternalStorageState;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    protected ActionBarDrawerToggle toggle;
    protected Menu drawerMenu;
    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Putting it here means you can see it change
//                updateUserDisplay();
                Toast.makeText(getApplicationContext(), "Poop", Toast.LENGTH_SHORT).show();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerMenu = navigationView.getMenu();


        mainFragment = new MainFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, mainFragment);
        // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
//        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }

//    @Override
//    public void onStart(){
//        super.onStart();
//    }

    public boolean onNavigationItemSelected(MenuItem item) {
        return true;

    }

//        protected void updateUserDisplay() {
//        String loginString = "";
//        String userString = userName;
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            loginString = String.format("Log out as %s", userName);
//        } else {
//            userString = "Please log in";
//            loginString = "Login";
//        }
//        TextView dit = (TextView) findViewById(R.id.drawerIDText);
//        if (dit != null) {
//            dit.setText(userString);
//        }
//        // findViewById does not work for menu items.
//        MenuItem logMenu = (MenuItem) drawerMenu.findItem(R.id.nav_login);
//        if (logMenu != null) {
//            logMenu.setTitle(loginString);
//            logMenu.setTitleCondensed(loginString);
//        }

//    }

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
