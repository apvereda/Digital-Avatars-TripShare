package com.apvereda.tripshare;

import android.util.Log;

import com.apvereda.db.Contact;
import com.apvereda.db.Proposal;
import com.apvereda.db.Trip;
import com.apvereda.db.TrustOpinion;
import com.apvereda.uDataTypes.SBoolean;

import java.util.ArrayList;
import java.util.List;

public class TripShareApp {
    public static final String ORIGIN_LAT ="originLatitude";
    public static final String ORIGIN_LON ="originLongitude";
    public static final String DESTINATION_LAT ="destinationLatitude";
    public static final String DESTINATION_LON ="destinationLongitude";
    public static final String DATE ="date";
    public static final String TIME ="time";
    public static final String SENDER ="sender";
    public static final String ONESIGNAL ="onesignalid";
    public static final String RECIPIENT ="recipient";
    public static final String MAX_DISTANCE ="maxDistance";
    public static final String MAX_WAIT ="waitingTime";
    public static final String EV_NEWTRIP ="TripShare_NewTrip"; //User enters new trip
    public static final String EV_TRIPQUERY ="TripShare_TripQuery"; //Contact receives trip query
    public static final String EV_NEWPROPOSAL ="TripShare_NewProposal"; //Contact responds with a proposal
    public static final String EV_TRIPPROPOSAL ="TripShare_TripProposal"; //User receives proposal from a contact



    private List<Proposal> trustedProposals = new ArrayList<>();
    private List<Proposal> untrustedProposals = new ArrayList<>();
    private Trip tripRequest;
    private static TripShareApp app;

    public static TripShareApp getApp(Trip t){
        if (app == null){
            app = new TripShareApp(t);
        }
        return app;
    }

    private TripShareApp(Trip t){
        trustedProposals = new ArrayList<>();
        untrustedProposals = new ArrayList<>();
        tripRequest = t;
    }

    public List<Proposal> getTrustedProposals() {
        return trustedProposals;
    }

    public void setTrustedProposals(List<Proposal> trustedProposals) {
        this.trustedProposals = trustedProposals;
    }

    public List<Proposal> getUntrustedProposals() {
        return untrustedProposals;
    }

    public void setUntrustedProposals(List<Proposal> untrustedProposals) {
        this.untrustedProposals = untrustedProposals;
    }

    public void setTripRequest(Trip tripRequest) {
        this.tripRequest = tripRequest;
        trustedProposals = new ArrayList<>();
        untrustedProposals = new ArrayList<>();
    }

    public Trip getSimilarTrip(Trip t, double distance, double wait) {
        List<Trip> candidates = Trip.getTripbyDate(t.getDate());
        for (int i=0; i< candidates.size(); i++){
            Trip trip = candidates.get(i);
            double distanceOrigin = distance(t.getOriginLat(), trip.getOriginLat(), t.getOriginLon(), trip.getOriginLon(),0,0);
            double distanceDestination = distance(t.getDestinationLat(), trip.getDestinationLat(), t.getDestinationLon(), trip.getDestinationLon(),0,0);
            int hour1 = Integer.parseInt(t.getTime().split(":")[0]);
            int minutes1 = Integer.parseInt(t.getTime().split(":")[1]);
            int hour2 = Integer.parseInt(trip.getTime().split(":")[0]);
            int minutes2 = Integer.parseInt(trip.getTime().split(":")[1]);
            double timediff = (Math.abs(hour1-hour2) * 60) + (Math.abs(minutes1-minutes2));
            Log.i("DigitalAvatars", "La diferencia de tiempo es " + timediff);
            Log.i("DigitalAvatars", "La distancia es " + distanceDestination);
            if(distanceOrigin > distance || distanceDestination > distance || timediff > wait){
                candidates.remove(i);
            }
        }
        return (candidates.isEmpty()) ? null : candidates.get(0);
    }

    public boolean checkSenderTrust(String sender) {
        List<TrustOpinion> direct = TrustOpinion.getDirectOpinionForTrustee(Contact.getContactByEmail(sender).getUID());
        if(!direct.isEmpty()){
            Log.i("DigitalAvatars", "Tengo una opinion directa sobre "+sender+" con valor "+direct.get(0).getTrust()+" y proyección "+direct.get(0).getTrust().projection());
            return (direct.get(0).getTrust().projection() >= 0.5) ? true : false;
        } else {
            List<TrustOpinion> contactsFunctionalTrust = TrustOpinion.getContactsOpinionForTrustee(Contact.getContactByEmail(sender).getUID());
            List<SBoolean> discounts = new ArrayList<>();
            for(TrustOpinion t : contactsFunctionalTrust){
                //Log.i("DigitalAvatars", "Tengo opiniones indirectas sobre "+sender);
                List<TrustOpinion> aux = TrustOpinion.getReferralOpinionforTrustee(t.getTruster());
                if(!aux.isEmpty()) {
                    //Log.i("DigitalAvatars", "Tengo referral sobre "+t.getTruster());
                    SBoolean s = t.getTrust().discount(aux.get(0).getTrust());
                    discounts.add(s);
                }
            }
            if(!discounts.isEmpty()){
                SBoolean s = SBoolean.cumulativeBeliefFusion(discounts);
                Log.i("DigitalAvatars", "Tengo opiniones indirectas sobre "+sender+" con valor "+s+" y proyección "+s.projection());
                return (s.projection() >= 0.5) ? true : false;
            } else {
                Log.i("DigitalAvatars", "No hay opiniones sobre "+sender);
                return false;
            }
        }
    }

    public void considerProposal(Proposal p) {
        trustedProposals.add(p);
        Log.i("Digital Avatars", " Recibida una propuesta válida de " + p.getSender());
    }

    public void rejectProposal(Proposal p) {
        untrustedProposals.add(p);
        Log.i("Digital Avatars", " Recibida una propuesta no confiable de " + p.getSender());
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    private double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
