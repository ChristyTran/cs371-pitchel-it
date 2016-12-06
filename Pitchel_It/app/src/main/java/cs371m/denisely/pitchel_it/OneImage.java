package cs371m.denisely.pitchel_it;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Denise on 11/11/2016.
 */

public class OneImage extends Activity {
    // FB Sharing help: http://simpledeveloper.com/how-to-share-an-image-on-facebook-in-android/
    // TODO: change submit tag button to cute icon


    private CallbackManager callbackManager;
    private LoginManager loginManager;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbname;

    String convertFilePath;
    String userName;


    public void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.one_image);

        ImageView imageView = (ImageView) findViewById(R.id.one_image);
        TextView textView = (TextView)findViewById(R.id.image_name);
        TextView textTag = (TextView)findViewById(R.id.tag_text) ;
        Button share_fb = (Button)findViewById(R.id.share_facebook);
        Button share_twitter = (Button)findViewById(R.id.share_twitter);
        Button share_ig = (Button)findViewById(R.id.share_instagram);
        Button submit_tag = (Button) findViewById(R.id.add_tags_button);
        EditText addtag = (EditText) findViewById(R.id.add_tags_text);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // If user is not logged in, hide the tag box
        if(user == null){
            addtag.setVisibility(View.GONE);
            submit_tag.setVisibility(View.GONE);
            textTag.setVisibility(View.GONE);
        } else {
            userName = user.getEmail().replaceAll("\\.", "@");
            dbname = FirebaseDatabase.getInstance().getReference(userName);
        }

        // Get file path from extras in intent
        Intent intent = getIntent();
        String file_path = intent.getSerializableExtra("thumbnail_path").toString();

        Bitmap bitmap = BitmapFactory.decodeFile(file_path);
        imageView.setImageBitmap(bitmap);
        textView.setText(file_path);


        // Hide keyboard for edit text initially
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        String againFUCK = file_path.replace(".", "@");
        convertFilePath = againFUCK.replace("/", "*");

        if (user != null) {
            DatabaseReference temp = dbname.child(convertFilePath);
            if (temp == null){
                PhotoObject photo = new PhotoObject("", null);

                String key = dbname.child(convertFilePath).push().getKey();
                dbname.child(convertFilePath).setValue(photo);
            } else {
                dbname.child(convertFilePath).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String tag = (String) dataSnapshot.child("tag").getValue();

                        if (tag == null || tag.equals("")) {
                            textTag.setText("Tag: No tag set");
                        } else {
                            textTag.setText("Tag: " + tag);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }


        // Listener for share on Facebook Button
        share_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackManager = CallbackManager.Factory.create();
                List<String> permissionNeeds = Arrays.asList("publish_actions");
                loginManager = LoginManager.getInstance();
                loginManager.logInWithPublishPermissions(OneImage.this, permissionNeeds);

                loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        sharePhotoToFacebook(bitmap);
                    }

                    @Override
                    public void onCancel()
                    {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        System.out.println("onError");
                    }
                });

            }
        });

        // Listener for share on Twitter button
        share_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyAppInstalled("Twitter")){
                    Intent twitterShareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    twitterShareIntent.setType("image/*");
                    Uri imageUri = Uri.fromFile(new File(file_path));
                    twitterShareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    twitterShareIntent.setPackage("com.twitter.android");
                    startActivity(twitterShareIntent);
                }
            }
        });

        // Listener for share on Instagram button
        share_ig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyAppInstalled("Instagram")){
                    Intent igShareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    igShareIntent.setType("image/*");
                    Uri imageUri = Uri.fromFile(new File(file_path));
                    igShareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    igShareIntent.setPackage("com.instagram.android");
                    startActivity(igShareIntent);
                }
            }
        });

        // Listener for submit tag to database button
        submit_tag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Remove periods from user name and get reference in database
                // Remove periods and back slashes from file path
                String key = dbname.child(convertFilePath).push().getKey();

                // Add tag to database and set text with tag
                String tag = addtag.getText().toString();
                dbname.child(convertFilePath).child("tag").setValue(tag);
                textTag.setText("Tag: " + tag);

                // Hide keyboard after submitting
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    public void sharePhotoToFacebook(Bitmap bitmap){
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    // Verify that Instagram or Twitter is installed
    private boolean verifyAppInstalled(String type){

        switch (type) {
            case "Instagram":
                try {
                    ApplicationInfo info = getPackageManager().getApplicationInfo("com.instagram.android", 0);
                    Log.d("Instagram", "Instagram is installed");
                    return true;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d("Instagram", "Instagram is NOT installed");
                    Toast.makeText(OneImage.this, "Instagram is not installed!", Toast.LENGTH_SHORT);
                    return false;
                }

            case "Twitter":
                try {
                    ApplicationInfo info = getPackageManager().getApplicationInfo("com.twitter.android", 0);
                    Log.d("Twitter", "Twitter is installed");
                    return true;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d("Twitter", "Twitter is NOT installed");
                    Toast.makeText(OneImage.this, "Twitter is not installed!", Toast.LENGTH_SHORT);
                    return false;
                }
        }
        return false;
    }
}
