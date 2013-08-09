package com.px.MyTimetable.Entities;

public class Place
{
   // Private fields:
   private String shortAddress;
   private String room;
   private String building;
   private String postcode;
   private long id;
 

   public Place(long id, String shortAddress, String room, String building, String postcode)
   {
      this.setShortAddress(shortAddress);
      this.setRoom(room);
      this.setBuilding(building);
      this.setPostcode(postcode);
      this.setId(id);
   }

   public boolean isEqual(Place other)
   {      
      return this.id == other.id; 
   }

   /**
    * @return the id
    */
   public long getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(long id)
   {
      this.id = id;
   }

   /**
    * @return the shortAddress
    */
   public String getShortAddress()
   {
      return shortAddress;
   }

   /**
    * @param shortAddress the shortAddress to set
    */
   public void setShortAddress(String shortAddress)
   {
      this.shortAddress = shortAddress;
   }

   /**
    * @return the room
    */
   public String getRoom()
   {
      return room;
   }

   /**
    * @param room the room to set
    */
   public void setRoom(String room)
   {
      this.room = room;
   }

   /**
    * @return the building
    */
   public String getBuilding()
   {
      return building;
   }

   /**
    * @param building the building to set
    */
   public void setBuilding(String building)
   {
      this.building = building;
   }

   /**
    * @return the postcode
    */
   public String getPostcode()
   {
      return postcode;
   }

   /**
    * @param postcode the postcode to set
    */
   public void setPostcode(String postcode)
   {
      this.postcode = postcode;
   }
   public String getFullAddress()
   {
      String fullAddress = "";
      fullAddress = this.getRoom() + "\n";
      fullAddress += this.getBuilding() + "\n";
      fullAddress += this.getPostcode();
      
//      int i = 0;
//      while(this.address.getAddressLine(i) != null)
//      {
//         if(i == 0) fullAddress += this.address.getAddressLine(i);
//         else fullAddress = fullAddress + "\n" + this.address.getAddressLine(i);
//         i++;
//      }
//      if(this.address.getPostalCode() != null) fullAddress += "\n" + this.address.getPostalCode();
      return fullAddress;
   }
}
