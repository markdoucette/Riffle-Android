package ca.rhythmtech.riffle.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import ca.rhythmtech.riffle.model.Trip;
import com.parse.ParseObject;

/**
 * Fragment that displays an Alert Dialog to confirm deletion of Trip
 * @author Mark Doucette
 */
public class DeleteTripAlertFragment extends DialogFragment {
    public static final String WARNING = "Warning!";
    public static final String MESSAGE = "Are you sure you want to delete this trip?";

    private String tripId;

    public DeleteTripAlertFragment(){}

    /*
    In order to give our fragment some state (the Trip Id to delete), we need to use a static
    method to create an instance and set the tripId as a param.
     */
    public static DeleteTripAlertFragment newInstance(String tripId) {
        DeleteTripAlertFragment fragment = new DeleteTripAlertFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tripId", tripId);//to use to delete the Trip
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Trip trip = null;
        if (getArguments() != null) {
            // create an empty object of the id to delete later
            tripId = getArguments().getString("tripId");
            trip = ParseObject.createWithoutData(Trip.class, tripId);
        }

        final Trip myTrip = trip; // must be done to use in the OnclickListener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (myTrip != null) {
                            myTrip.deleteEventually();
                            getActivity().finish();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    default:
                        break;
                }
                dismiss();
            }
        };

        return new AlertDialog.Builder(getActivity()).setTitle(WARNING)
                .setMessage(MESSAGE)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener).create();
    }
}
