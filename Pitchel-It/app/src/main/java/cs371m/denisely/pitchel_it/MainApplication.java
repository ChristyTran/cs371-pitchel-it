package cs371m.denisely.pitchel_it;

import android.app.Application;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

/**
 * Created by Denise on 10/25/2016.
 */

public class MainApplication extends Application implements IAdobeAuthClientCredentials {

    /* Be sure to fill in the two strings below. */
    private static final String CREATIVE_SDK_CLIENT_ID = "f9623d62195b4a528fe83d9aecfb090f";
    private static final String CREATIVE_SDK_CLIENT_SECRET = "a47749d6-bcaa-45c2-98ad-36f42ef3ea90";

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
