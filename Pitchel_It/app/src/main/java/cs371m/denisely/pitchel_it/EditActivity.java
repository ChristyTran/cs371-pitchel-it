package cs371m.denisely.pitchel_it;

import android.app.Application;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

/**
 * Created by Denise on 11/8/2016.
 */

public class EditActivity extends Application implements IAdobeAuthClientCredentials {
    /* Be sure to fill in the two strings below. */
    private static final String CREATIVE_SDK_CLIENT_ID = "362fbe23c0c24e789af42399c9636d58";
    private static final String CREATIVE_SDK_CLIENT_SECRET = "9ae86d91-7eb8-4c58-b1b8-0e6999feea68";

    @Override
    public void onCreate() {
        super.onCreate();
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

}
