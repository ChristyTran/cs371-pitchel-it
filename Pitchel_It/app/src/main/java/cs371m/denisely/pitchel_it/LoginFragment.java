package cs371m.denisely.pitchel_it;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
//import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {
    public interface FirebaseLoginInterface {
        void firebaseLoginFinish();
        void firebaseFromLoginToCreateAccount();
    }

    private FirebaseLoginInterface firebaseLoginInterface;
    //private FirebaseAuth mAuth;
    public static String TAG = "Flogin";
    protected View myRootView;
    protected Button loginBut;
    protected Button createFragBut;

    static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            firebaseLoginInterface = (FirebaseLoginInterface) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement FirebaseEmailPasswordInterface ");
//        }
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_or_create, container, false);
        myRootView = v;
        loginBut = (Button) v.findViewById(R.id.login);
        createFragBut = (Button) v.findViewById(R.id.create_account);
        if (loginBut == null || createFragBut == null) {
            Log.d(TAG, "Null buttons!");
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up the login form
        View v = myRootView;
        final EditText usernameView = (EditText) v.findViewById(R.id.username);
        final EditText passwordView = (EditText) v.findViewById(R.id.password);
        //mAuth = FirebaseAuth.getInstance();
        usernameView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_TAB) {
                    passwordView.setFocusableInTouchMode(true);
                    passwordView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        // Sign up button click handler
        createFragBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: Create fragment for new account
//                firebaseLoginInterface.firebaseFromLoginToCreateAccount();
            }
        });

        // Set up the submit button click handler
        loginBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            //TODO: Add logic to log a user on
            }
        });

        //TODO: Add back button for fragment

    }
}