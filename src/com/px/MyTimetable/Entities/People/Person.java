package com.px.MyTimetable.Entities.People;

public class Person
{
   private String name;
   private String title;
   private String number;
   private String email;

   /** Initialises a new instance of the Person class
    * 
    * @param name Name of the person
    * @param title Person's title
    * @param number Person's phone number
    * @param email Person's email address
    */
   public Person(String name, String title, String number, String email)
   {
      this.name = name;
      this.title = title;
      this.number = number;
      this.email = email;
   }

   /** Gets Person's name
    * 
    * @return Name of the person
    */
   public String getName()
   {
      return this.name;
   }
   
   /** Gets title of person
    * 
    * @return Person's title
    */
   public String getTitle()
   {
      return this.title;
   }
   
   /** Gets phone number of person
    * 
    * @return Person's phone number
    */
   public String getNumber()
   {
      return this.number;
   }
   
   /** Gets email address of person
    * 
    * @return Person's email address
    */
   public String getEmail()
   {
      return this.email;
   }

   /** Sets Person's name
    * 
    * @param newName New name of person
    */
   public void setName(String newName)
   {
      this.name = newName;
   }
   
   /** Sets person's title
    * 
    * @param newTitle New title of person
    */
   public void setTitle(String newTitle)
   {
      this.title = newTitle;
   }
   
   /** Sets person's phone number
    * 
    * @param newNumber New phone number of person
    */
   public void setNumber(String newNumber)
   {
      this.number = newNumber;
   }
   
   /** Sets email address of person
    * 
    * @param newEmail New email address of person
    */
   public void setEmail(String newEmail)
   {
      this.email = newEmail;
   }
}
