package cs371m.denisely.pitchel_it;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Denise on 11/11/2016.
 */

public class OneImage extends Activity {

    ImageView imageView;
    TextView textView;

    public void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.one_image);

        imageView = (ImageView) findViewById(R.id.one_image);
        textView = (TextView)findViewById(R.id.image_name);

        Intent intent = getIntent();
        String file_path = intent.getStringExtra("thumbnail_path");

        Bitmap bitmap = BitmapFactory.decodeFile(file_path);
        imageView.setImageBitmap(bitmap);
        textView.setText(file_path.toString());
    }
}
