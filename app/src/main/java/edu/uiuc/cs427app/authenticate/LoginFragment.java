package edu.uiuc.cs427app.authenticate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.uiuc.cs427app.R;

/**
 * Class to create and control the Login page.
 *
 * @author Seoungdeok Jeon
 */
public class LoginFragment extends Fragment {

    private LoginListener mLoginListener;

    /**
     * Called to do initial creation of LoginFragment.
     *
     * @param savedInstanceState - If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginListener = (LoginListener) getActivity();
    }

    /**
     * Called to have the fragment instantiate its user interface view
     * with fragment_login.xml file.
     *
     * @param inflater           - The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container          - If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState - If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return - View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Sign In");
        final EditText usernameEditText = view.findViewById(R.id.et_username);
        final EditText passwordEditText = view.findViewById(R.id.et_password);
        final TextView errorTextView = view.findViewById(R.id.tv_error);
        Button loginButton = view.findViewById(R.id.btn_login);
        // Login if loginButton is clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                mLoginListener.login(username, password);
            }
        });

        // Launch SignUpFragment if createAccountButton is clicked
        Button createAccountButton = view.findViewById(R.id.btn_create_account);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginListener.launchSignUpFragment();
            }
        });
        return view;
    }

    /**
     * Interface for logging in.
     */
    public interface LoginListener {
        /**
         * Login with user's entered email and password through
         * retrieving corresponding account from the web service.
         *
         * @param email    - user'e email
         * @param password - user's password
         */
        void login(String email, String password);

        /**
         * Launch signUpFragment.
         */
        void launchSignUpFragment();
    }
}