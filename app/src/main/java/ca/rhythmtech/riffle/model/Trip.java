package ca.rhythmtech.riffle.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Model class for representing a Trip in our application
 *
 * @author Mark Doucette
 * @version 1.0
 */
@ParseClassName("Trip")
public class Trip extends ParseObject {
    public static final String DEFAULT_TRIP_NAME = "My Fishing Trip";
    public static final String DEFAULT_WEATHER = "Sunny";
    public static final double DEFAULT_LEVEL = 0.0;

    private static final String DATE_FIELD = "date";
    private static final String WEATHER_FIELD = "weather";
    private static final String WATER_TEMP_FIELD = "water_temp";
    private static final String LEVEL_FIELD = "level";
    private static final String LOCATION_FIELD = "location";
    private static final String NOTES_FIELD = "notes";
    private static final String NAME_FIELD = "name";

    /*
    This model will be using the Parse.com api for our datastore
     */
    // Default ctor required by ParseObject
    public Trip() {
    }

    /**
     * The name of the Trip
     *
     * @return the name of the Trip
     */
    public String getName() {
        return getString(NAME_FIELD);
    }

    /**
     * Set the name of the Trip, (place or river/lake)
     *
     * @param name The name of the Trip
     */
    public void setName(String name) {
        if (name == null) {
            put(NAME_FIELD, DEFAULT_TRIP_NAME);
        }
        else {
            put(NAME_FIELD, name);
        }
    }

    /**
     * Get the date of the Trip
     *
     * @return The date of the Trip
     */
    public Calendar getDate() {
        Calendar date = Calendar.getInstance();
        Date tempDate = new Date();
        tempDate = getDate(DATE_FIELD);
        if (tempDate == null) {
            return date; // just return the current date
        }

        date.setTime(tempDate); // set to received date
        return date;
    }

    /**
     * Set the date based on the Calendar date provided by the DatePicker.
     *
     * @param date The date in Calendar object format
     */
    public void setDate(Calendar date) {
        Date setDate = new Date();
        if (date == null) { // we didn't get a valid date so put the current date
            put(DATE_FIELD, setDate);
        }
        else {
            setDate = date.getTime();
            put(DATE_FIELD, setDate);
        }
    }

    /**
     * Get the weather for the Trip
     *
     * @return The weather string
     */
    public String getWeather() {
        return getString(WEATHER_FIELD);
    }

    /**
     * Set the weather for the Trip
     *
     * @param weather The weather string
     */
    public void setWeather(String weather) {
        if (weather == null) {
            put(WEATHER_FIELD, DEFAULT_WEATHER);
        }
        else {
            put(WEATHER_FIELD, weather);
        }
    }

    /**
     * Get the water temperature in degrees Celsius for the Trip
     * @return the water temperature in degrees celsius
     */
    public double getWaterTempDegC() {
        return getDouble(WATER_TEMP_FIELD);
    }

    /**
     * Set the water temperature in degrees celsius for the Trip
     * @param tempDegC the temperature in degrees celsius
     */
    public void setWaterTempDegC(double tempDegC) {
        put(WATER_TEMP_FIELD, tempDegC);
    }

    /**
     * Get the river/lake level in metres
     *
     * @return The river/lake level
     */
    public double getLevelMeters() {
        return getDouble(LEVEL_FIELD);
    }

    /**
     * Set the river/lake level in metres
     *
     * @param levelMeters The river level
     */
    public void setLevelMeters(double levelMeters) {
        if (levelMeters < 0.0) {
            put(LEVEL_FIELD, DEFAULT_LEVEL);
        }
        else {
            put(LEVEL_FIELD, levelMeters);
        }
    }

    /**
     * Get the location in the form of a ParseGeoPoint object
     * @return location (ParseGeoPoint)
     */
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION_FIELD);
    }

    /**
     * Set the location in the form of a ParseGeoPoint object
     * @param location the location (ParseGeoPoint)
     */
    public void setLocation(ParseGeoPoint location) {
        if (location == null) {
            put(LOCATION_FIELD, new ParseGeoPoint());
        }
        else {
            put(LOCATION_FIELD, location);
        }
    }

    /**
     * Get the notes for the Trip
     * @return The notes
     */
    public String getNotes() {
        return getString(NOTES_FIELD);
    }

    /**
     * Set the notes for the Trip
     * @param notes the notes
     */
    public void setNotes(String notes) {
        if (notes == null) {
            put(NOTES_FIELD, "");
        }
        else {
            put(NOTES_FIELD, notes);
        }
    }


}
