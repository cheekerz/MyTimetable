package com.px.MyTimetable.FileChooser;

public class Option implements Comparable<Option>
{

   private String name, path, data;
   
   public Option(String n, String d, String p){
      name = n;
      data = d;
      path = p;
   }
   
   public String getName(){
      return name;
   }
   
   public String getPath(){
      return path;
   }
   
   public String getData(){
      return data;
   }
   
   @Override
   public int compareTo(Option o)
   {
      // TODO Auto-generated method stub
      if (this.name != null)
         return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
      else
         throw new IllegalArgumentException();
   }

}
