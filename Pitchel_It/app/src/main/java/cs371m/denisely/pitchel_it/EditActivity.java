package cs371m.denisely.pitchel_it;

import android.app.Application;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

/**
 * Created by Denise on 11/8/2016.
 */

public class EditActivity extends Application implements IAdobeAuthClientCredentials {
    /* Be sure to fill in the two strings below. */
    private static final String CREATIVE_SDK_CLIENT_ID = "a10a979a33c04624be5979a4b628bb31\n";
    private static final String CREATIVE_SDK_CLIENT_SECRET = "93ff32a5-efa7-4ac2-953e-3cfbd2fc5a5b";

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
