package com.px.MyTimetable.Dialogs;

/* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks. */
public interface DialogListener
{
    public void onDialogPositiveClick(int id);
    
    public void onDialogNegativeClick(int id);
}
