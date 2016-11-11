package cs371m.denisely.pitchel_it;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by Denise on 11/11/2016.
 */

public class OneImage extends Activity {
    public void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.one_image);

        ImageView imageView = (ImageView) findViewById(R.id.one_image);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        
    }
}
