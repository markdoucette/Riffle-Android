package ca.rhythmtech.riffle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import ca.rhythmtech.riffle.R;
import ca.rhythmtech.riffle.adapter.TripsViewAdapter;
import ca.rhythmtech.riffle.model.Trip;
import ca.rhythmtech.riffle.util.ActivityHelper;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class DisplayTripsActivity extends Activity implements AdapterView.OnItemClickListener {
    private TripsViewAdapter adapter;
    private ListView lvTrips;

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
                ParseQuery<Trip> query = ParseQuery.getQuery("Trip");
                return query; // Api limits to 100 items to a getall query
            }
        };

        lvTrips.setOnItemClickListener(this);
        // initialize the adapter and set to the ListView
        adapter = new TripsViewAdapter(this, qf);
        lvTrips.setAdapter(adapter);
    }

    // Round up all of our required views
    private void initializeViews() {
        lvTrips = (ListView) findViewById(R.id.act_display_lv_trips);
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

    }
}
