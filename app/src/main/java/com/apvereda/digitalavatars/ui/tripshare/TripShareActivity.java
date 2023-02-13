package com.apvereda.digitalavatars.ui.tripshare;

import android.content.Intent;
import android.os.Bundle;

import com.apvereda.db.Avatar;
import com.apvereda.db.Trip;
import com.apvereda.digitalavatars.R;
import com.apvereda.tripshare.TripShareApp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import org.wso2.siddhi.android.platform.SiddhiAppService;

public class TripShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_share);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TripShareApp.getApp(null);
        FloatingActionButton fab = findViewById(R.id.fabtrip);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView originlat = findViewById(R.id.originlattxt);
                TextView destinationlat = findViewById(R.id.destinationlattxt);
                TextView originlon = findViewById(R.id.originlontxt);
                TextView destinationlon = findViewById(R.id.destinationlontxt);
                TextView date = findViewById(R.id.datetxt);
                TextView time = findViewById(R.id.timetxt);
                TextView maxdistance = findViewById(R.id.distancetxt);
                TextView wait = findViewById(R.id.waittxt);

                Trip t = new Trip(Double.parseDouble(originlat.getText().toString()), Double.parseDouble(originlon.getText().toString()),
                        Double.parseDouble(destinationlat.getText().toString()), Double.parseDouble(destinationlon.getText().toString()),
                        date.getText().toString(), time.getText().toString());

                Intent i = new Intent(TripShareApp.EV_NEWTRIP);
                i.putExtra(TripShareApp.ORIGIN_LAT, t.getOriginLat());
                i.putExtra(TripShareApp.ORIGIN_LON, t.getOriginLon());
                i.putExtra(TripShareApp.DESTINATION_LAT, t.getDestinationLat());
                i.putExtra(TripShareApp.DESTINATION_LON, t.getDestinationLon());
                i.putExtra(TripShareApp.DATE, date.getText().toString());
                i.putExtra(TripShareApp.TIME, time.getText().toString());
                i.putExtra(TripShareApp.MAX_WAIT, Double.parseDouble(wait.getText().toString()));
                i.putExtra(TripShareApp.MAX_DISTANCE, Double.parseDouble(maxdistance.getText().toString()));
                i.putExtra(TripShareApp.ONESIGNAL, Avatar.getAvatar().getOneSignalID());
                SiddhiAppService.getServiceInstance().sendBroadcast(i);

                TripShareApp.getApp(t).setTripRequest(t);
                Snackbar.make(view, "Procesando viaje y avisando a sus contactos...", Snackbar.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fabadd = findViewById(R.id.fabadd);
        fabadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView originlat = findViewById(R.id.originlattxt);
                TextView destinationlat = findViewById(R.id.destinationlattxt);
                TextView originlon = findViewById(R.id.originlontxt);
                TextView destinationlon = findViewById(R.id.destinationlontxt);
                TextView date = findViewById(R.id.datetxt);
                TextView time = findViewById(R.id.timetxt);

                Trip t = new Trip(Double.parseDouble(originlat.getText().toString()), Double.parseDouble(originlon.getText().toString()),
                        Double.parseDouble(destinationlat.getText().toString()), Double.parseDouble(destinationlon.getText().toString()),
                        date.getText().toString(), time.getText().toString());
                Trip.createTrip(t);
                Snackbar.make(view, "Añadido a Tus Viajes", Snackbar.LENGTH_LONG).show();
            }
        });
        FloatingActionButton fablist = findViewById(R.id.fablist);
        fablist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), MyTripsFragment.class);
                startActivity(i);
            }
        });

        FloatingActionButton fabresult = findViewById(R.id.fabresult);
        fabresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ResultTripsFragment.class);
                startActivity(i);
            }
        });
    }
}