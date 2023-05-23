package edu.uiuc.cs427app.MainActivity;

import static edu.uiuc.cs427app.GetUserData.getUserName;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.CITY_CONTENT_URI;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.CITY_COUNTRY;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.CITY_ID;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.CITY_NAME;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.ID;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.LATITUDE;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.LONGITUDE;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.PICTURE_URL;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.USER_CITY_LIST_CONTENT_URL;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.USER_CITY_LIST_JOIN_URI;
import static edu.uiuc.cs427app.MainActivity.MyContentProvider.USER_NAME;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uiuc.cs427app.Models.City;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.ThemeActivity;
import edu.uiuc.cs427app.authenticate.SignInActivity;
import edu.uiuc.cs427app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    // included initially by the TAs. no idea what this is
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    // used for displaying the cities
    private RecyclerView rv; // reference to UI element that displays the dynamic list
    private List<City> cities; // list of cities

    private boolean isDarkTheme = false;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;


    /**
     * onCreate is called when the the Main Activity is loaded
     * It updates the title of the action bar and ensures that the theme is correct
     * It also populates the screen with added cities
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the the expected layout for this activity
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Team 6 - " + getUserName(this));

        initializeTheme();

        // add the cities to the UI
        inflateCities();
    }

    /*
        Called when the sun button in the menu bar is pressed
        Redirects to the theme selection activity
     */
    public void onThemeClick(MenuItem menuItem) {
        startActivity(new Intent(this, ThemeActivity.class));
    }

    /**
     * Method is called in onCreate
     * Checks sharedPreferences for the theme and enables the default light or dark mode depending
     * on chosen theme. It also sets up the city cards correctly according to theme
     */
    private void initializeTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("styles", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean(getUserName(this) + "isDarkTheme", false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // depending on the theme we will be using one of the two root layouts in activity_main.xml
        if (isDarkTheme) {
            // set the dark theme layout visible and hide the light theme layout
            findViewById(R.id.listLayout).setVisibility(View.GONE);
            findViewById(R.id.cardsLayout).setVisibility(View.VISIBLE);

            // get the UI view that is used to display the city list
            rv = (RecyclerView) findViewById(R.id.cityCards);
        }
        else {
            // get the UI view that is used to display the city list
            rv = (RecyclerView) findViewById(R.id.cityList);
            // add a divider to the list to separate the items visually
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                    DividerItemDecoration.VERTICAL);
            rv.addItemDecoration(mDividerItemDecoration);
        }
    }

    /**
     * callback method that is called when the action bar is created
     * Inflates menu and changes menu color depending on theme
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add items to the action bar at the top
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);

        // set the correct button icon depending on what theme is displayed rn
        MenuItem nightThemeButton = menu.findItem(R.id.themeMenu);
        if(isDarkTheme){
            nightThemeButton.setIcon(R.drawable.ic_baseline_dark_mode_24);
        }
        else {
            nightThemeButton.setIcon(R.drawable.ic_baseline_light_mode_24);
        }
        return true;
    }

    /**
     * this method is triggered by an event when one of the buttons in the menu (action bar) were
     * pressed
     *
     *  Performs appropriate action depending on button clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addCityMenu: // add new city was clicked
                addCity();
                return true;

            case R.id.logOutMenu: // log out was clicked
                Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false).commit();
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.themeMenu: // change theme was clicked
                // switch the themes
                if(isDarkTheme){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called from onCreate
     * Used to populate the recycler view with cities that user has previously added
     */
    private void inflateCities() {
        cities = initializeDefault();

        // define what happens when an individual item on the list is clicked
        RVClickListener listener = (view, position)->{
            //Open new activity here
            Toast.makeText(this, "City is " + cities.get(position).getCityName(), Toast.LENGTH_SHORT).show();
        };
        RVLongClickListener deleteListener = (view, position)->{
            // define the delete item functionality
            deleteCityofUser(cities.get(position), getUserName(this));
            cities.remove(position);
            rv.getAdapter().notifyItemRemoved(position);
        };

        // depending on the theme we have two different layouts
        // using card view for dark theme and list view for light theme
        if(isDarkTheme) {
            // use card adapter
            CityCardAdapter cardAdapter = new CityCardAdapter(cities, listener, deleteListener);
            rv.setAdapter(cardAdapter);
            rv.setHasFixedSize(true);
            // this layout must be horizontal (default is vertical)
            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

            // add snapping to center each card upon scrolling
            SnapHelper helper = new LinearSnapHelper();
            helper.attachToRecyclerView(rv);
            if (cities.size() > 0) {
                rv.scrollToPosition(cities.size() - 1);
                rv.smoothScrollToPosition(0);
            }
        }
        else {
            // use list adapter
            CityListAdapter listAdapter = new CityListAdapter(cities, listener, deleteListener);
            rv.setAdapter(listAdapter);

            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }

    // button even listener
    public void onAddCity(View v){
        addCity();
    }

    // logic that's responsible for adding the new city
    public void addCity(){
        String apiKey = getString(R.string.google_maps_key);;

        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),apiKey);
        }
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).setTypeFilter(TypeFilter.CITIES)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    // the address is returned as a comma separated string,
    // we want to only extract the state and country form that string
    // ex: "Chicago, IL, USA, 60000" -> "IL, USA"
    private String parseCityAddress(String address) {
        // split the elements of the string into a list by commas
        List<String> addressList = new ArrayList<String>(Arrays.asList(address.split(", ")));
        addressList.remove(0); // remove the name of the city
        addressList.removeIf(x -> x.matches("[0-9]+")); // remove the entries that include numbers
        return String.join(", ", addressList);
    }

    // callback method that is called when a city from result list if clicked
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
//                System.out.println("==============> Place: " + place.toString());
                String pictureUrl = "https://maps.googleapis.com/maps/api/place/photo?" + "maxwidth=1600" + "&photo_reference=" + place.getPhotoMetadatas().get(0).zza() + "&key=" + getString(R.string.google_maps_key);
                City city = new City(place.getId(),
                        place.getName(),
                        parseCityAddress(place.getAddress()),
                        place.getLatLng().latitude,
                        place.getLatLng().longitude,
                        pictureUrl);
                if (!cities.contains(city)) {
                    cities.add(city);
                    addCityToUser(city, getUserName(this));
                    rv.getAdapter().notifyItemInserted(cities.size() - 1);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called when user deletes city
     * Deletes city
     */
    private void deleteCityofUser(City city, String userID)
    {
        int rowDeleted = getContentResolver().delete(USER_CITY_LIST_CONTENT_URL, CITY_ID + "= '" + city.getId() + "' AND " + USER_NAME + "= '" + userID + "'", null );
        // if there is actual record being deleted, check if the city is the last city
        if (rowDeleted>=1)
        {
            // deletes the city in the city table if there is no user holding the city
            Cursor cursor = getContentResolver().query(USER_CITY_LIST_CONTENT_URL, null, CITY_ID + "= '" + city.getId() +"'", null, null);
            if (!cursor.moveToFirst()){
                getContentResolver().delete(CITY_CONTENT_URI, ID + "= '" + city.getId() + "'", null);
            }
        }
    }

    private void addCityToUser(City city, String userID) {
        //query if the city exists or not
        Cursor cursor = getContentResolver().query(CITY_CONTENT_URI, null,  ID + "='" + city.getId() + "'", null, null);
        //add new city if the city does not exist
        if (!cursor.moveToFirst()) {
            addNewCity(city);
        }
        //add record to the UserCityList Table
        ContentValues values = new ContentValues();
        values.put(USER_NAME, userID);
        values.put(CITY_ID, city.getId());
        getContentResolver().insert(USER_CITY_LIST_CONTENT_URL, values);
    }

    private void addNewCity(City city)
    {
        ContentValues values = new ContentValues();
        values.put(ID, city.getId());
        values.put(CITY_NAME, city.getCityName());
        values.put(CITY_COUNTRY, city.getCityCountry());
        values.put(LATITUDE, city.getLatitude());
        values.put(LONGITUDE, city.getLongitude());
        values.put(PICTURE_URL, city.getPictureURL());
        getContentResolver().insert(CITY_CONTENT_URI, values);
    }

    /**
     * Creates a small sample of cities to check how the list looks like
     * without add/remove functionality. TO BE REMOVED IN PROD
     *
     * @return List of cities that we want to be displayed by default
     */
    //TODO: see if the picture url can be cached instead of storing all of it in db
    //static final String pictureUrl = "https://images.pexels.com";
    // all fields are well defined in the table, therefore returning -1 for getColumnIndex is not possible
    @SuppressLint("Range")
    private List<City> initializeDefault() {
        //Get User City List From the Table using inner join
        Cursor cursor = getContentResolver().query(USER_CITY_LIST_JOIN_URI, null,  "u." + USER_NAME + "='" + getUserName(this) + "'", null, null);
        List<City> cities = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    City city = new City();
                    city.setId(cursor.getString(cursor.getColumnIndex(CITY_ID)));
                    city.setCityName(cursor.getString(cursor.getColumnIndex(CITY_NAME)));
                    city.setCityCountry(cursor.getString(cursor.getColumnIndex(CITY_COUNTRY)));
                    city.setLatitude(cursor.getDouble(cursor.getColumnIndex(LATITUDE)));
                    city.setLongitude(cursor.getDouble(cursor.getColumnIndex(LONGITUDE)));
                    city.setPictureURL(cursor.getString(cursor.getColumnIndex(PICTURE_URL)));
                    cities.add(city);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("ControlsProviderService", "Error while trying to get cities from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return cities;
    }
}

