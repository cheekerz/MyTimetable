package com.px.MyTimetable.Service;

import java.util.Calendar;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.Service.MyLocation.LocationResult;

public class SmartNotification
{
   DistanceTime dTime;
   Context context;
   String[] postcode;
   String postcode_1,postcode_2;
   public void run(Context context, String currentLocation){
      Log.d("LocationUpdate","started");
      
      String pCode = ((TimetableAccess)context.getApplicationContext()).getTimetable().getNextLecture(Calendar.getInstance()).getPlace().getPostcode();
      
      // If no postcode given use university postcode
      // TODO : should check a postcode is available before ever getting to this point
      if(pCode == null){
         postcode_1 = "BS8";
         postcode_2 = "1TH";
      }else{
         postcode = pCode.split("\\s+");
         if(postcode[0]==""){
            postcode_1 = postcode[1];
            postcode_2 = postcode[2];
         }else{
            postcode_1 = postcode[0];
            postcode_2 = postcode[1];
         }
      }
      Log.d("Postcode",postcode_1 + "," + postcode_2);
      
      if (currentLocation == null)
      {
         LocationResult locationResult = new LocationResult(){
            @Override
            public void gotLocation(Location location,Context context){
                //Got the location!
              Log.d("Main","gotLocation");
              double Latitude = location.getLatitude();
              double Longitude = location.getLongitude();
              Log.d("Location",Double.toString(Latitude)+","+Double.toString(Longitude));
              
              dTime = new DistanceTime();
              dTime.getTime(Latitude,Longitude,postcode_1,postcode_2,context);
            }
         };
         MyLocation myLocation = new MyLocation();
         myLocation.getLocation(context, locationResult);
         Log.d("LocationUpdate","ended");
      }
      else
      {
         String[] t = currentLocation.split(" ");
         dTime = new DistanceTime();
         dTime.getTime(t[0],t[1],postcode_1,postcode_2,context);
      }
   }
}
