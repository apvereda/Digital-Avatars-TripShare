package com.apvereda.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.apvereda.db.Proposal;
import com.apvereda.db.Trip;
import com.apvereda.digitalavatars.R;
import com.apvereda.tripshare.TripShareApp;
import com.apvereda.uDataTypes.SBoolean;

import org.wso2.siddhi.android.platform.SiddhiAppService;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TripShareReceiver extends BroadcastReceiver {
    private TripShareApp app;

    public TripShareReceiver() {
        super();
        app = TripShareApp.getApp(null);
    }

    @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * Recibo una petición de un viaje, debo comprobar si tengo un viaje parecido en mi bd
             * y si es asi enviar un tripProposal de respuesta
             *
             * la peticion de viaje tiene como atributos:
             * sender, originLatitude, originLongitude, destinationLatitude, destinationLongitude, date, time, maxDistance, waitingTime
             *
             * la propuesta tiene como argumentos:
             * sender, originLatitude, originLongitude, destinationLatitude, destinationLongitude, date, time
             */
            if (intent.getAction().equals(TripShareApp.EV_TRIPQUERY)) {
                String text="Viajas? : "
                        + intent.getStringExtra(TripShareApp.ORIGIN_LAT) + " => "
                        + intent.getStringExtra(TripShareApp.DATE);
                Log.i("Digital-Avatars", text);


                Trip t = new Trip(Double.parseDouble(intent.getStringExtra(TripShareApp.ORIGIN_LAT)), Double.parseDouble(intent.getStringExtra(TripShareApp.ORIGIN_LON)),
                        Double.parseDouble(intent.getStringExtra(TripShareApp.DESTINATION_LAT)), Double.parseDouble(intent.getStringExtra(TripShareApp.DESTINATION_LON)),
                        intent.getStringExtra(TripShareApp.DATE), intent.getStringExtra(TripShareApp.TIME));
                Trip similar = app.getSimilarTrip(t, Double.parseDouble(intent.getStringExtra(TripShareApp.MAX_DISTANCE)), Double.parseDouble(intent.getStringExtra(TripShareApp.MAX_WAIT)));
                if(similar != null){
                    Intent i = new Intent(TripShareApp.EV_NEWPROPOSAL);
                    i.putExtra(TripShareApp.ORIGIN_LAT, similar.getOriginLat());
                    i.putExtra(TripShareApp.ORIGIN_LON, similar.getOriginLon());
                    i.putExtra(TripShareApp.DESTINATION_LAT, similar.getDestinationLat());
                    i.putExtra(TripShareApp.DESTINATION_LON, similar.getDestinationLon());
                    i.putExtra(TripShareApp.DATE, similar.getDate());
                    i.putExtra(TripShareApp.TIME, similar.getTime());
                    i.putExtra(TripShareApp.RECIPIENT, intent.getStringExtra(TripShareApp.ONESIGNAL));
                    SiddhiAppService.getServiceInstance().sendBroadcast(i);
                    Log.i("DigitalAvatars", "MANDO PROPUESTA");
                } else{
                    Log.i("DigitalAvatars", "No hay ninguna coincidencia de viaje similar");
                }
            }
            /**
             * Recibo una propuesta de viaje de alguno de mis contactos, debo comprobar mi trust en
             * ese contacto y si no tengo pedirsela a un referal.
             *
             * Si no tengo referencia la propuesta se almacena en una lista de posibles y pido referal
             *
             * Si me fio, almaceno la propuesta en la lista de definitivas, debe tener un campo de accepted???
             */
            if (intent.getAction().equals(TripShareApp.EV_TRIPPROPOSAL)) {
                /*OneSignalService.postMessage(text);
                String text="Llevas "+intent.getLongExtra("numSteps", 0)+ " pasos, te faltan "+intent.getLongExtra("faltan",0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Siddhi")
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Animo Siddhi")
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(101, builder.build());
                Toast toast1 = Toast.makeText(context,text, Toast.LENGTH_LONG);
                toast1.show();*/

                Log.i("DigitalAvatars", "RECIBO PROPUESTA de " + intent.getStringExtra(TripShareApp.SENDER));
                // Incluir opcion de que no tengo mas que referal, pero no functional
                Proposal p = new Proposal(Double.parseDouble(intent.getStringExtra(TripShareApp.ORIGIN_LAT)), Double.parseDouble(intent.getStringExtra(TripShareApp.ORIGIN_LON)),
                        Double.parseDouble(intent.getStringExtra(TripShareApp.DESTINATION_LAT)), Double.parseDouble(intent.getStringExtra(TripShareApp.DESTINATION_LON)),
                        intent.getStringExtra(TripShareApp.DATE), intent.getStringExtra(TripShareApp.TIME), intent.getStringExtra(TripShareApp.SENDER));
                if(app.checkSenderTrust(intent.getStringExtra(TripShareApp.SENDER))){
                    app.considerProposal(p);
                } else{
                    app.rejectProposal(p);
                }
            }
            /**
             * Recibo una peticion para dar mi opinion sobre alguien. Lo busco en mis contactos y
             * si tengo opinion sobre el se la mando de vuelta
             */
            else if (intent.getAction().equals("ReferalTrustRequest")) {

            }
            /**
             * Recibo un trust que habia preguntado. Si es bueno, actualizo las listas para mover su
             * propuesta de las posibles a definitivas
             */
            else if (intent.getAction().equals("ReferalTrustResponse")) {

            }
        }
}
