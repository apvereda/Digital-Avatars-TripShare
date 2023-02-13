package com.apvereda.digitalavatars.ui.tripshare;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.apvereda.db.Proposal;
import com.apvereda.db.Trip;
import com.apvereda.digitalavatars.R;
import com.apvereda.tripshare.TripShareApp;
import com.apvereda.utils.DigitalAvatar;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ResultTripsFragment extends AppCompatActivity {
    DigitalAvatar da;
    ResultsAdapter adapter;
    ListView list;
    TripShareApp app;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_result_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*CollapsingToolbarLayout layout = root.findViewById(R.id.friend_list_toolbar_layout);
        Toolbar toolbar = root.findViewById(R.id.friend_list_toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Digital Avatars", "Intentando abrir el cajon");
                DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });*/
        da = DigitalAvatar.getDA();
        app = TripShareApp.getApp(null);
        List<Proposal> trips = app.getTrustedProposals();
        adapter = new ResultsAdapter(this, trips);
        list = (ListView) findViewById(R.id.listResults);
        list.setAdapter(adapter);

        /*NavController navController1 = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController1.getGraph()).build();
        NavigationUI.setupWithNavController(layout, toolbar, navController1, appBarConfiguration);*/

        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    }

    public void updateTrips(){
        List<Proposal> trips = app.getTrustedProposals();
        Log.i("Digital Avatar", "Estos son los viajes que pongo en la lista:"+trips);
        adapter.setData(trips);
        list.setAdapter(adapter);
    }
}
