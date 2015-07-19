package ca.rhythmtech.riffle.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ca.rhythmtech.riffle.R;
import ca.rhythmtech.riffle.model.Trip;
import com.parse.ParseGeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddTripActivity extends Activity implements View.OnClickListener {
    public static final String ERROR_NAME = "Please enter a title for the Trip";
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    private EditText etName;
    private Button btnDate;
    private EditText etWeather;
    private EditText etWaterTemp;
    private EditText etLevel;
    private TextView tvLocation;
    private EditText etNotes;

    private DatePickerDialog datePickerDialog; // dialog for choosing a date
    SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        // remove the icon from the actionbar
        getActionBar().setDisplayShowHomeEnabled(false);

        etName = (EditText) findViewById(R.id.act_add_et_name);
        btnDate = (Button) findViewById(R.id.act_add_btn_date);
        btnDate.setOnClickListener(this);


        etWeather = (EditText) findViewById(R.id.act_add_et_weather);
        etWaterTemp = (EditText) findViewById(R.id.act_add_et_watertemp);
        etLevel = (EditText) findViewById(R.id.act_add_et_level);
        tvLocation = (TextView) findViewById(R.id.act_add_tv_location);
        etNotes = (EditText) findViewById(R.id.act_add_et_notes);

        // initialize the date button text to today's date for new Trip
        btnDate.setText(getTodaysDate());
        setChosenDate();

    }

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

    // Helper methods
    // validate some of the mandatory data: Name
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveTask:
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
                        Log.d("AddTripActivity", String.format("Error parsing date: %s", e.getMessage()));
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

                    // test location
                    // TODO: Remove after location is implemented
                    ParseGeoPoint location = new ParseGeoPoint(49.290186, -123.137372);
                    trip.setLocation(location);

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
            default:
                break;
        }

    }
}
