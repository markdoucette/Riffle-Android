package ca.rhythmtech.riffle.util;

import android.app.ActionBar;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Collection of static helper methods for commonly required tasks in Activity
 *
 * @author Mark Doucette
 */
public class ActivityHelper {

    /**
     * Set the action bar to stop displaying the application icon
     *
     * @param activity The current Activity
     */
    public static void setActionBarNoIcon(Activity activity) {
        // remove the icon from the actionbar
        ActionBar actionBar = activity.getActionBar();

        if (actionBar != null) {
            activity.getActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    /**
     * Set the action bar title
     *
     * @param title    The title to set
     * @param activity The current Activity
     */
    public static void setActionBarTitle(Activity activity, String title) {
        ActionBar actionBar = activity.getActionBar();

        if (actionBar != null && title != null && !title.equals("")) {
            actionBar.setTitle(title);
        } else if (actionBar != null && title != null && title.equals("")) {
           actionBar.setDisplayShowTitleEnabled(false); // we don't want a title
        }
    }

    /* with some help from Stackoverflow */
    public static void setReadOnly(final EditText view, final boolean readOnly) {
        view.setFocusable(!readOnly);
        view.setFocusableInTouchMode(!readOnly);
        view.setClickable(!readOnly);
        view.setLongClickable(!readOnly);
        view.setCursorVisible(!readOnly);
    }

    /* with some help from Stackoverflow */
    public static void setReadOnly(final TextView view, final boolean readOnly) {
        view.setFocusable(!readOnly);
        view.setFocusableInTouchMode(!readOnly);
        view.setClickable(!readOnly);
        view.setLongClickable(!readOnly);
        view.setCursorVisible(!readOnly);
    }

    /* with some help from Stackoverflow */
    public static void setReadOnly(final Button view, final boolean readOnly) {
        view.setFocusable(!readOnly);
        view.setFocusableInTouchMode(!readOnly);
        view.setClickable(!readOnly);
        view.setLongClickable(!readOnly);
        view.setCursorVisible(!readOnly);
    }

    /* with some help from Stackoverflow */
    public static void setReadOnly(final ImageButton view, final boolean readOnly) {
        view.setEnabled(!readOnly);
    }
}
