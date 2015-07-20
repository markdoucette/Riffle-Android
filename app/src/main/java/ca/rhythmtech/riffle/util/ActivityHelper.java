package ca.rhythmtech.riffle.util;

import android.app.ActionBar;
import android.app.Activity;

/**
 * Collection of static helper methods for commonly required tasks in Activity
 * @author Mark Doucette
 */
public class ActivityHelper {

    public static void setActionBarNoIcon(Activity activity) {
        // remove the icon from the actionbar
        ActionBar actionBar = activity.getActionBar();

        if (actionBar != null) {
            activity.getActionBar().setDisplayShowHomeEnabled(false);
        }
    }
}
