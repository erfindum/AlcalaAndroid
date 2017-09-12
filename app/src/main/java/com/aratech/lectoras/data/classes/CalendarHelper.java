package com.aratech.lectoras.data.classes;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.aratech.lectoras.R;
import com.aratech.lectoras.beans.Reg20Comunidades;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CalendarView.OnDateChangeListener;

public class CalendarHelper implements OnClickListener {

	private LinearLayout mParent;
	private onDateClickListener mListener;
	RelativeLayout selectedDateLayout, previousDateLayout, currentDateLayout,
			calendarLayout;
	TextView tvLabelCurrentDate, tvLabelPreviousDate, tvPreviousState,
			tvCurrentState, tvSelectedDate;
	CalendarSelectionType mCurrentSelection;
	String currentSelectedDated = null, previousSelectedDated = null;
	CalendarView calendarView;

	public CalendarHelper(LinearLayout calendarParentLayout,
			onDateClickListener dateClickListener) {
		mParent = calendarParentLayout;
		mListener = dateClickListener;

		currentDateLayout = (RelativeLayout) mParent
				.findViewById(R.id.layout_current_date);
		currentDateLayout.setOnClickListener(this);

		previousDateLayout = (RelativeLayout) mParent
				.findViewById(R.id.layout_previous_date);
		previousDateLayout.setOnClickListener(this);

		calendarLayout = (RelativeLayout) mParent
				.findViewById(R.id.layout_calendar);

		tvLabelCurrentDate = (TextView) mParent
				.findViewById(R.id.tv_current_date);
		tvLabelPreviousDate = (TextView) mParent
				.findViewById(R.id.tv_previous_date);

		tvPreviousState = (TextView) mParent
				.findViewById(R.id.tv_previous_state);
		tvCurrentState = (TextView) mParent.findViewById(R.id.tv_current_state);

		tvSelectedDate = (TextView) mParent.findViewById(R.id.tvDate);

		calendarView = (CalendarView) mParent.findViewById(R.id.calendar);
		calendarView.setFirstDayOfWeek(Calendar.MONDAY);
		calendarView.setOnDateChangeListener(calendarListener);
		mCurrentSelection = CalendarSelectionType.CURRENT;
		// disableClick();
		setInitialCurrentDate();
	}

    private void setInitialCurrentDate(){
        Calendar currentCal = Calendar.getInstance();
        String currentDateString = getFormatedDate(currentCal.get(Calendar.YEAR),
                                                    currentCal.get(Calendar.MONTH),
                                                    currentCal.get(Calendar.DAY_OF_MONTH));
        tvSelectedDate.setText(currentDateString);
    }

	private OnDateChangeListener calendarListener = new OnDateChangeListener() {

		@Override
		public void onSelectedDayChange(CalendarView view, int year, int month,
				int dayOfMonth) {
			if (mCurrentSelection != null
					&& mCurrentSelection == CalendarSelectionType.CURRENT) {
				String formattedDate = getFormatedDate(year, month, dayOfMonth);
				tvCurrentState.setText(formattedDate);
				setTextViewYellow(tvCurrentState);
				tvSelectedDate.setText(formattedDate);
				mListener.onDateClick(formattedDate, mCurrentSelection);
			}

		}
	};

	public void disableClick() {
		try {
			calendarView.setEnabled(false);
			calendarView.setOnDateChangeListener(null);
			disableClickView(mParent);
		} catch (Exception ex) {
			Log.e("DISABLE CLICK",
					"Error al desactivar los clicks del calendario");
		}
	}

