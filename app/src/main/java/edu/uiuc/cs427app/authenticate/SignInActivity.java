package edu.uiuc.cs427app.authenticate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import edu.uiuc.cs427app.MainActivity.MainActivity;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.ThemeActivity;

public class SignInActivity extends AppCompatActivity
        implements LoginFragment.LoginListener, SignUpFragment.SignUpListener {

    private SharedPreferences mSharedPreferences;

    /**
     * Called when the activity is starting.
     * It inflates the activity's UI using activity_sign_in.xml file.
     *
     * @param savedInstanceState - If the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        // Checks if login has been done. If so, go to MainActivity class.
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        if (!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sign_in_layout, new LoginFragment())
                    .commit();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Overridden method from LoginFragment.LoginListener interface.
     * It logs in with user's entered username and password through
     * retrieving corresponding account from the web service.
     *
     * @param username    - user'e username
     * @param password - user's password
     */
    @Override
    public void login(String username, String password) {
        // Get the user's stored password
        SharedPreferences usersSharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_usernames_to_passwords), Context.MODE_PRIVATE);

        String storedPassword = usersSharedPref.getString(username, null);

        if (password.equals(storedPassword)) {
            // Storing the logged in user's username.
            SharedPreferences loggedInUserSharedPref = this.getSharedPreferences(
                    getString(R.string.preference_file_logged_in_user), Context.MODE_PRIVATE);

            SharedPreferences.Editor loggedInUserEditor = loggedInUserSharedPref.edit();
            loggedInUserEditor.putString("loggedInUser", username);
            loggedInUserEditor.commit();

            Toast.makeText(getApplicationContext(), "Logged in successfully"
                    , Toast.LENGTH_SHORT).show();

            // Launch the user's main cities list page
            launchMain();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter in correct username or password."
                    , Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Overridden method from LoginFragment.LoginListener interface.
     * It launches signUpFragment.
     */
    @Override
    public void launchSignUpFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.sign_in_layout, new SignUpFragment())
                .addToBackStack(null)
                .commit();
    }

    /**
     * Launch MainActivity class with data stored in mSharedPreferences.
     */
    private void launchMain() {
        mSharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), true)
            .commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Overridden method from SignUpFragment.SignUpListener interface.
     * It signs up an input account through
     * posting the account to the web service.
     *
     * @param account - The account to be signed up.
     */
    @Override
    public void signUp(Account account) {

        // Storing the username mappings to their passwords.
        SharedPreferences usersSharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_usernames_to_passwords), Context.MODE_PRIVATE);

        SharedPreferences.Editor usersEditor = usersSharedPref.edit();
        usersEditor.putString(account.getUserName(), account.getPassword());
        usersEditor.commit();

        // Storing the usernames mappings to their email addresses.
        SharedPreferences emailsSharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_usernames_to_emails), Context.MODE_PRIVATE);

        SharedPreferences.Editor emailsEditor = emailsSharedPref.edit();
        emailsEditor.putString(account.getUserName(), account.getEmail());
        emailsEditor.commit();

        SharedPreferences firstNamesSharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_usernames_to_first_names), Context.MODE_PRIVATE);

        // Storing the usernames mappings to their first names.
        SharedPreferences.Editor firstNamesEditor = firstNamesSharedPref.edit();
        firstNamesEditor.putString(account.getUserName(), account.getFirstName());
        firstNamesEditor.commit();

        // Storing the usernames mappings to their last names.
        SharedPreferences lastNamesSharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_usernames_to_last_name), Context.MODE_PRIVATE);

        SharedPreferences.Editor lastNamesEditor = lastNamesSharedPref.edit();
        lastNamesEditor.putString(account.getUserName(), account.getLastName());
        lastNamesEditor.commit();

        // Storing the logged in user's username.
        SharedPreferences loggedInUserSharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_logged_in_user), Context.MODE_PRIVATE);

        SharedPreferences.Editor loggedInUserEditor = loggedInUserSharedPref.edit();
        loggedInUserEditor.putString("loggedInUser", account.getUserName());
        loggedInUserEditor.commit();
        
        Toast.makeText(getApplicationContext(), "Account created successfully"
                , Toast.LENGTH_SHORT).show();

        mSharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), true)
                .commit();

        Intent intent = new Intent(this, ThemeActivity.class);
        startActivity(intent);
        finish();
    }
}