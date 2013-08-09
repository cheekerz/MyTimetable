package com.px.MyTimetable.Entities.People;

public class Student extends Person
{

   /** Initialises a new instance of the Student class
    * 
    * @param name Name of the student
    * @param title Student's title
    * @param number Student's phone number
    * @param email Student's email address
    */
   public Student(String name, String title, String number, String email)
   {
      super(name, title, number, email);
   }
}
