package com.px.MyTimetable.Settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.px.MyTimetable.R;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
       super.onCreate(savedInstanceState);

       addPreferencesFromResource(R.xml.settings);
       PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);
   }

   @Override
   public void onSharedPreferenceChanged(SharedPreferences options, String key)
   {
       // Workaround to make sure list preference's summary text is being updated.
       if (key.equals("volume"))
       {
           ListPreference listPreference = (ListPreference)findPreference(key);
           final String value = options.getString(key, key);
           final int index = listPreference.findIndexOfValue(value);            
           if (index >= 0)
           {
               final String summary = (String)listPreference.getEntries()[index];         
               listPreference.setSummary(summary);
           }
       }
   }
}
