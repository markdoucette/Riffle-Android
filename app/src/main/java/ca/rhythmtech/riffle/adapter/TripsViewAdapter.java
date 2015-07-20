package ca.rhythmtech.riffle.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ca.rhythmtech.riffle.R;
import ca.rhythmtech.riffle.activity.AddTripActivity;
import ca.rhythmtech.riffle.model.Trip;
import com.parse.ParseQueryAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Custom adapter to get trips from Parse.com data store and populate ListView of Trips
 * @author Mark Doucette
 */
public class TripsViewAdapter extends ParseQueryAdapter<Trip> {
    private SimpleDateFormat dateFormat;

    public TripsViewAdapter(Context context, QueryFactory<Trip> queryFactory) {
        super(context, queryFactory);
        dateFormat = new SimpleDateFormat(AddTripActivity.DATE_FORMAT, Locale.US);
    }

    @Override
    public View getItemView(Trip trip, View v, ViewGroup parent) {

        if (null == v) {
            v = View.inflate(getContext(), R.layout.layout_trip, null);
        }

        super.getItemView(trip, v, parent);

        TextView tvLabel = (TextView) v.findViewById(R.id.act_display_tv_label);
        TextView tvDate = (TextView) v.findViewById(R.id.act_displaytrips_tv_date);

        tvLabel.setText(trip.getName());

        Calendar date = trip.getDate();
        if (date != null && dateFormat != null) {
            tvDate.setText(dateFormat.format(date.getTime()));
        }
        else {
            tvDate.setText("");
        }

        return v;
    }
}
