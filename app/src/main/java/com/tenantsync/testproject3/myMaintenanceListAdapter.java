package com.tenantsync.testproject3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Dad on 9/27/2015.
 */
public class myMaintenanceListAdapter extends ArrayAdapter<MaintenaceRequest> {
    private final Context context;
    private final MaintenaceRequest[] values;

    public myMaintenanceListAdapter(Context context, MaintenaceRequest[] values) {
        super(context, R.layout.maintenance_list_display, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.maintenance_list_display, parent, false);
        TextView requestView = (TextView) rowView.findViewById(R.id.request);
        TextView responseView = (TextView) rowView.findViewById(R.id.response);
        requestView.setText(values[position].getRequest());
        responseView.setText(values[position].getResponse());

        return rowView;
    }
}

