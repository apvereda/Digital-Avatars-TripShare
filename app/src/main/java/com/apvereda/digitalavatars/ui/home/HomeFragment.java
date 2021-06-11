package com.apvereda.digitalavatars.ui.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.apvereda.digitalavatars.R;
import com.apvereda.digitalavatars.ui.tripshare.TripShareActivity;
import com.apvereda.receiver.TripShareReceiver;
import com.apvereda.utils.SiddhiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.wso2.siddhi.android.platform.SiddhiAppService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private String appName="";
    View root;
    List<String> apps;
    List<String> appnames;
    TripShareReceiver dur;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        setRetainInstance(true);
        if(savedInstanceState !=null) {
            //textView.setText(savedInstanceState.getString("text"));
            if(savedInstanceState.getInt("btn_play")==View.VISIBLE) {
                root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
                root.findViewById(R.id.fabstop).setVisibility(View.GONE);
            } else{
                root.findViewById(R.id.fabplay).setVisibility(View.GONE);
                root.findViewById(R.id.fabstop).setVisibility(View.VISIBLE);
            }
        }
        apps = new ArrayList<String>();
        appnames = new ArrayList<String>();
        //SiddhiService.getServiceConnection(getActivity().getApplicationContext());
        Button btntripshare = root.findViewById(R.id.tripsharebtn);
        btntripshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TripShareActivity.class);
                startActivity(i);
            }
        });
        FloatingActionButton fabplay = root.findViewById(R.id.fabplay);
        fabplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startApp();
                    root.findViewById(R.id.fabplay).setVisibility(View.GONE);
                    root.findViewById(R.id.fabstop).setVisibility(View.VISIBLE);
                    Snackbar.make(view, "Siddhi app running", Snackbar.LENGTH_LONG).show();
                    //textView.setText("Siddhi app running");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        FloatingActionButton fabstop = root.findViewById(R.id.fabstop);
        fabstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    stopApp();
                    root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
                    root.findViewById(R.id.fabstop).setVisibility(View.GONE);
                    Snackbar.make(view, "Siddhi app stopped", Snackbar.LENGTH_LONG).show();
                    //textView.setText("Siddhi app stopped");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
        root.findViewById(R.id.fabstop).setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState !=null) {
            //TextView textView = root.findViewById(R.id.text_home);
            //textView.setText(savedInstanceState.getString("text"));
            if(savedInstanceState.getInt("btn_play")==View.VISIBLE) {
                root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
                root.findViewById(R.id.fabstop).setVisibility(View.GONE);
            } else{
                root.findViewById(R.id.fabplay).setVisibility(View.GONE);
                root.findViewById(R.id.fabstop).setVisibility(View.VISIBLE);
            }
        }

    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TextView textView = root.findViewById(R.id.text_home);
        outState.putInt("btn_play", root.findViewById(R.id.fabplay).getVisibility());
        //outState.putString("text", textView.getText().toString());

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    private void startApp() throws RemoteException {
        String app1 = "@app:name('SiddhiTripShare')" +
                "@source(type='android-message', appid ='TripShareApp'," +
                "@map(type='keyvalue',fail.on.missing.attribute='false'," +
                "@attributes(sender='sender', body='message', origin='origin', destination='destination', date='date', time='time')))" +
                "define stream inMessageStream(sender String, body String, origin String, destination String, date String, time String);"+

                "@source(type='android-broadcast', identifier='TripShare_NewTrip'," +
                "@map(type='keyvalue',fail.on.missing.attribute='false'))" +
                "define stream newTrip(origin String, destination String, date String, time String);" +

                "@sink(type='android-message' , appid='TripShareApp', recipients='Relations'," +
                "@map(type='keyvalue', @payload(message = 'Viajas desde {{origin}} a {{destination}} el dia {{date}} a las {{time}}?', " +
                "origin = '{{origin}}', destination = '{{destination}}', date = '{{date}}', time = '{{time}}')))"+
                "define stream sendMessage(origin String, destination String, date String, time String); " +

                "@sink(type='android-broadcast', identifier='TripQuery', " +
                "@map(type='keyvalue'))" +
                "define stream tripQuery(origin String, destination String, date String, time String); " +

                "@sink(type='android-notification', title='TripShareMessage',multiple.notifications = 'true', " +
                "@map(type='keyvalue'))" +
                "define stream notifyMessage(sender String, body String); " +

                //"from stepsInStream#window.timeBatch(20 sec) select sum(steps) as numSteps insert into leftStepsInStream;"+
                //"from leftStepsInStream[numSteps<=2000] select numSteps, 2000-numSteps as faltan insert into sendMessage;"+
                "from newTrip select * insert into sendMessage;"+
                "from inMessageStream select origin, destination, date, time insert into tripQuery;"+
                "from inMessageStream select sender, body insert into notifyMessage;";
        apps.add(app1);
        dur = new TripShareReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("TripQuery");
        SiddhiAppService.getServiceInstance().registerReceiver(dur, intentFilter);
        for(String app: apps) {
            appnames.add(SiddhiService.getServiceConnection(getActivity().getApplicationContext()).startSiddhiApp(app));
        }
    }

    private void stopApp() throws RemoteException{
        for( String app : appnames) {
            SiddhiService.getServiceConnection(getContext()).stopSiddhiApp(app);
        }
        SiddhiAppService.getServiceInstance().unregisterReceiver(dur);
        appnames = new ArrayList<String>();
    }

    public void removeApps() {
        apps = new ArrayList<String>();
    }

    public void readFile(){
        apps = new ArrayList<String>();
        File fileDirectory = new File(getContext().getExternalFilesDir(null)+"/SiddhiApps");
        if(!fileDirectory.exists()){
            fileDirectory.mkdir();
        }
        File[] dirFiles = fileDirectory.listFiles();
        Snackbar.make(this.root, "Leyendo Siddhi Apps", Snackbar.LENGTH_LONG).show();
        String app = "";
        String line;
        for (File f : dirFiles) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                while ((line = br.readLine()) != null){
                    app += line;
                }
                apps.add(app);
                System.out.println(app);
                app = "";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("TripQuery")) {
                String text="Me ha llegado una peticion de viaje: "
                        + intent.getStringExtra("origin") + " => "
                        + intent.getStringExtra("destination");
                Log.i("Digital-Avatars", text);
                //tratar el evento y luego devolver la respuesta al cep como un broadcast intent
                //SiddhiAppService.getServiceInstance().sendBroadcast(intent);
            }

        }
    }*/
}
