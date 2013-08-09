package com.px.MyTimetable.Entities;

public class Subject
{
   private String name;
   private String unitCode;
   private String department;

   /** Initialises a new instance of the Subject class
    * 
    * @param name Name of the subject
    * @param unitCode Unit's code
    * @param department Department the subject belongs to
    */
   public Subject(String name, String unitCode, String department)
   {
      this.name = name;
      this.unitCode = unitCode;
      this.department = department;
   }

   /** Gets the name
    * 
    * @return Name of the subject
    */
   public String getName()
   {
      return this.name;
   }
   
   public String getUnitCode()
   {
      return this.unitCode;
   }
   
   /** Gets the department
    * 
    * @return Department the subject belongs to
    */
   public String getDepartment()
   {
      return this.department;
   }

   /** Sets the name of the subject
    * 
    * @param newName New name for the subject
    */
   public void setName(String newName)
   {
      this.name = newName;
   }
   
   public void setUnitCode(String newUnitCode)
   {
      this.unitCode = newUnitCode;
   }
   
   /** Sets the subjects department
    * 
    * @param newDepartment New department for the subject
    */
   public void setDepartment(String newDepartment)
   {
      this.department = newDepartment;
   }
   
   public boolean isEqual(Subject other)
   {
      return this.getUnitCode().equals(other.getUnitCode());
   }
}
