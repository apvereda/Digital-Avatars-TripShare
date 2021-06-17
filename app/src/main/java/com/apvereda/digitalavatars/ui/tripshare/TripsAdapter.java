package com.apvereda.digitalavatars.ui.tripshare;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apvereda.db.Contact;
import com.apvereda.db.Trip;
import com.apvereda.digitalavatars.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class TripsAdapter extends BaseAdapter {
    Activity context;
    List<Trip> data;

    public TripsAdapter(Activity context, List<Trip> data) {
        super();
        this.context = context;
        this.data = data;
    }

    public void setData(List<Trip> data) {
        this.data = data;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.trip_list_item, null);
        }

        //List<String> keys = da.getDoc("Relations").getKeys();
        TextView lbldate = (TextView) convertView.findViewById(R.id.lbldate);
        lbldate.setText(data.get(position).getDate() + " " + data.get(position).getTime());
        TextView lbltrip1 = (TextView) convertView.findViewById(R.id.lbltrip1);
        TextView lbltrip2 = (TextView) convertView.findViewById(R.id.lbltrip2);
        String text = data.get(position).getOriginLat() + ", " + data.get(position).getOriginLon() +
                "--->" +
                data.get(position).getDestinationLat() + ", " + data.get(position).getDestinationLon();
        lbltrip1.setText(data.get(position).getOriginLat() + ", " + data.get(position).getOriginLon());
        lbltrip2.setText( data.get(position).getDestinationLat() + ", " + data.get(position).getDestinationLon());
        //Log.i("Digital Avatar", "Pinto viaje el: "+data.get(position).getDate() + " : " + data.get(position).getUID());
        ImageButton btndelete = convertView.findViewById(R.id.btndelete);
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trip.deleteTrip(data.get(position).getUID());
                data.remove(position);
                notifyDataSetChanged();
            }
        });
        /*List<RoutinePlace> places = db.getRoutinePlaces(data.get(position).getId());
        TextView lblAddress = (TextView) convertView.findViewById(R.id.lbladdress);
        lblAddress.setText(places.get(1).getPlace().getDescription());
        //lblAddress.setText(data.get(position).getLatitude()+"");

        TextView lblHour = (TextView) convertView.findViewById(R.id.lblhour);
        lblHour.setText(getDateTime(data.get(position).getStart()));
        //lblHour.setText(getDateTime(data.get(position).getDate()));
        */
        return (convertView);
    }

    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}

