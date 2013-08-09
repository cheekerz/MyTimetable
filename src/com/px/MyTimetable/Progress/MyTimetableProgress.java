package com.px.MyTimetable.Progress;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.px.MyTimetable.R;
import com.px.MyTimetable.Dialogs.MultipleDialogFactory;
import com.px.MyTimetable.Dialogs.MultipleDialogListener;
import com.px.MyTimetable.Entities.Lecture;
import com.px.MyTimetable.Entities.Subject;
import com.px.MyTimetable.Entities.Timetable;
import com.px.MyTimetable.Main.TimetableAccess;
import com.px.MyTimetable.Settings.MyTimetableSettings;

public class MyTimetableProgress extends Activity implements OnClickListener,
		MultipleDialogListener {
	public static final String PROGRESS_NAME = "MyTimetableProgress";
	List<Subject> subjects;
	List<List<Integer>> lectureIdsBySubject;

	@Override
	protected void onResume() {
		super.onResume();
		this.getActionBar().setHomeButtonEnabled(true);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_progress);
		LinearLayout progressLayout = (LinearLayout) findViewById(R.id.LinearLayout_progress);
		this.subjects = ((TimetableAccess) this.getApplication())
				.getTimetable().getSubjects();

		lectureIdsBySubject = new ArrayList<List<Integer>>();
		for (Subject s : subjects) {
			List<Integer> ids = new ArrayList<Integer>();
			for (Lecture l : ((TimetableAccess) this.getApplication())
					.getTimetable().getLectures(s)) {
				ids.add((int) l.getID());
			}
			lectureIdsBySubject.add(ids);
		}

		// Display the fragment as the main content.
		for (int i = 0; i < subjects.size(); i++) {
			String subjectName = subjects.get(i).getName();
			List<Integer> lectureIds = lectureIdsBySubject.get(i);

			Timetable timetable = ((TimetableAccess) this.getApplication())
					.getTimetable();
			List<Integer> lectureAttendances = new ArrayList<Integer>();
			for (int id : lectureIds) {
				lectureAttendances.add(timetable.getLectureById(id)
						.getAttendance());
			}

			View view = getLayoutInflater().inflate(R.layout.progress_view,
					progressLayout, false);
			populateProgressView(view, subjectName,
					(int) percentAttendance(lectureAttendances),
					lectureAttendances.size(), attended(lectureAttendances),
					missed(lectureAttendances));
			view.setOnClickListener(this);
			//view.findViewById(R.id.setAllButton).setTag(subjects.get(i));
			//view.findViewById(R.id.setAllButton).setOnClickListener(this);
			// if (attended() == 0 && missed() == 0) {
			// //v.setVisibility(View.GONE);
			// }
			progressLayout.addView(view);
		}

	}

	/**
	 * progressView has been clicked
	 */
	@Override
	public void onClick(View v) {
		Log.v(PROGRESS_NAME, "clicked view");//
		if (v.getTag().equals("progressView")) {
			showHideStats(v);
		} else {
			Log.v(PROGRESS_NAME, "clicked button");
			// assuming that the user has clicked the setAllAttended button
			setAllDialogue((Subject) v.getTag());
		}
	}

	public static void showHideStats(View v) {
		if (v.findViewById(R.id.moreLayout).getVisibility() == View.VISIBLE) {
			((LinearLayout) v.findViewById(R.id.moreLayout))
					.setVisibility(View.GONE);

		} else {
			((LinearLayout) v.findViewById(R.id.moreLayout))
					.setVisibility(View.VISIBLE);
		}
	}

	public static void populateProgressView(View progressView, String name,
			int percentAttendance, int total, int attended, int missed) {
		// Use time as ID to use later for clicking time slots
		progressView.setId(1);
		progressView.setTag("progressView");
		((TextView) progressView.findViewById(R.id.progress_view_title))
				.setText(name);
		((TextView) progressView.findViewById(R.id.progress_view_percent))
				.setText(Integer.toString(percentAttendance) + "%");
		((TextView) progressView.findViewById(R.id.text_view_attended_num))
				.setText(Integer.toString(attended));
		((TextView) progressView.findViewById(R.id.text_view_missed_num))
				.setText(Integer.toString(missed));
		((TextView) progressView.findViewById(R.id.text_view_unmarked_num))
				.setText(Integer.toString(total - attended - missed));
		((TextView) progressView.findViewById(R.id.text_view_total_num))
				.setText(Integer.toString(total));
		// TODO: get rid of margin definitions from code
		ProgressBar pg = (ProgressBar) progressView
				.findViewById(R.id.progress_bar);
		pg.setProgress(percentAttendance);
		pg.setSecondaryProgress(100);
	}

	public static double percentAttendance(List<Integer> lectureAttendances) {
		double totalMarkedAttendance = 0;
		double totalAttended = 0;
		for (int a : lectureAttendances) {
			if (a != Lecture.ATTENDANCE_NULL) {
				totalMarkedAttendance++;
				if (a == Lecture.ATTENDANCE_TRUE) {
					totalAttended++;
				}
			}
		}
		return (totalMarkedAttendance == 0.0) ? 0.0
				: (100.0 * totalAttended / totalMarkedAttendance);
	}

	public static int attended(List<Integer> lectureAttendances) {
		int attended = 0;
		for (int a : lectureAttendances) {
			if (a == Lecture.ATTENDANCE_TRUE) {
				attended++;
			}
		}
		return attended;
	}

	public static int missed(List<Integer> lectureAttendances) {
		int missed = 0;
		for (int a : lectureAttendances) {
			if (a == Lecture.ATTENDANCE_FALSE) {
				missed++;
			}
		}
		return missed;
	}

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.activity_progress, menu);
      return true;
   }

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
         case R.id.menu_settings:
            Intent intent = new Intent(this, MyTimetableSettings.class);
            startActivity(intent);
            return true;
            
      	case android.R.id.home:
      		this.finish();
      		return true;
      
      	default:
      		return super.onOptionsItemSelected(item);
		}
	}

	public void setAllDialogue(Subject subject) {
		MultipleDialogFactory
				.getDialog(
						R.string.SetAllMessage,
						new int[] { R.string.SetAllButton1,
								R.string.SetAllButton2, R.string.SetAllButton3 },
						this,
						((TimetableAccess) this.getApplication())
								.getTimetable().getSubjectIndex(
										subject.getUnitCode()), (Context) this)
				.show();
		Log.v(PROGRESS_NAME, "dialog");
	}

	public void onDialogPositiveClick(int id) {
	};

	public void onDialogNegativeClick(int id) {
	};

	public void onDialogNeutralClick(int id) {
		//Timetable t = ((TimetableAccess) this.getApplication()).getTimetable();
		//t.markLectures(t.getSubject(id), t.getStartCal(), Calendar.getInstance());
	};
}
