package com.px.MyTimetable.Service;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class MyLocation {
   Timer timer;
   LocationManager lManager;
   Context context;
   static DistanceTime dTime;
   LocationResult lResult;
   boolean e_gps = false;
   boolean e_network = false;

   public boolean getLocation(Context context, LocationResult locationResult) {
      Log.d("MyLocation","started");
      this.context = context;
      // TODO Auto-generated method stub
      lResult = locationResult;
      if(lManager == null){
         lManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
      }
      
      try{
         e_gps = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
      }
      catch(Exception e){}
      try{
         e_network = lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
      }
      catch(Exception e){}
      
      if(!e_gps && !e_network){
         return false;
      }
      if(e_gps){
         lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
      } // TODO : else if??
      if(e_network){
         lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
      }
      timer = new Timer();
      timer.schedule(new GetLastLocation(),20000);
      return true;
   }
   
   LocationListener locationListenerGps = new LocationListener(){
      public void onLocationChanged(Location location){
         
         timer.cancel();
         lResult.gotLocation(location,context);
         lManager.removeUpdates(this);
         lManager.removeUpdates(locationListenerNetwork);
      }

      @Override
      public void onProviderDisabled(String arg0) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void onProviderEnabled(String arg0) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
         // TODO Auto-generated method stub
         
      }
   };
   
   LocationListener locationListenerNetwork = new LocationListener(){
      public void onLocationChanged(Location location){
         timer.cancel();
         lResult.gotLocation(location,context);
         lManager.removeUpdates(this);
         lManager.removeUpdates(locationListenerGps);
      }

      @Override
      public void onProviderDisabled(String provider) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void onProviderEnabled(String provider) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras) {
         // TODO Auto-generated method stub
         
      }
   };
   
   class GetLastLocation extends TimerTask{
      public void run(){
         lManager.removeUpdates(locationListenerGps);
         lManager.removeUpdates(locationListenerNetwork);
         
         Location n_location=null;
         Location g_location=null;
         
         if(e_gps){
            g_location = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
         }
         if(e_network){
            n_location = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
         }
         
         if(g_location!=null && n_location!=null){
            if(g_location.getTime() > n_location.getTime()){
               lResult.gotLocation(g_location,context);
            }else{
               lResult.gotLocation(n_location,context);
            }
            return;
         }
         
         if(g_location!=null){
            lResult.gotLocation(g_location,context);
            return;
         }
         if(n_location!=null){
            lResult.gotLocation(n_location, context);
            return;
         }
         
         lResult.gotLocation(null,context);
      }
   }
   
   public static abstract class LocationResult{
      public abstract void gotLocation(Location location,Context context);
      /*{
         double Latitude = location.getLatitude();
         double Longitude = location.getLongitude();
         Log.d("LocationResult","data received");
         dTime.getTime(Latitude,Longitude);
      }*/
   }

}
