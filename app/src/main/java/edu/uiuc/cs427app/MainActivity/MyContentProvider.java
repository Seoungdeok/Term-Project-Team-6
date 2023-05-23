package edu.uiuc.cs427app.MainActivity;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {
    public MyContentProvider() {
    }
    // defining authority so that other application can access it
    static final String PROVIDER_NAME = "com.city.provider";

    // defining content URI
    private static final String CITY_URL = "content://" + PROVIDER_NAME + "/cities";
    private static final String USER_CITY_LIST_JOIN_URL = "content://" + PROVIDER_NAME + "/user_cities/join";
    private static final String USER_CITY_LIST_URL = "content://" + PROVIDER_NAME + "/user_cities";

    // parsing the content URI
    static final Uri CITY_CONTENT_URI = Uri.parse(CITY_URL);
    static final Uri USER_CITY_LIST_JOIN_URI = Uri.parse(USER_CITY_LIST_JOIN_URL);
    static final Uri USER_CITY_LIST_CONTENT_URL = Uri.parse(USER_CITY_LIST_URL);

    //for the UserCityList table fields
    static final String USER_NAME = "USER_NAME";
    static final String CITY_ID = "CITYID";

    // for the city table fields
    static final String ID = "ID";
    static final String CITY_NAME = "NAME";
    static final String CITY_COUNTRY = "COUNTRY";
    static final String LATITUDE = "LATITUDE";
    static final String LONGITUDE = "LONGITUDE";
    static final String PICTURE_URL = "PICTUREURL";

    //the inner join query statement for the query method
    private static final String INNER_JOIN_CITY = "userCityList u INNER JOIN cities c ON (u.CITYID = c.id)";

    //3 code cases, first is just city table, second is for the inner join query, third is for the UserCityList Table
    private static final int City = 1;
    private static final int UserCityListJoin = 2;
    private static final int UserCityList = 3;
    private static final UriMatcher uriMatcher;

    static {

        // to match the content URI
        // every time user access table under content provider
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // to access the table
        uriMatcher.addURI(PROVIDER_NAME, "cities", City);

        uriMatcher.addURI(PROVIDER_NAME,"user_cities/join", UserCityListJoin);

        uriMatcher.addURI(PROVIDER_NAME,"user_cities", UserCityList);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case City:
                count = db.delete(CITY_TABLE_NAME, selection, selectionArgs);
                break;
            case UserCityList:
                count = db.delete(USER_CITY_LIST_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case City:
                return "vnd.android.cursor.dir/cities";
            case UserCityList:
                return "vnd.android.cursor.dir/user_cities";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri _uri = null;
        switch (uriMatcher.match(uri))
        {
            case City:
            {
                long rowID = db.insert(CITY_TABLE_NAME, "", values);
                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(CITY_CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            }
            case UserCityList:
            {
                long rowID = db.insert(USER_CITY_LIST_TABLE_NAME, "", values);
                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(USER_CITY_LIST_JOIN_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            }
            default: throw new SQLiteException("Failed to add a record into " + uri);
        }
        return _uri;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case City:
                qb.setTables(CITY_TABLE_NAME);
                break;
            case UserCityListJoin:
                qb.setTables(INNER_JOIN_CITY);
                break;
            case UserCityList:
                qb.setTables(USER_CITY_LIST_TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case City:
                count = db.update(CITY_TABLE_NAME, values, selection, selectionArgs);
                break;
            case UserCityList:
                count = db.update(USER_CITY_LIST_TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    // creating object of database
    // to perform query
    private SQLiteDatabase db;

    // declaring name of the database
    static final String DATABASE_NAME = "USERDB";

    // declaring table name of the database
    static final String CITY_TABLE_NAME = "cities";
    static final String USER_CITY_LIST_TABLE_NAME = "userCityList";
    // declaring version of the database
    static final int DATABASE_VERSION = 1;

    // sql query to create the table
    static final String CREATE_CITY_DB_TABLE = " CREATE TABLE IF NOT EXISTS " + CITY_TABLE_NAME
            +  "(ID TEXT PRIMARY KEY, "
            + CITY_NAME + " TEXT NOT NULL, "
            + CITY_COUNTRY + " TEXT NOT NULL, "
            + LATITUDE + " REAL, "
            + LONGITUDE + " REAL, "
            + PICTURE_URL + " TEXT"
            + ");";
    static final String CREATE_USER_CITY_LIST_DB_TABLE = " CREATE TABLE IF NOT EXISTS  " + USER_CITY_LIST_TABLE_NAME
            +  "(USER_NAME TEXT, "
            + CITY_ID + " TEXT, "
            + "PRIMARY KEY (USER_NAME, CITYID)"
            + ");";
    // creating a database
    private static class DatabaseHelper extends SQLiteOpenHelper {
        // defining a constructor
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // creating a table in the database
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_CITY_DB_TABLE);
            db.execSQL(CREATE_USER_CITY_LIST_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // sql query to drop a table
            // having similar name
            db.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE_NAME);
            onCreate(db);
        }
    }
}