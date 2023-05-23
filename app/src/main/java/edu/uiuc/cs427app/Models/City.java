package edu.uiuc.cs427app.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * This is a class that represents a city in the internal database.
 * The class implements a Parcelable interface, which allows the objects
 * of that class to be serialized and pass around as bytestreams which
 * is required for passing those objects to intents and other Android structs
 *
 * @author Kyr Nastahunin
 *
 */
public class City implements Parcelable {

    private String id;
    private String cityName;
    private String cityCountry; // this can also include the state: "IL, USA"
    private double latitude;
    private double longitude;
    private String pictureURL; // used if we need to load a picture of the city

    // constructor
    public City(String cityName, String cityCountry, double latitude, double longitude, String pictureURL) {
        this.id = "0";
        this.cityName = cityName;
        this.cityCountry = cityCountry;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pictureURL = pictureURL;
    }

    public City(String id, String cityName, String cityCountry, double latitude, double longitude, String pictureURL) {
        this.id = id;
        this.cityName = cityName;
        this.cityCountry = cityCountry;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pictureURL = pictureURL;
    }

    public City() {

    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCountry() {
        return cityCountry;
    }

    public void setCityCountry(String cityCountry) {
        this.cityCountry = cityCountry;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    // generated
    @Override
    public String toString() {
        return "City{" +
                "id='" + id + '\'' +
                ", cityName='" + cityName + '\'' +
                ", cityCountry='" + cityCountry + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", pictureURL='" + pictureURL + '\'' +
                '}';
    }

    // generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City city = (City) o;
        return Double.compare(city.getLatitude(), getLatitude()) == 0 && Double.compare(city.getLongitude(), getLongitude()) == 0 && getId().equals(city.getId()) && getCityName().equals(city.getCityName()) && getCityCountry().equals(city.getCityCountry());
    }

    //generated
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCityName(), getCityCountry(), getLatitude(), getLongitude());
    }

    // Parcelable methods. Called automatically, shouldn't concern the user of the class.
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(cityName);
        dest.writeString(cityCountry);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(pictureURL);
    }

    protected City(Parcel in) {
        id = in.readString();
        cityName = in.readString();
        cityCountry = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        pictureURL = in.readString();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
