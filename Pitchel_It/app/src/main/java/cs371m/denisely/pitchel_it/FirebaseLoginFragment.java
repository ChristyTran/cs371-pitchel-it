package cs371m.denisely.pitchel_it;

import android.app.Fragment;

/**
 * Created by Denise on 11/22/2016.
 */

public class FirebaseLoginFragment extends Fragment{
    public interface FirebaseLoginInterface {
        void firebaseLoginFinish();
        void firebaseFromLoginToCreateAccount();
    }

}
