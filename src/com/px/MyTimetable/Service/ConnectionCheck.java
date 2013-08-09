package com.px.MyTimetable.Service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionCheck
{
   Context context;
   public ConnectionCheck(Context context){
      this.context = context;
   }
   
   public boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      if(connectivityManager != null){
         NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
         if(networkInfo != null){
            for(int i = 0; i < networkInfo.length; i++){
               if(networkInfo[i].getState() == NetworkInfo.State.CONNECTED){
                  return true;
               }
            }
         }
      }
      return false;
  }

   public ConnectivityManager getSystemService(String connectivityService)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
