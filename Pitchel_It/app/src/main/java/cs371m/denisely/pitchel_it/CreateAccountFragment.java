package cs371m.denisely.pitchel_it;

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
import android.app.Fragment;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountFragment extends Fragment {
    public interface CreateAccountInterface {
        void firebaseLoginFinish();
    }

    private CreateAccountFragment.CreateAccountInterface CreateAccountInterface;
    private FirebaseAuth mAuth;
    public static String TAG = "FCreateAcct";
    protected View myRootView;
    protected Button createAcctButton;

    static CreateAccountFragment newInstance() {
        CreateAccountFragment CreateAccountFragment = new CreateAccountFragment();
        return CreateAccountFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CreateAccountInterface = (CreateAccountInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create, container, false);
        myRootView = v;
        createAcctButton = (Button) v.findViewById(R.id.create_account);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = myRootView;
        final EditText usernameView = (EditText) v.findViewById(R.id.username);
        final EditText passwordView = (EditText) v.findViewById(R.id.password);
        final EditText passwordAgainView = (EditText) v.findViewById(R.id.password_confirmation);
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
        passwordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_TAB) {
                    passwordAgainView.setFocusableInTouchMode(true);
                    passwordAgainView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        createAcctButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final String username = usernameView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();
                String passwordAgain = passwordAgainView.getText().toString().trim();

                //TODO: validate that passwords are the same

                // Set up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(getActivity());
                dlg.setMessage("Please wait signing up.");
                dlg.show();
                // Set up a new Firebase user
                mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                dlg.dismiss();
                                if (!task.isSuccessful()) {
                                    // Show the error message
                                    Exception e = task.getException();
                                    if (e != null) Log.d(TAG, e.getMessage());
//                                    Snackbar.make(myRootView, "Authentication failed", Snackbar.LENGTH_LONG)
//                                            .setAction("Action", null).show();
                                } else {
                                    CreateAccountInterface.firebaseLoginFinish();
                                }
                            }
                        });

            }
        });
    }
}
