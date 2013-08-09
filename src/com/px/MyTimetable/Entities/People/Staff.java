package com.px.MyTimetable.Entities.People;

import com.px.MyTimetable.Entities.Place;

public class Staff extends Person
{
   private Place office;

   /** Initialises a new instance of the Staff class
    * 
    * @param name Name of the staff
    * @param title Staff's title
    * @param number Staff's phone number
    * @param email Staff's email address
    * @param office Staff's office
    * @param department Staff's department
    */
   public Staff(String name, String title, String number, String email, Place office)
   {
      super(name, title, number, email);
      this.office = office;
   }

   /** Gets staff's office
    * 
    * @return Staff's office
    */
   public Place getOffice()
   {
      return this.office;
   }

   /** Sets staff's office
    * 
    * @param newOffice new office of staff
    */
   public void setOffice(Place newOffice)
   {
      this.office = newOffice;
   }
}
