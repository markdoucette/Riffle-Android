package ca.rhythmtech.riffle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import ca.rhythmtech.riffle.R;

/**
 * Main entry point for the application Riffle
 * @author Mark Doucette
 * @version 1.0
 */
public class MainActivity extends Activity implements View.OnClickListener{
    public static final String TAG = "MainActivity";
    // ui elements
    private Button btnAddTrip;
    private Button btnViewTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // remove the icon from the actionbar
        getActionBar().setDisplayShowHomeEnabled(false);


        // set the views
        btnAddTrip = (Button) findViewById(R.id.act_main_btn_add_trip);
        btnAddTrip.setOnClickListener(this);
        btnViewTrips = (Button) findViewById(R.id.act_main_btn_view_trip);
        btnViewTrips.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_main_btn_add_trip:
                // go to AddTripActivity
                Intent intent = new Intent(MainActivity.this, AddTripActivity.class);
                startActivity(intent);
                break;
            case R.id.act_main_btn_view_trip:

                break;
            default:
                Log.d(TAG, "Onclick result in default case");
                break;
        }

    }
}
