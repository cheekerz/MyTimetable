package com.px.MyTimetable.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Main.TimetableAccess;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class DistanceTime{
   
   Context context;
   void getTime(Double latitude, Double longitude, String dest_postcode_1, String dest_postcode_2, Context context){
      this.context = context;
      Log.d("DistanceTime","started");

      StringBuilder sBuilder = new StringBuilder();
      sBuilder.append("http://maps.googleapis.com/maps/api/directions/json?");
      //from
      sBuilder.append("origin=");
      sBuilder.append(latitude);
      sBuilder.append(",");
      sBuilder.append(longitude);
      //to
      sBuilder.append("&destination=");
      sBuilder.append(dest_postcode_1);
      sBuilder.append(",");
      sBuilder.append(dest_postcode_2);
      sBuilder.append("&mode=walking&sensor=true");
      //for debugging in LogCat
      Log.d("DistanceTime","L&L="+latitude+","+longitude);
      
      String url = sBuilder.toString();
      Log.d("url",url);
      new getJSON(context).execute(url);
      Log.d("DistanceTime","ending");     
      }
   
   void getTime(String postcode_1, String postcode_2, String dest_postcode_1, String dest_postcode_2, Context context){
      this.context = context;
      Log.d("DistanceTime","started");

      StringBuilder sBuilder = new StringBuilder();
      sBuilder.append("http://maps.googleapis.com/maps/api/directions/json?");
      //from
      sBuilder.append("origin=");
      sBuilder.append(postcode_1+" ");
      sBuilder.append(",");
      sBuilder.append(postcode_2);
      //to
      sBuilder.append("&destination=");
      sBuilder.append(postcode_1);
      sBuilder.append(",");
      sBuilder.append(postcode_2);
      sBuilder.append("&mode=walking&sensor=true");
      //for debugging in LogCat
      Log.d("DistanceTime","Postcode =" + postcode_1 +" "+ postcode_2);
      
      String url = sBuilder.toString();
      Log.d("url",url);
      new getJSON(context).execute(url);
      Log.d("DistanceTime","ending");     
      }  
}

class getJSON extends AsyncTask<String, Void, Void> {
   Context context;
   Lecture nextLecture;
   AlarmManager service;
   Intent intent_2;
   PendingIntent pIntent_2;
   Calendar cal;
   
   public getJSON(Context context){
      this.context = context;
      nextLecture = ((TimetableAccess)context.getApplicationContext()).getTimetable().getNextLecture(Calendar.getInstance());
      //Log.d("NextLecture","works");
      service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      intent_2 = new Intent(context, ServiceStart.class);
      pIntent_2 = PendingIntent.getBroadcast(context, 0, intent_2, PendingIntent.FLAG_CANCEL_CURRENT);
      cal = Calendar.getInstance();
      Log.d("Context","works");

   }
   
    protected Void doInBackground(String... sBuilder) {
      
      String temp = "";
      String response = "";
      HttpURLConnection urlConnection = null;
      //Log.d("try",sBuilder[0]);
      
      try {
         //Internet request
         URL url = new URL(sBuilder[0]);
         urlConnection = (HttpURLConnection) url.openConnection();
         urlConnection.setRequestMethod("GET");
         urlConnection.setDoOutput(true);
         urlConnection.setDoInput(true);
         urlConnection.connect();
         InputStream inStream = urlConnection.getInputStream();
         BufferedReader bufReader = new BufferedReader(new InputStreamReader(inStream));
         while((temp = bufReader.readLine())!=null){
            response += temp;
         }
         bufReader.close();
          inStream.close();
          urlConnection.disconnect();
          
          
          //JSON parser
          JSONObject jObject = (JSONObject) new JSONTokener(response).nextValue();
          JSONArray jArray = jObject.getJSONArray("routes");
          JSONObject routes = jArray.getJSONObject(0);
          JSONArray legs = routes.getJSONArray("legs");
          JSONObject d = legs.getJSONObject(0);
          JSONObject duration = d.getJSONObject("duration");
          int time = duration.getInt("value");
          //CatLog
          Log.d("JSON","duration="+time);
          
          
          int timeSlot = nextLecture.getTimeSlot();
          cal.set(Calendar.HOUR_OF_DAY, timeSlot);
          service.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()-time*1000, pIntent_2);
          
         
      } catch (MalformedURLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (JSONException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      
        return null;
    }
}

