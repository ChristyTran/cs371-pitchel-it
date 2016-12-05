package cs371m.denisely.pitchel_it;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {
    public interface LoginInterface {
        void firebaseLoginFinish();
    }

    private LoginInterface LoginInterface;
    private FirebaseAuth mAuth;
    public static String TAG = "Flogin";
    protected View myRootView;
    protected Button loginBut;

    static LoginFragment newInstance() {
        LoginFragment LoginFragment = new LoginFragment();
        return LoginFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LoginInterface = (LoginInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, container, false);
        myRootView = v;
        loginBut = (Button) v.findViewById(R.id.login);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = myRootView;
        final EditText usernameView = (EditText) v.findViewById(R.id.username);
        final EditText passwordView = (EditText) v.findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
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


        loginBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = usernameView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();

                // TODO: Validate username, pw
                final ProgressDialog dlg = new ProgressDialog(getActivity());
                dlg.setMessage("Logging in. Please wait.");
                dlg.show();
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dlg.dismiss();
                                Log.d(TAG, "Sign in complete!!!:" + task.isSuccessful());

                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "Sign in failed!!!!! " + task.getException().getMessage());
                                    // TODO: Put error messages when failed to log in

                                    Snackbar.make(myRootView,
                                            "Authentication failed: " + task.getException().getMessage(),
                                            Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    LoginInterface.firebaseLoginFinish();
                                }
                            }
                        });
            }
        });

    }
}
