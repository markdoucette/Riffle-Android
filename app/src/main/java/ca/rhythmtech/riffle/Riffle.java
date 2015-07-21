package ca.rhythmtech.riffle;

import android.app.Application;
import ca.rhythmtech.riffle.model.Trip;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Main Application class for settings
 * @author Mark Doucette
 */
public class Riffle extends Application {
    private static final String TAG = "RiffleApplication";
    @Override
    public void onCreate() {
        super.onCreate();

        // Parse.com information
        // enable local datastore
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Trip.class);
        Parse.initialize(this, "576Eu9lNFnRVPhjzDBue7V1AxZFO9nlTeepm4PTa", "q60K8u5tZWTdjOcMOc1hywKIFwzQMOGJvUf7EFu0");
    }
}
