package com.apvereda.digitalavatars.ui.tripshare;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apvereda.db.Proposal;
import com.apvereda.db.Trip;
import com.apvereda.digitalavatars.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ResultsAdapter extends BaseAdapter {
    Activity context;
    List<Proposal> data;

    public ResultsAdapter(Activity context, List<Proposal> data) {
        super();
        this.context = context;
        this.data = data;
    }

    public void setData(List<Proposal> data) {
        this.data = data;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.result_list_item, null);
        }

        //List<String> keys = da.getDoc("Relations").getKeys();
        TextView lbltrust = (TextView) convertView.findViewById(R.id.lblresulttrust);
        lbltrust.setText(data.get(position).getSender()+" @ "+data.get(position).getDate() + " " + data.get(position).getTime());
        TextView lbltrip1 = (TextView) convertView.findViewById(R.id.lblresulttrip1);
        TextView lbltrip2 = (TextView) convertView.findViewById(R.id.lblresulttrip2);
        String text = data.get(position).getOriginLat() + ", " + data.get(position).getOriginLon() +
                "--->" +
                data.get(position).getDestinationLat() + ", " + data.get(position).getDestinationLon();
        lbltrip1.setText(data.get(position).getOriginLat() + ", " + data.get(position).getOriginLon());
        lbltrip2.setText( data.get(position).getDestinationLat() + ", " + data.get(position).getDestinationLon());
        //Log.i("Digital Avatar", "Pinto viaje el: "+data.get(position).getDate() + " : " + data.get(position).getUID());
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

