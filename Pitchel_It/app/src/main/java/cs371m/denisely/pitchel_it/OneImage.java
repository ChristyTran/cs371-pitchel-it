package cs371m.denisely.pitchel_it;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

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
    Button share_fb;
    Button share_twitter;
    Button share_ig;

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    public void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.one_image);

        imageView = (ImageView) findViewById(R.id.one_image);
        textView = (TextView)findViewById(R.id.image_name);
        share_fb = (Button)findViewById(R.id.share_facebook);
        share_twitter = (Button)findViewById(R.id.share_twitter);
        share_ig = (Button)findViewById(R.id.share_instagram);

        Intent intent = getIntent();
        String file_path = intent.getStringExtra("thumbnail_path");

        Bitmap bitmap = BitmapFactory.decodeFile(file_path);
        imageView.setImageBitmap(bitmap);
        textView.setText(file_path.toString());

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
}
