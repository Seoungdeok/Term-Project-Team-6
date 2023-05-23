package edu.uiuc.cs427app;

import android.content.Context;
import android.content.SharedPreferences;

public class GetUserData {
    public static String getUserName(Context context){
        // Getting the logged in user's username.
        SharedPreferences loggedInUserSharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_logged_in_user), Context.MODE_PRIVATE);

        String loggedInUser = loggedInUserSharedPref.getString("loggedInUser", null);

        return loggedInUser;
    }
}
