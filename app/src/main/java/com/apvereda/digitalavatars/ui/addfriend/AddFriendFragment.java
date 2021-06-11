package com.apvereda.digitalavatars.ui.addfriend;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.apvereda.db.Contact;
import com.apvereda.digitalavatars.R;
import com.apvereda.digitalavatars.ui.home.HomeFragment;
import com.apvereda.digitalavatars.ui.profile.ProfileViewModel;
import com.apvereda.utils.DigitalAvatar;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AddFriendFragment extends Fragment {

    private AddFriendViewModel addFriendViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addFriendViewModel =
                ViewModelProviders.of(this).get(AddFriendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_friend, container, false);

        CollapsingToolbarLayout layout = root.findViewById(R.id.friend_toolbar_layout);
        Toolbar toolbar = root.findViewById(R.id.friend_toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Digital Avatars", "Intentando abrir el cajon");
                DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        /*NavController navController1 = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController1.getGraph()).build();
        NavigationUI.setupWithNavController(layout, toolbar, navController1, appBarConfiguration);*/

        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DigitalAvatar da = DigitalAvatar.getDA(getContext());
                //MutableDictionary dictionary = new MutableDictionary();
                TextView name = root.findViewById(R.id.friendname);
                //dictionary.setString("Name", name.getText().toString());
                TextView phone = root.findViewById(R.id.friendmobile);
                //dictionary.setString("Phone", phone.getText().toString());
                TextView email = root.findViewById(R.id.friendemail);
                //dictionary.setString("Email", email.getText().toString());
                TextView onesignal = root.findViewById(R.id.friendonesignal);
                //dictionary.setString("IDOneSignal", onesignal.getText().toString());
                //MutableDocument doc = da.getDoc("Relations");
                //doc.setDictionary(email.getText().toString(),dictionary);
                //da.saveDoc(doc);
                Contact c = new Contact(email.getText().toString(), name.getText().toString(),
                        "", phone.getText().toString(), onesignal.getText().toString(), "uid");
                //Log.i("Digital Avatar", "Tengo un nuevo amigo:"+c.getEmail());
                new Thread(new MyRunnable(c)).start();
                Toast toast1 = Toast.makeText(getContext(),"Friend Added", Toast.LENGTH_LONG);
                toast1.show();
                name.setText("");
                phone.setText("");
                email.setText("");
                onesignal.setText("");
                //Log.i("Digital Avatar", "Estos son mis amigos:"+doc.getKeys());
            }
        });
        return root;
    }

    private String postHttpRequest(String request, String email){
        String result ="";
        try {
            String urlParameters  = "email="+email;
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;
            URL url            = new URL( request );
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );
            try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                wr.write( postData );
                wr.close();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result = br.readLine();
            br.close();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private class MyRunnable implements Runnable {
        Contact c;

        public MyRunnable(Contact c){ this.c = c; }
        @Override
        public void run() {
            String uid = postHttpRequest("https://digitalavatars.appspot.com/email2uid", c.getEmail());
            c.setUID(uid);
            Contact.createContact(c);
            Log.i("Digital Avatar", "Tengo un nuevo amigo:"+c.getEmail());
        }
    }
}
