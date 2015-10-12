package ca.rhythmtech.riffle.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by mdoucette on 10/10/15.
 */
public class TripImagesThumbsAdapter extends ArrayAdapter<ImageView>{
    public TripImagesThumbsAdapter(Context context, int resource, List<ImageView> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
