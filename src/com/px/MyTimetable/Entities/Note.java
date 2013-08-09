package com.px.MyTimetable.Entities;

import java.util.Calendar;

public class Note
{
   private Subject subject;
   private Calendar cal;
   private String filePath;
   
   public Note(Subject subject, Calendar cal, String filePath)
   {
      this.setSubject(subject);
      this.setCal(cal);
      this.setFilePath(filePath);
   }

   /**
    * @return the subject
    */
   public Subject getSubject()
   {
      return subject;
   }

   /**
    * @param subject the subject to set
    */
   public void setSubject(Subject subject)
   {
      this.subject = subject;
   }

   /**
    * @return the cal
    */
   public Calendar getCal()
   {
      return cal;
   }

   /**
    * @param cal the cal to set
    */
   public void setCal(Calendar cal)
   {
      this.cal = cal;
   }

   /**
    * @return the filePath
    */
   public String getFilePath()
   {
      return filePath;
   }

   /**
    * @param filePath the filePath to set
    */
   public void setFilePath(String filePath)
   {
      this.filePath = filePath;
   }
   
   public String getName()
   {
      String[] strings = this.filePath.split("/");
      return strings[strings.length -1];
   }
}
