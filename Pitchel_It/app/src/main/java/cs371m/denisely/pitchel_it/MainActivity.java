package cs371m.denisely.pitchel_it;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.os.Environment.getExternalStorageState;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoginFragment.LoginInterface,
        CreateAccountFragment.CreateAccountInterface
{

    protected ActionBarDrawerToggle toggle;
    protected Menu drawerMenu;
    MainFragment mainFragment;

    public static String TAG = "DemoFirebase";
    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected String userName;

    protected void firebaseInit() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    userName = user.getDisplayName();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    userName = null;
                }
                Log.d(TAG, "userName="+userName);
                updateUserDisplay();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Generate Facebook key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "cs371m.denisely.pitchel_it",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Putting it here means you can see it change
//                updateUserDisplay();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerMenu = navigationView.getMenu();


        firebaseInit();
        updateUserDisplay();
        mainFragment = new MainFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, mainFragment);
        // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
//        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // When user selects something from navigation drawer
        int id = item.getItemId();
//                toggleHamburgerToBack();
//                LoginFragment flf = LoginFragment.newInstance();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                // Replace any other Fragment with our new Details Fragment with the right data
//                ft.add(R.id.main_frame, flf);
//                // Let us come back
//                ft.addToBackStack(null);
//                // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                ft.commit();
//            }
        if (id == R.id.nav_logon){
            if (id == R.id.nav_logon) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    mAuth.signOut(); // Will call updateUserDisplay via callback
                    return true;
                } else {
                    Toast.makeText(this, "LOG ON BUTTON SELECTED", Toast.LENGTH_LONG).show();
                    LoginFragment loginFragment = LoginFragment.newInstance();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.add(R.id.main_frame, loginFragment);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            }
        } else if (id == R.id.nav_create_account) {
            Toast.makeText(this, "CREATE ACCOUNT BUTTON SELECTED", Toast.LENGTH_LONG).show();
            CreateAccountFragment createAccountFragment = CreateAccountFragment.newInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.main_frame, createAccountFragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else if (id == R.id.nav_log_out){ //TODO: handle logout
            Toast.makeText(this, "LOG OUT BUBTTON SELECTED", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            userName = null;
            updateUserDisplay();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void firebaseLoginFinish() {
        // Dismiss the Login fragment
        getFragmentManager().popBackStack();
        // Toggle back button to hamburger
        toggle.setDrawerIndicatorEnabled(true);

        updateUserDisplay();
    }

    protected void updateUserDisplay() {
        String loginString = "";
        String userString = userName;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("Current user:", user.getEmail());
            userName = user.getEmail();
            loginString = String.format("Logged in as %s", user.getEmail());
            drawerMenu.findItem(R.id.nav_log_out).setVisible(true);
            drawerMenu.findItem(R.id.nav_create_account).setVisible(false);
        } else {
            Log.d("Current user?", "No current user");
            userString = "Please log in";
            loginString = "Login";
            drawerMenu.findItem(R.id.nav_create_account).setVisible(true);
        }
        TextView dit = (TextView) findViewById(R.id.drawerIDText);
        if (dit != null) {
            dit.setText(userString);
        }
        // findViewById does not work for menu items.
        MenuItem logMenu = (MenuItem) drawerMenu.findItem(R.id.nav_logon);
        if (logMenu != null) {
            logMenu.setTitle(loginString);
            logMenu.setTitleCondensed(loginString);
        }

        if (userName == null){
            drawerMenu.findItem(R.id.nav_log_out).setVisible(false);
        }
    }
}
