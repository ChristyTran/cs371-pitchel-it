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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Denise on 11/11/2016.
 */

public class OneImage extends Activity {
    // FB Sharing help: http://simpledeveloper.com/how-to-share-an-image-on-facebook-in-android/

    ImageView imageView;
    TextView textView;
    EditText addtag;
    Button share_fb;
    Button share_twitter;
    Button share_ig;
    Button submit_tag;
    String convertFilePath;

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbname;

    public void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.one_image);

        imageView = (ImageView) findViewById(R.id.one_image);
        textView = (TextView)findViewById(R.id.image_name);
        share_fb = (Button)findViewById(R.id.share_facebook);
        share_twitter = (Button)findViewById(R.id.share_twitter);
        share_ig = (Button)findViewById(R.id.share_instagram);
        addtag = (EditText) findViewById(R.id.add_tags_text);
        submit_tag = (Button) findViewById(R.id.add_tags_button);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null){
            addtag.setVisibility(View.GONE);
            submit_tag.setVisibility(View.GONE);
        }

        String againFUCK;
        Intent intent = getIntent();
        String file_path = intent.getStringExtra("thumbnail_path");
        againFUCK = file_path.replace(".", "-");
        convertFilePath = againFUCK.replace("/", "*");

        Bitmap bitmap = BitmapFactory.decodeFile(file_path);
        imageView.setImageBitmap(bitmap);
        textView.setText(file_path.toString());


        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

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

        share_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        share_ig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaInstagram()){
                    Intent igShareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    igShareIntent.setType("image/*");
                    Uri imageUri = Uri.fromFile(new File(file_path));
                    igShareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    igShareIntent.setPackage("com.instagram.android");
                    startActivity(igShareIntent);
                }
            }
        });

        submit_tag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String tag = addtag.getText().toString();
                String userName = user.getEmail().replaceAll("\\.", "@");

                dbname = FirebaseDatabase.getInstance().getReference(userName);

                PhotoObject photo = new PhotoObject();
                photo.name = addtag.getText().toString();
                //userDB = FirebaseDatabase.getInstance().getReference(_userName);
                //photo.encodedBytes = Base64.encodeToString(data, Base64.DEFAULT);
                if (dbname != null) {
                    dbname.child(convertFilePath).push().setValue(photo);
                }

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

    private boolean verificaInstagram(){
        boolean installed = false;

        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.instagram.android", 0);
            installed = true;
            Log.d("Instagram", "Instagram is installed");
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
            Log.d("Instagram", "Instagram is NOT installed");
//            Toast.makeText(OneImage.this, "Instagram is not installed!", Toast.LENGTH_SHORT);
        }
        return installed;
    }
}
