package com.apvereda.db;

import android.util.Log;

import com.apvereda.utils.DigitalAvatar;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Trip {

    private double originLat;
    private double destinationLat;
    private double originLon;
    private double destinationLon;
    private String date;
    private String time;
    private String UID;

    public Trip(double originLat, double originLon, double destinationLat, double destinationLon, String date, String time) {
        this.originLat = originLat;
        this.destinationLat = destinationLat;
        this.originLon = originLon;
        this.destinationLon = destinationLon;
        this.date = date;
        this.time =  time;
    }
    public Trip(double originLat, double originLon, double destinationLat, double destinationLon, String date, String time, String id) {
        this.originLat = originLat;
        this.destinationLat = destinationLat;
        this.originLon = originLon;
        this.destinationLon = destinationLon;
        this.date = date;
        this.time =  time;
        this.UID = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getOriginLon() {
        return originLon;
    }

    public void setOriginLon(double originLon) {
        this.originLon = originLon;
    }

    public double getDestinationLon() {
        return destinationLon;
    }

    public void setDestinationLon(double destinationLon) {
        this.destinationLon = destinationLon;
    }

    public static List<Trip> getAllTrips(){
        ArrayList<Trip> resultList = new ArrayList<Trip>();
        Query query = QueryBuilder
                .select(SelectResult.expression(Meta.id),
                        SelectResult.property("OriginLatitude"),
                        SelectResult.property("OriginLongitude"),
                        SelectResult.property("DestinationLatitude"),
                        SelectResult.property("DestinationLongitude"),
                        SelectResult.property("Date"),
                        SelectResult.property("Time"))
                .from(DigitalAvatar.getDataSource())
                .where(Expression.property("type").equalTo(Expression.string("Trip")));
        try {
            ResultSet rs = query.execute();
            for (Result result : rs) {
                //Dictionary result = r.getDictionary(0);
                Trip c = new Trip(result.getDouble("OriginLatitude"), result.getDouble("OriginLongitude"),
                        result.getDouble("DestinationLatitude"), result.getDouble("DestinationLongitude"),
                        result.getString("Date"), result.getString("Time"), result.getString("id"));
                resultList.add(c);
            }
        } catch (CouchbaseLiteException e) {
            Log.e("CouchbaseError", e.getLocalizedMessage());
        }
        return resultList;
    }

    public static List<Trip> getTripbyDate(String date){
        ArrayList<Trip> resultList = new ArrayList<Trip>();
        Query query = QueryBuilder
                .select(SelectResult.expression(Meta.id),
                        SelectResult.property("OriginLatitude"),
                        SelectResult.property("OriginLongitude"),
                        SelectResult.property("DestinationLatitude"),
                        SelectResult.property("DestinationLongitude"),
                        SelectResult.property("Date"),
                        SelectResult.property("Time"))
                .from(DigitalAvatar.getDataSource())
                .where(Expression.property("type").equalTo(Expression.string("Trip"))
                        .and(Expression.property("Date").equalTo(Expression.string(date))));

        try {
            ResultSet rs = query.execute();
            for (Result result : rs) {
                //Dictionary result = r.getDictionary(0);
                Trip c = new Trip(result.getDouble("OriginLatitude"), result.getDouble("OriginLongitude"),
                        result.getDouble("DestinationLatitude"), result.getDouble("DestinationLongitude"),
                        result.getString("Date"), result.getString("Time"), result.getString("id"));
                resultList.add(c);
            }
        } catch (CouchbaseLiteException e) {
            Log.e("CouchbaseError", e.getLocalizedMessage());
        }
        return resultList;
    }

    public static void createTrip(Trip c){
        MutableDocument tripDoc = new MutableDocument();
        tripDoc.setString("type", "Trip");
        tripDoc.setDouble("OriginLatitude", c.getOriginLat());
        tripDoc.setDouble("DestinationLatitude", c.getDestinationLat());
        tripDoc.setDouble("OriginLongitude", c.getOriginLon());
        tripDoc.setDouble("DestinationLongitude", c.getDestinationLon());
        tripDoc.setString("Date", c.getDate());
        tripDoc.setString("Time", c.getTime());

        DigitalAvatar.getDA().saveDoc(tripDoc);
    }

    public static void deleteTrip(String uid){
        DigitalAvatar.getDA().deleteDoc(uid);
    }

    public static void updateTrip(Trip c){
        MutableDocument tripDoc = DigitalAvatar.getDA().getDoc(c.getUID()).toMutable();
        tripDoc.setDouble("OriginLatitude", c.getOriginLat());
        tripDoc.setDouble("DestinationLatitude", c.getDestinationLat());
        tripDoc.setDouble("OriginLongitude", c.getOriginLon());
        tripDoc.setDouble("DestinationLongitude", c.getDestinationLon());
        tripDoc.setString("Date", c.getDate());
        tripDoc.setString("Time", c.getTime());

        DigitalAvatar.getDA().saveDoc(tripDoc);
    }
}
