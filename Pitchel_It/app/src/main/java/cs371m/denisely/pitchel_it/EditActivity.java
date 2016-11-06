package cs371m.denisely.pitchel_it;

import ly.img.android.ImgLySdk;

/**
 * Created by Denise on 11/6/2016.
 */

public class EditActivity extends android.app.Application{
    @Override
    public void onCreate(){
        super.onCreate();

        ImgLySdk.init(this);
    }

}
