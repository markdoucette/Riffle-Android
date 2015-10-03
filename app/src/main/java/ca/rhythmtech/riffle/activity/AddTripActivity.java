package ca.rhythmtech.riffle.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ca.rhythmtech.riffle.R;
import ca.rhythmtech.riffle.fragment.DeleteTripAlertFragment;
import ca.rhythmtech.riffle.fragment.LocationAlertFragment;
import ca.rhythmtech.riffle.model.Trip;
import ca.rhythmtech.riffle.util.ActivityHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddTripActivity extends Activity implements LocationAlertFragment
        .OnCurrentLocationListener, View.OnClickListener, GoogleApiClient
        .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String ERROR_NAME = "Please enter a title for the Trip";
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String TAG = "AddTripActivity";
    private static final int SHARE_MENU_ID = Menu.FIRST + 1;
    private static final int EDIT_MENU_ID = Menu.FIRST + 2;
    private static final int DELETE_MENU_ID = Menu.FIRST + 3;
    private static final int TAKE_PHOTO_REQUEST = 10;
    public static final String DELETE_TRIP_ALERT_FRAGMENT = "DeleteTripAlertFragment";
    public static final String NO_TRIP_ERROR = "No Trip to delete";

    private EditText etName;
    private Button btnDate;
    private EditText etWeather;
    private EditText etWaterTemp;
    private EditText etLevel;
    private ImageButton ebLocation;
    private TextView tvLocationCoords;
    private View miSaveButton;
    private EditText etNotes;
    private DatePickerDialog datePickerDialog; // dialog for choosing a date
    private SimpleDateFormat dateFormat;
    private GoogleApiClient mGoogleApiClient;

    private Location lastLocation;
    private Location chosenLocation;
    private Trip trip;
    private boolean isEditing = false; // user is editing the Trip ?
    private boolean isViewing = false; // user is just viewing the Trip ?
    private boolean isUsingCurrentLocation = true;
    private FragmentManager fragmentManager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        // remove the icon from the actionbar
        ActivityHelper.setActionBarNoIcon(AddTripActivity.this);

        // Set up the Google Play Services API for the Location
        buildGoogleApiClient();

        initializeViews();

        // Set the date
        setChosenDate();
    }

    // Round up all of our required views
    private void initializeViews() {
        etName = (EditText) findViewById(R.id.act_add_et_name);
        btnDate = (Button) findViewById(R.id.act_add_btn_date);
        btnDate.setOnClickListener(this);
        etWeather = (EditText) findViewById(R.id.act_add_et_weather);
        etWaterTemp = (EditText) findViewById(R.id.act_add_et_watertemp);
        etLevel = (EditText) findViewById(R.id.act_add_et_level);
        ebLocation = (ImageButton) findViewById(R.id.act_add_imgbtn_mylocation);
        ebLocation.setOnClickListener(this);
        tvLocationCoords = (TextView) findViewById(R.id.act_add_tv_coords);
        etNotes = (EditText) findViewById(R.id.act_add_et_notes);
        // initialize the date button text to today's date for new Trip
        btnDate.setText(getTodaysDate());
        miSaveButton = findViewById(R.id.menu_opt_save_trip);

        // get rid of the title
        ActivityHelper.setActionBarTitle(AddTripActivity.this, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get the intent and check where we came from
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getStringExtra(DisplayTripsActivity.TRIP_ACTION_KEY);
            String tripId = intent.getStringExtra(DisplayTripsActivity.TRIP_ID_KEY);
            if (action != null && !action.equals("")) {
                Trip trip = new Trip();
                ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
                try {
                    trip = query.get(tripId);
                }
                catch (com.parse.ParseException e) {
                    Log.e(TAG, DisplayTripsActivity.TRIP_NOT_FOUND_ERROR);
                }
                populateTripsField(trip);
                this.trip = trip; // save our trip for later use
                switch (action) {
                    case DisplayTripsActivity.TRIP_ACTION_VIEW:
                        isViewing = true; // we are in view only mode
                        populateTripsField(trip);
                        setViewOnlyMode();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void setViewOnlyMode() {
        // disable the editing
        ActivityHelper.setReadOnly(etName, true);
        ActivityHelper.setReadOnly(btnDate, true);
        ActivityHelper.setReadOnly(etWeather, true);
        ActivityHelper.setReadOnly(etWaterTemp, true);
        ActivityHelper.setReadOnly(etLevel, true);
        ActivityHelper.setReadOnly(tvLocationCoords, true);
        ActivityHelper.setReadOnly(etNotes, true);
        ActivityHelper.setReadOnly(ebLocation, true);

        // address the save button
        if (miSaveButton == null) {
            Log.d(TAG, "Save Button is null");
        }
        else {

            miSaveButton.setEnabled(false);
        }

        invalidateOptionsMenu();
    }

    private void setEditMode() {
        // enable the editing
        ActivityHelper.setReadOnly(etName, false);
        ActivityHelper.setReadOnly(btnDate, false);
        ActivityHelper.setReadOnly(etWeather, false);
        ActivityHelper.setReadOnly(etWaterTemp, false);
        ActivityHelper.setReadOnly(etLevel, false);
        ActivityHelper.setReadOnly(tvLocationCoords, false);
        ActivityHelper.setReadOnly(etNotes, false);
        ActivityHelper.setReadOnly(ebLocation, false);

    }


    // Set the fields with the retrieved trip
    private void populateTripsField(Trip trip) {
        if (trip != null) {
            etName.setText(trip.getName());
            Calendar cal = trip.getDate();
            btnDate.setText(dateFormat.format(cal.getTime()));
            if (trip.getWeather() != null) {
                etWeather.setText(trip.getWeather());
            }
            etWaterTemp.setText(String.valueOf(trip.getWaterTempDegC()));
            etLevel.setText(String.valueOf(trip.getLevelMeters()));

            if (trip.getLocation() != null) {
                ParseGeoPoint loc = trip.getLocation();
                tvLocationCoords.setText(String.format("Lat: %f, Long: %f",
                        loc.getLatitude(), loc.getLongitude()));
            }

            if (trip.getNotes() != null) {
                etNotes.setText(trip.getNotes());
            }
        }
    }

    // Use our custom Alert fragment to confirm delete and kill the Task if required.
    private void showDeleteDialog() {
        if (trip != null) {
            DeleteTripAlertFragment fragment = DeleteTripAlertFragment.newInstance(trip.getObjectId());
            fragment.show(fragmentManager, DELETE_TRIP_ALERT_FRAGMENT);
        }
        else {
            Toast.makeText(AddTripActivity.this, NO_TRIP_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    // User our custom Alert fragment to query the user if they want to use the current location
    // or to get it from Google Maps
    private void showLocationDialog(double latitude, double longitude) {
        LocationAlertFragment fragment = LocationAlertFragment.newInstance(latitude, longitude);
        fragment.show(fragmentManager, LocationAlertFragment.TAG);
    }

    /*
        Set the date chosen by the user in the DatePickerDialog using the default format
         */
    private void setChosenDate() {
        Calendar calDate = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                btnDate.setText(dateFormat.format(newDate.getTime()));
            }
        },
                calDate.get(Calendar.YEAR), calDate.get(Calendar.MONTH), calDate.get(Calendar
                .DAY_OF_MONTH)
        );
    }

    // validate some of the mandatory data: Name, Date (Date is required by proxy since it is
    // first automatically set to the current date until the user changes it
    private boolean validateData() {
        String msg;
        if ("".equals(etName.getText().toString().trim())) {
            msg = ERROR_NAME;
            Toast.makeText(AddTripActivity.this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /*
    Initially for the Add Trip screen we just set the text on the Date button to todays date in a
    simple date format
     */
    private String getTodaysDate() {
        dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return dateFormat.format(new Date());
    }

    // send to home screen
    private void goToHomeActivity() {
        // back to the display activity
        Intent intent = new Intent(AddTripActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToTakePhotoActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
        }
    }


    /*
    Using the Google Api's for Location Services
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_trip, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isViewing) {
            menu.clear();
            menu.add(0, SHARE_MENU_ID, Menu.NONE, R.string.shareTrip).setIcon(R.drawable
                    .ic_share_white_24dp).setShowAsAction(MenuItem
                    .SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            menu.add(0, EDIT_MENU_ID, Menu.NONE, R.string.editTrip).setIcon(R.drawable
                    .ic_mode_edit_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem
                    .SHOW_AS_ACTION_WITH_TEXT);
        }
        else if (isEditing) {
            menu.clear();
            menu.add(0, R.id.menu_opt_photo, Menu.NONE, R.string.menu_take_photo).setIcon(R.drawable
                    .ic_camera_alt_white_24dp).setShowAsAction(MenuItem
                    .SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            menu.add(0, R.id.menu_opt_save_trip, Menu.NONE, R.string.saveTask).setIcon(R.drawable
                    .ic_save_white_24dp).setShowAsAction(MenuItem
                    .SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            menu.add(0, DELETE_MENU_ID, Menu.NONE, "Delete")
                    .setIcon(R.drawable.ic_remove_circle_outline_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_opt_save_trip:
                Trip trip = new Trip();
                if (isEditing && this.trip != null) {
                    trip = this.trip; // we are updating fields not creating a new save
                }
                if (validateData()) {
                    trip.setName(etName.getText().toString());
                    // Handle the text date
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
                    Date date = new Date(); // default date to start
                    try { // capture the date string from the button
                        date = dateFormat.parse(btnDate.getText().toString());
                    }
                    catch (ParseException e) {
                        Log.d(TAG, String.format("Error parsing date: %s", e.getMessage()));
                    }
                    Calendar cDate = Calendar.getInstance();
                    cDate.setTime(date);
                    trip.setDate(cDate);
                    if (!etWeather.getText().toString().equals("")) { // add weather if set
                        trip.setWeather(etWeather.getText().toString());
                    }
                    if (!etWaterTemp.getText().toString().equals("")) { // add water temp if set
                        trip.setWaterTempDegC(Double.valueOf(etWaterTemp.getText().toString()));
                    }
                    if (!etLevel.getText().toString().equals("")) { // add water level if set
                        trip.setLevelMeters(Double.valueOf(etLevel.getText().toString()));
                    }

                    if (lastLocation != null && isUsingCurrentLocation) {
                        ParseGeoPoint location = new ParseGeoPoint(lastLocation.getLatitude(),
                                lastLocation.getLongitude());
                        trip.setLocation(location);
                    }
                    else if (chosenLocation != null && !isUsingCurrentLocation) {
                        ParseGeoPoint location = new ParseGeoPoint(chosenLocation.getLatitude(),
                                chosenLocation.getLongitude());
                        trip.setLocation(location);
                    }

                    if (!etNotes.getText().toString().equals("")) { // add notes if set
                        trip.setNotes(etNotes.getText().toString());
                    }

                    trip.saveInBackground();

                    goToHomeActivity();
                }
                break;
            case SHARE_MENU_ID:
                emailTrip(this.trip);
                break;
            case EDIT_MENU_ID:
                isViewing = false;
                isEditing = true;
                setEditMode();
                invalidateOptionsMenu();
                break;
            case DELETE_MENU_ID:
                showDeleteDialog();
                break;
            case R.id.menu_opt_photo:
                goToTakePhotoActivity();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // email the trip
    // TODO: Build proper message to include Trip details
    private void emailTrip(Trip trip) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        intent.putExtra(Intent.EXTRA_CC, new String[]{""});
        intent.putExtra(Intent.EXTRA_SUBJECT, trip.getName());
        intent.putExtra(Intent.EXTRA_TEXT, trip.getNotes());
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_add_btn_date:
                datePickerDialog.show();
                break;
            case R.id.act_add_imgbtn_mylocation:
                // Get the Latitude & Longitude from Location Services
                if (lastLocation != null) {
                    Log.d(TAG, String.format("Latitude: %f, Longitude: %f",
                            lastLocation.getLatitude(), lastLocation.getLongitude()));

                    showLocationDialog(lastLocation.getLatitude(), lastLocation.getLongitude());
                }
                else {
                    showLocationDialog(0.0, 0.0);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LocationAlertFragment.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    chosenLocation = new Location("");
                    chosenLocation.setLatitude(latLng.latitude);
                    chosenLocation.setLongitude(latLng.longitude);
                    isUsingCurrentLocation = false;
                }
                Log.d(TAG, String.format("Latitude: %f, Longitude: %f",
                        latLng.latitude, latLng.longitude));
                tvLocationCoords.setText(String.format("Lat: %f, Long: %f",
                        latLng.latitude, latLng.longitude));
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // retrieve the last location delivered by Location Services
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Location Connection failed: " + connectionResult.toString());

    }

    @Override
    public void useCurrentLocation() { // user has chosen to use the current location
        isUsingCurrentLocation = true;
        if (lastLocation != null) {
            tvLocationCoords.setText(String.format("Lat: %f, Long: %f",
                    lastLocation.getLatitude(), lastLocation.getLongitude()));
        }
    }

}
