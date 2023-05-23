package edu.uiuc.cs427app;

import static edu.uiuc.cs427app.GetUserData.getUserName;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import edu.uiuc.cs427app.MainActivity.MainActivity;

public class ThemeActivity extends AppCompatActivity {

    boolean isDarkTheme;

    /**
     * called when ThemeActivity is started
     *
     * Loads the correct theme and applies it, switches the toggle if dark mode is currently active,
     * and explicitly updates the SharedPreferences to light mode if no mode is specified
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("styles", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean(getUserName(this) + "isDarkTheme", false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_theme);

        Switch toggle = (Switch) findViewById(R.id.switch_theme);
        if (isDarkTheme) {
            toggle.toggle();
        }
        else {
            sharedPreferences.edit().putBoolean(getUserName(this) + "isDarkTheme", false).apply();
        }
    }

    /**
     * listener which is called when DarkMode toggle is pressed, or Switch .toggle function is
     * called (like in the OnCreate function above)
     *
     * Updates SharedPreferences based on whether the switch is toggled (isDarkTheme == True if
     * toggled). Also calls recreate() so that UI updates to reflect new theme.
     */
    public void onToggle(View v) {
        boolean hasNightMode = ((Switch) v).isChecked();
        SharedPreferences sharedPreferences = getSharedPreferences("styles", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(getUserName(this) + "isDarkTheme", hasNightMode).apply();
        recreate();
    }

    /**
     * Called when saveTheme button is pressed.
     *
     * Navigates to MainActivity since the user has decided on their theme.
     */
    public void saveTheme(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }
}