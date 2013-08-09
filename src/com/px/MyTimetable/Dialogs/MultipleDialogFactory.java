package com.px.MyTimetable.Dialogs;

import com.px.MyTimetable.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MultipleDialogFactory
{
   /**
    * Creates a new dialog with 3 buttons, Cancel, Just this one, all instances. Must call .show() to display
    * @param message Message to display with dialog
    * @param listener listening class
    * @param id Id of this dialog, used to distinguish between multiple dialog calls in single listener 
    * @param context activity context
    * @return 3 button dialog
    */
   public static AlertDialog getDialog(int message, final MultipleDialogListener listener, final int id, Context context)
   {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setMessage(message)
         .setPositiveButton(R.string.DialogApplyToAll, new DialogInterface.OnClickListener()
         {
             public void onClick(DialogInterface dialog, int which)
             {
                listener.onDialogPositiveClick(id);
             }
         })
         .setNeutralButton(R.string.DialogApplyToOne, new DialogInterface.OnClickListener()
         {
             public void onClick(DialogInterface dialog, int which)
             {
                listener.onDialogNeutralClick(id);
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
/**
	 * Creates a new dialog with 3 custom buttons. Must call .show() to display
	 * 
	 * @param message
	 *            Message to display with dialog
	 * @param buttons
	 * 			  messages to be displayed on the 3 buttons, positive click, neutral click and negative click
	 * @param listener
	 *            listening class
	 * @param id
	 *            Id of this dialog, used to distinguish between multiple dialog
	 *            calls in single listener
	 * @param context
	 *            activity context
	 * @return 3 button dialog
	 */
	public static AlertDialog getDialog(int message, int[] buttons,
			final MultipleDialogListener listener, final int id, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
				.setPositiveButton(buttons[0],
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onDialogPositiveClick(id);
							}
						})
				.setNeutralButton(buttons[1],
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onDialogNeutralClick(id);
							}
						})
				.setNegativeButton(buttons[2],
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onDialogNegativeClick(id);
							}
						});

		return builder.create();
	}
}
