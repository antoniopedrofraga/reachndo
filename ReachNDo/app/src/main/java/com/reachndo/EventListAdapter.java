package com.reachndo;

/**
 * Created by Pedro on 11/02/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.service.Event;

import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final ArrayList<Event> eventsArrayList;

    public EventListAdapter(Context context, ArrayList<Event> eventsArrayList) {

        super(context, R.layout.row, eventsArrayList);
        this.context = context;
        this.eventsArrayList = eventsArrayList;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 1. Create inflater

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = null;

        if (eventsArrayList.get(position).isGroupHeader()){
            rowView = inflater.inflate(R.layout.group_header_item, parent, false);
            TextView titleView = (TextView) rowView.findViewById(R.id.header);
            titleView.setText(eventsArrayList.get(position).getName());
            rowView.setEnabled(false);
            rowView.setOnClickListener(null);
            rowView.setMinimumHeight(20);
        }else {
            rowView = inflater.inflate(R.layout.row, parent, false);


            ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
            TextView titleView = (TextView) rowView.findViewById(R.id.string);
            TextView counterView = (TextView) rowView.findViewById(R.id.substring);


            imgView.setImageResource(eventsArrayList.get(position).getIcon());
            titleView.setText(eventsArrayList.get(position).getName());
            counterView.setText(eventsArrayList.get(position).getDescription());


        }

        return rowView;
    }
}
