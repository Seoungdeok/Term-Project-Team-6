package edu.uiuc.cs427app.authenticate;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * User's account class.
 *
 * @author Seoungdeok Jeon
 */
public class Account implements Serializable {

    // Properties of JSON object. Used to GET/POST an account.
    public static final String FIRST_NAME = "first";
    public static final String LAST_NAME = "last";
    public static final String USER_NAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    private String mFirstName;
    private String mLastName;
    private String mUserName;
    private String mEmail;
    private String mPassword;

    public Account(String firstName, String lastName, String username, String email, String password) {
        mFirstName = firstName;
        mLastName = lastName;
        mUserName = username;
        if (isValidEmail(email)) {
            mEmail = email;
        } else {
            throw new IllegalArgumentException("Invalid email");
        }
        mPassword = password;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    /**
     * Email validation pattern.
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    /**
     * Validates if the given input is a valid email address.
     *
     * @param email The email to validate.
     * @return {@code true} if the input is a valid email. {@code false} otherwise.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

}