package ca.rhythmtech.riffle.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ca.rhythmtech.riffle.R;
import ca.rhythmtech.riffle.model.Trip;
import ca.rhythmtech.riffle.util.ActivityHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddTripActivity extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String ERROR_NAME = "Please enter a title for the Trip";
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String TAG = "AddTripActivity";
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
    private boolean isEditing = false; // user is editing the Trip ?
    private boolean isViewing = false; // user is just viewing the Trip ?

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
                switch (action) {
                    case DisplayTripsActivity.TRIP_ACTION_VIEW:
                        isViewing = true; // we are in view only mode
                        Log.d(TAG, "In viewing mode");
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

        // address the save button
        if (miSaveButton == null) {
            Log.d(TAG, "Save Button is null");
        }
        else {

            miSaveButton.setEnabled(false);
        }

        invalidateOptionsMenu();
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
            menu.getItem(0).setEnabled(false).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_opt_save_trip:
                if (validateData()) {
                    Trip trip = new Trip();
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

                    if (lastLocation != null) {
                        ParseGeoPoint location = new ParseGeoPoint(lastLocation.getLatitude(),
                                lastLocation.getLongitude());
                        trip.setLocation(location);
                    }

                    if (!etNotes.getText().toString().equals("")) { // add notes if set
                        trip.setNotes(etNotes.getText().toString());
                    }

                    trip.saveInBackground();

                    goToHomeActivity();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
                    tvLocationCoords.setText(String.format("Lat: %f, Long: %f",
                            lastLocation.getLatitude(), lastLocation.getLongitude()));
                }
                break;
            default:
                break;
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
}
