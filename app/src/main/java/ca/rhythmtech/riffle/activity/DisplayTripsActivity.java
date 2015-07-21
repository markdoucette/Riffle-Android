package ca.rhythmtech.riffle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import ca.rhythmtech.riffle.R;
import ca.rhythmtech.riffle.adapter.TripsViewAdapter;
import ca.rhythmtech.riffle.model.Trip;
import ca.rhythmtech.riffle.util.ActivityHelper;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

public class DisplayTripsActivity extends Activity implements AdapterView.OnItemClickListener {
    public static final String TRIP_ACTION_VIEW = "view";
    public static final String TAG = "DisplayTripActivity";
    public static final String TRIP_STRING = "Trip";
    public static final String TRIP_NOT_FOUND_ERROR = "Error retrieving selected trip";
    public static final String TRIP_ID_KEY = "tripId";
    public static final String TRIP_ACTION_KEY = "tripAction";

    private TripsViewAdapter adapter;
    private ListView lvTrips;
    private ProgressBar pgWaiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trips);
        // remove the icon from the actionbar
        ActivityHelper.setActionBarNoIcon(DisplayTripsActivity.this);

        // set up the views
        initializeViews();

        // Use a query factory to initialize the ListView with all trips first
        ParseQueryAdapter.QueryFactory<Trip> qf = new ParseQueryAdapter.QueryFactory<Trip>() {
            @Override
            public ParseQuery<Trip> create() {
                return ParseQuery.getQuery(TRIP_STRING); // Api limits to 100 items to a getall query
            }
        };

        lvTrips.setOnItemClickListener(this);
        // initialize the adapter and set to the ListView
        adapter = new TripsViewAdapter(this, qf);
        adapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Trip>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(List<Trip> list, Exception e) {
                pgWaiting.setVisibility(View.GONE); // get rid of progress bar once we have data
            }
        });
        lvTrips.setAdapter(adapter);
    }

    // Round up all of our required views
    private void initializeViews() {
        lvTrips = (ListView) findViewById(R.id.act_display_lv_trips);
        pgWaiting = (ProgressBar) findViewById(R.id.progressBar);
        ActivityHelper.setActionBarTitle(DisplayTripsActivity.this, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_trips, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // retrieve the Trip corresponding to the position clicked
        Trip trip = adapter.getItem(position);
        Intent intent = new Intent(DisplayTripsActivity.this, AddTripActivity.class);
        if (trip != null) {
            intent.putExtra(TRIP_ID_KEY, trip.getObjectId());
            intent.putExtra(TRIP_ACTION_KEY, TRIP_ACTION_VIEW);
        }
        else {
            Log.e(TAG, TRIP_NOT_FOUND_ERROR);
            Toast.makeText(DisplayTripsActivity.this, TRIP_NOT_FOUND_ERROR, Toast.LENGTH_SHORT).show();
        }
        startActivity(intent);
    }
}
