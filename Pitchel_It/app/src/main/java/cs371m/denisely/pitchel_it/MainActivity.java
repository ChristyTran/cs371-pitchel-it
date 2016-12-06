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
import android.view.inputmethod.InputMethodManager;
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
    protected NavigationView navigationView;
    MainFragment mainFragment;

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
                    Log.d("Logged in?", "Yes, username is " + user.getDisplayName());
                    userName = user.getDisplayName();
                } else {
                    // User is signed out
                    Log.d("Logged in?", "no user is null");
                    userName = null;
                }
                updateUserDisplay();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFacebook();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerMenu = navigationView.getMenu();

        firebaseInit();
        updateUserDisplay();
        mainFragment = new MainFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, mainFragment);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();

    }

    public void initializeFacebook(){
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // When user selects something from navigation drawer
        int id = item.getItemId();

        if (id == R.id.nav_logon){
            if (id == R.id.nav_logon) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    mAuth.signOut();
                    return true;
                } else {
                    LoginFragment loginFragment = LoginFragment.newInstance();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.add(R.id.main_frame, loginFragment);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            }
        } else if (id == R.id.nav_create_account) {
            CreateAccountFragment createAccountFragment = CreateAccountFragment.newInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.main_frame, createAccountFragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else if (id == R.id.nav_log_out){ //TODO: handle logout
            mAuth.signOut();
            userName = null;
            updateUserDisplay();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        // Hides the keyboard when returning to MainFragment
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    protected void updateUserDisplay() {
        String loginString = "";

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("Current user:", user.getEmail());
            userName = user.getEmail();
            loginString = String.format("Logged in as %s", user.getEmail());
            drawerMenu.findItem(R.id.nav_log_out).setVisible(true);
            drawerMenu.findItem(R.id.nav_create_account).setVisible(false);
            drawerMenu.findItem(R.id.nav_logon).setVisible(false);

        } else {
            Log.d("Current user?", "No current user");
            loginString = "Please log in";
            drawerMenu.findItem(R.id.nav_logon).setVisible(true);
            drawerMenu.findItem(R.id.nav_create_account).setVisible(true);
        }

        // Need to initialize the header of the drawer.
        // Otherwise, can't find the header items on start up.
        View header = navigationView.getHeaderView(0);
        TextView dit = (TextView) header.findViewById(R.id.drawerIDText);
        if (dit != null) { // For some reason, this is always null on app start up
            Log.d("dit", "" + loginString);
            dit.setText(loginString);
        } else {
            Log.d("dit", "is null");
        }

        if (userName == null){
            drawerMenu.findItem(R.id.nav_log_out).setVisible(false);
        }
    }
}
