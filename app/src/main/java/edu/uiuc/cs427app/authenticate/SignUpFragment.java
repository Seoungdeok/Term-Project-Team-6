package edu.uiuc.cs427app.authenticate;

import android.os.Bundle;
import edu.uiuc.cs427app.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * Class to create and control the Sign Up page.
 *
 * @author Seoungdeok Jeon
 */
public class SignUpFragment extends Fragment {

    private SignUpListener mSignUpListener;

    /**
     * Called to do initial creation of SignUpFragment.
     *
     * @param savedInstanceState - If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSignUpListener = (SignUpListener) getActivity();
    }

    /**
     * Called to have the fragment instantiate its user interface view
     * with fragment_sign_up.xml file.
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
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        getActivity().setTitle("Create Account");
        final EditText firstNameEditText = view.findViewById(R.id.et_first_name);
        final EditText lastNameEditText = view.findViewById(R.id.et_last_name);
        final EditText uerNameEditText = view.findViewById(R.id.et_user_name);
        final EditText emailEditText = view.findViewById(R.id.et_username);
        final EditText passwordEditText = view.findViewById(R.id.et_password);
        Button signUpButton = view.findViewById(R.id.btn_sign_up);
        // Sign up an account with user's input data
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String userName = uerNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                try{
                    Account account = new Account(firstName, lastName, userName, email, password);
                    if (mSignUpListener != null) {
                        mSignUpListener.signUp(account);
                    }
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getActivity(), "Enter a valid email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    /**
     * Interface for signing up.
     */
    public interface SignUpListener {
        /**
         * Sign up an input account through
         * posting the account to the web service.
         *
         * @param account - account to be signed up.
         */
        void signUp(Account account);
    }

}