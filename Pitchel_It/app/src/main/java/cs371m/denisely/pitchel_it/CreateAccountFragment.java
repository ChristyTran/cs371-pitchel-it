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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

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
        try {
            CreateAccountInterface = (CreateAccountInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement CreateAccountInterface ");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.create, container, false);
        // Why doesn't getView return the root view?
        // Alternative is the less savory: getActivity().findViewById(R.id.yourId)
        myRootView = v;
        createAcctButton = (Button) v.findViewById(R.id.create_account);
        if (createAcctButton == null) {
            Log.d(TAG, "Null create account button!");
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up the login form.
        View v = myRootView;
        // These are final because they are accessed from inner classes
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

        // Set up the submit button click handler
        createAcctButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final String username = usernameView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();
                String passwordAgain = passwordAgainView.getText().toString().trim();

//                // Validate the log in data
//                String validateString = EmailPasswordValidate.validate(getResources(),
//                        username, password, passwordAgain);
//                // If there is a validation error, display the error
//                if (validateString.length() > 0) {
//                    Snackbar.make(myRootView, validateString, Snackbar.LENGTH_LONG).show();
//                    return;
//                }

                // Set up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(getActivity());
                dlg.setTitle("Please wait.");
                dlg.setMessage("Signing up.  Please wait.");
                dlg.show();
                // Set up a new Firebase user
                mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                dlg.dismiss();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // Show the error message
                                    Exception e = task.getException();
                                    if (e != null) Log.d(TAG, e.getMessage());
                                    Snackbar.make(myRootView, "Authentication failed", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
//                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                            .setDisplayName(username).build();
//                                    mAuth.getCurrentUser().updateProfile(profileUpdates);
                                    CreateAccountInterface.firebaseLoginFinish();
                                }
                            }
                        });
            }
        });
    }
}
