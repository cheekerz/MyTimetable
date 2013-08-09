package com.px.MyTimetable.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.px.MyTimetable.R;

public class DialogFactory
{   
   /**
    * Creates a new dialog with 2 buttons, Cancel and OK. Must call .show() to display
    * @param message Message to display with dialog
    * @param listener listening class
    * @param id Id of this dialog, used to distinguish between multiple dialog calls in single listener 
    * @param context activity context
    * @return 2 button dialog
    */
   public static AlertDialog getDialog(int message, final DialogListener listener, final int id, Context context)
   {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      return setButtons(builder.setMessage(message), listener, id);
   }
   
   /**
    * Creates a new dialog with 2 buttons, Cancel and OK. Must call .show() to display
    * @param message Message to display with dialog
    * @param listener listening class
    * @param id Id of this dialog, used to distinguish between multiple dialog calls in single listener 
    * @param context activity context
    * @return 2 button dialog
    */
   public static AlertDialog getDialog(String message, final DialogListener listener, final int id, Context context)
   {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      return setButtons(builder.setMessage(message), listener, id);
   }
   
   private static AlertDialog setButtons(AlertDialog.Builder builder, final DialogListener listener, final int id)
   {
      builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener()
      {
         public void onClick(DialogInterface dialog, int which)
         {
            listener.onDialogPositiveClick(id);
         }
      })
      .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener()
      {
         public void onClick(DialogInterface dialog, int which)
         {
            listener.onDialogNegativeClick(id);
         }
      });
      
      return builder.create();
   }
}