	private void disableClickView(View view) {
		view.setClickable(false);
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

				disableClickView(((ViewGroup) view).getChildAt(i));

			}
		}

	}

	public void enableClick() {
		try {
			calendarView.setEnabled(true);
			calendarView.setOnDateChangeListener(calendarListener);
			enableClickView(mParent);
			previousDateLayout.setOnClickListener(this);
			currentDateLayout.setOnClickListener(this);

			tvLabelCurrentDate.setOnClickListener(this);
			tvCurrentState.setOnClickListener(this);
			tvLabelPreviousDate.setOnClickListener(this);
			tvPreviousState.setOnClickListener(this);

		} catch (Exception ex) {
			Log.e("ENABLE CLICK", "Error al activar los clicks del calendario");
		}
	}

	private void enableClickView(View view) {
		view.setClickable(true);
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

				enableClickView(((ViewGroup) view).getChildAt(i));

			}
		}

	}

	public void loadValuesFromReg20(Reg20Comunidades currentSelected) {

		// enableClick();
		if (currentSelected.initDate != null
				&& !currentSelected.initDate.equals("")) {
			try {

				previousSelectedDated = currentSelected.initDate;
				tvPreviousState.setText(previousSelectedDated);
				setTextViewColor(tvPreviousState,
						Integer.valueOf(android.R.color.white));

			} catch (Exception ex) {
			}
		} else {
			tvPreviousState.setText(R.string.no);
			setTextViewRed(tvPreviousState);
		}

		if (currentSelected.endDate != null
				&& !currentSelected.endDate.equals("")) {

			currentSelectedDated = currentSelected.endDate;
			tvCurrentState.setText(currentSelectedDated);
			setTextViewGreen(tvCurrentState);

		} else {
			tvCurrentState.setText(R.string.no);
			setTextViewRed(tvCurrentState);
		}

		mCurrentSelection = CalendarSelectionType.CURRENT;
		calendarView.setOnDateChangeListener(null);
		loadCalendarFromFormattedDate(currentSelectedDated);
		calendarView.setOnDateChangeListener(calendarListener);

	}

	private void loadCalendarFromFormattedDate(String formatedDate) {

		int DAY = 0, MONTH = 1, YEAR = 2;
		if (formatedDate != null && !formatedDate.equals("")) {
			tvSelectedDate.setText(formatedDate);

			String[] dateSplitted = formatedDate.split("/");

			int day = Integer.valueOf(dateSplitted[DAY]);
			int month = Integer.valueOf(dateSplitted[MONTH]) - 1;
			int year = Integer.valueOf(dateSplitted[YEAR]);
			/*if (year > 90) {
				year += 1900;
			} else {
				year += 2000;
			}*/

			GregorianCalendar cal = new GregorianCalendar(year, month, day);
			calendarView.setDate(cal.getTimeInMillis());
		}
	}

	public enum CalendarSelectionType {
		CURRENT, PREVIOUS;
	}

	public interface onDateClickListener {

		public void onDateClick(String formattedDate,
				CalendarSelectionType selectionType);

	}

	public void setTextViewRed(TextView view) {
		setTextViewColor(view, Integer.valueOf(R.color.state_red));
	}

	public void setTextViewGreen(TextView view) {
		setTextViewColor(view, Integer.valueOf(R.color.state_green));
	}

	public void setTextViewYellow(TextView view) {
		setTextViewColor(view, Integer.valueOf(R.color.state_yellow));
	}

	private void setTextViewColor(TextView view, int colorResourceId) {
		if (view != null) {
			view.setTextColor(mParent.getResources().getColor(colorResourceId));
		}
	}

	@Override
	public void onClick(View v) {
		/*
		 * if (v.getId() == R.id.layout_current_date || v.getId() ==
		 * R.id.tv_current_date || v.getId() == R.id.tv_current_state) { if
		 * ((selectedDateLayout != null && selectedDateLayout !=
		 * currentDateLayout) || selectedDateLayout == null) {
		 * selectedDateLayout = currentDateLayout; mCurrentSelection =
		 * CalendarSelectionType.CURRENT;
		 * tvLabelPreviousDate.setTextColor(mParent.getResources()
		 * .getColor(android.R.color.black)); previousDateLayout
		 * .setBackgroundResource(R.drawable.negro_alpha_bacground);
		 * currentDateLayout
		 * .setBackgroundResource(R.drawable.azul_alpha_bacground);
		 * tvLabelCurrentDate.setTextColor(mParent.getResources()
		 * .getColor(android.R.color.white)); if (currentSelectedDated != null)
		 * { calendarView.setOnDateChangeListener(null);
		 * loadCalendarFromFormattedDate(currentSelectedDated);
		 * calendarView.setOnDateChangeListener(calendarListener); }
		 * 
		 * } } else if (v.getId() == R.id.layout_previous_date || v.getId() ==
		 * R.id.tv_previous_date || v.getId() == R.id.tv_previous_state) { if
		 * ((selectedDateLayout != null && selectedDateLayout !=
		 * previousDateLayout) || selectedDateLayout == null) {
		 * selectedDateLayout = previousDateLayout; mCurrentSelection =
		 * CalendarSelectionType.PREVIOUS;
		 * tvLabelPreviousDate.setTextColor(mParent.getResources()
		 * .getColor(android.R.color.white)); previousDateLayout
		 * .setBackgroundResource(R.drawable.azul_alpha_bacground);
		 * currentDateLayout
		 * .setBackgroundResource(R.drawable.negro_alpha_bacground);
		 * tvLabelCurrentDate.setTextColor(mParent.getResources()
		 * .getColor(android.R.color.black)); if (previousSelectedDated != null)
		 * { calendarView.setOnDateChangeListener(null);
		 * loadCalendarFromFormattedDate(previousSelectedDated);
		 * calendarView.setOnDateChangeListener(calendarListener); } } }
		 */
	}

	public static String getFormatedDate(int year, int month, int dayOfMonth) {
		// DD/MM/AA

		StringBuilder resultBuilder = new StringBuilder();
		if (dayOfMonth < 10)
			resultBuilder.append("0");
		resultBuilder.append(dayOfMonth);
		resultBuilder.append("/");

		if (month + 1 < 10)
			resultBuilder.append("0");
		resultBuilder.append(month + 1);
		resultBuilder.append("/");

		/*if (year >= 2000) {
			year -= 2000;
		} else {
			year -= 1900;
		}
		if (year < 10) {
			resultBuilder.append("0");
		}*/
		resultBuilder.append(year);

		return resultBuilder.toString();
	}

}
