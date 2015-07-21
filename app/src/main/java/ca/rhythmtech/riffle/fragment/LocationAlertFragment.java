package ca.rhythmtech.riffle.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import ca.rhythmtech.riffle.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

/**
 * Dialog Fragment to ask user if they want to use the current location or get location from
 * Google Maps
 *
 * @author Mark Doucette
 */
public class LocationAlertFragment extends DialogFragment {
    private static final String MESSAGE = "Would you like to save your current location or choose" +
            " from Google Maps?";
    public static final String TITLE = "Choose Location";
    public static final String TAG = "LocationAlertFragment";
    public static final String LATITUDE = "latitude";
    public static final int PLACE_PICKER_REQUEST = 1;
    public static final String LONGITUDE = "longitude";
    public static final int CURRENT_LOCATION_RESULT_CODE = 2;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private OnCurrentLocationListener mListener;

    public LocationAlertFragment() {
    }

    public static LocationAlertFragment newInstance(double latitude, double longitude) {
        LocationAlertFragment fragment = new LocationAlertFragment();
        if (latitude != 0.0 && longitude != 0) {
            Bundle bundle = new Bundle();
            bundle.putDouble(LATITUDE, latitude);
            bundle.putDouble(LONGITUDE, longitude);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCurrentLocationListener) activity;
        }
        catch (ClassCastException e) {
            Log.e(TAG, activity.getClass().getSimpleName() + " must implement " +
                    "OnCurrentLocationListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        double latitude = 0.0;
        double longitude = 0.0;
        if (getArguments() != null) {
            latitude = getArguments().getDouble(LATITUDE);
            longitude = getArguments().getDouble(LONGITUDE);
        }

        final double finalLatitude = latitude;
        final double finalLongitude = longitude;
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: // Maps
                        // Uses the Places api to open a PlacePicker
                        // TODO: investigate using a normal Map instead in order to use Search
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        Context context = getActivity().getApplicationContext();
                        try {
                            getActivity().startActivityForResult(builder.build(context),
                                    PLACE_PICKER_REQUEST);
                        }
                        catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        }
                        catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                        break;
                    case DialogInterface.BUTTON_NEUTRAL: // Current location
                        mListener.useCurrentLocation();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                    default:
                        break;
                }
                dismiss();
            }
        };

        return new AlertDialog.Builder(getActivity()).setTitle(TITLE)
                .setMessage(MESSAGE)
                .setPositiveButton(R.string.dialog_location_maps, listener)
                .setNeutralButton(R.string.dialog_location_current, listener)
                .setNegativeButton(android.R.string.cancel, listener).create();

    }

    /**
     * User has selected "Current" location and we need the Activity to act on it
     */
    public interface OnCurrentLocationListener {
        void useCurrentLocation();
    }
}
