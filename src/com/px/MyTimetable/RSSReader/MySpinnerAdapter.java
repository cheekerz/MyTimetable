package com.px.MyTimetable.RSSReader;

import com.px.MyTimetable.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class MySpinnerAdapter extends ArrayAdapter<String> implements SpinnerAdapter
{

   Context context;
   
   public MySpinnerAdapter(Context context, int textViewResourceId, String[] objects)
   {
      super(context, textViewResourceId, objects);
      this.context = context;
   }
   
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View v = vi.inflate(R.layout.spinner_prompt, null);
      return v;
   }

}
