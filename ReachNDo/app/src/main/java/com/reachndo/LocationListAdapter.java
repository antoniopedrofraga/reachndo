package com.reachndo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.service.Event;
import com.service.Location;

import java.util.ArrayList;

/**
 * Created by Pedro on 12/09/2015.
 */
public class LocationListAdapter extends ArrayAdapter<Location> {

    private final Context context;
    private ArrayList<Location> locationArrayList;

    public LocationListAdapter(Context context, ArrayList<Location> locationArrayList) {

        super(context, R.layout.location_row, locationArrayList);
        this.context = context;
        this.locationArrayList = locationArrayList;
    }

    public ArrayList<Location> getLocationArrayList(){
        return locationArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 1. Create inflater

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = null;

            rowView = inflater.inflate(R.layout.location_row, parent, false);



            ImageButton imgView = (ImageButton) rowView.findViewById(R.id.closeButton);
            TextView titleView = (TextView) rowView.findViewById(R.id.string);


            imgView.setImageResource(R.drawable.trash);

            if(locationArrayList.get(position).isChecked()){
                rowView.setBackgroundColor(getContext().getResources().getColor(R.color.MaterialPurple));
                imgView.setBackgroundColor(getContext().getResources().getColor(R.color.MaterialPurple));
            }
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRemoveWarning(position);
                }
            });
            titleView.setText(locationArrayList.get(position).getName());

        return rowView;
    }

    public void setLocations(ArrayList<Location> locations){
        this.locationArrayList = locations;
    }

    private void showRemoveWarning(final int position) {
        final MaterialDialog alert = new MaterialDialog.Builder(getContext())
                .title(R.string.remove_warning_title)
                .content(R.string.remove_warning_content)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .show();
        View positive = alert.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainMenu menu = MainMenu.getInstance();
                menu.removeLocation(position);
                alert.dismiss();
            }
        });

    }
}