package com.apvereda.digitalavatars.ui.tripshare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apvereda.db.Contact;
import com.apvereda.db.Trip;
import com.apvereda.digitalavatars.R;
import com.apvereda.digitalavatars.ui.friendslist.AdapterForListView;
import com.apvereda.utils.DigitalAvatar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


public class MyTripsFragment extends AppCompatActivity {
    DigitalAvatar da;
    TripsAdapter adapter;
    ListView list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trip_list);
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
        List<Trip> trips = Trip.getAllTrips();
        adapter = new TripsAdapter(this, trips);
        list = (ListView) findViewById(R.id.listTrips);
        list.setAdapter(adapter);

        /*NavController navController1 = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController1.getGraph()).build();
        NavigationUI.setupWithNavController(layout, toolbar, navController1, appBarConfiguration);*/

        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    }

    public void updateTrips(){
        List<Trip> trips = Trip.getAllTrips();
        Log.i("Digital Avatar", "Estos son los viajes que pongo en la lista:"+trips);
        adapter.setData(trips);
        list.setAdapter(adapter);
    }
}
