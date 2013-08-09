package com.px.MyTimetable.TimetablePage;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.px.MyTimetable.Entities.Timetable;

public class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter
{
   // Number of days to show
   private final int PAGE_COUNT = 365;
   private Timetable timetable;
   
   public MyFragmentStatePagerAdapter(FragmentManager fm, Timetable timetable)
   {
      super(fm);
      this.timetable = timetable;
   }

   @Override
   public Fragment getItem(int arg0)
   {
      DayFragment dayFragment = new DayFragment();
      Calendar cal = (Calendar)timetable.getStartCal().clone();
      cal.add(Calendar.DAY_OF_YEAR, arg0);
      Bundle data = new Bundle();
      data.putSerializable("Calendar", cal);
      dayFragment.setArguments(data);
      return dayFragment;
   }

   @Override
   public int getCount()
   {
      return PAGE_COUNT;
   }
}
