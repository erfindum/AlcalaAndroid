package com.aratech.lectoras;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aratech.lectoras.adapters.PropertyAdapter;
import com.aratech.lectoras.adapters.PropertyAdapter.PropertyHolder;
import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg.RegType;
import com.aratech.lectoras.beans.Reg00;
import com.aratech.lectoras.beans.Reg20Comunidades;
import com.aratech.lectoras.data.RegDataBase;
import com.aratech.lectoras.data.classes.CalendarHelper;
import com.aratech.lectoras.data.classes.CalendarHelper.CalendarSelectionType;
import com.aratech.lectoras.data.classes.CalendarHelper.onDateClickListener;
import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg20Constants;

public class SelectionPropertyActivity extends Activity implements
		PropertyAdapter.OnRegSelectedListener, OnClickListener,
		onDateClickListener {

	ListView lvPropertys;
	public RegDataBase database;
	Button btnSave, btnReadings;
	Reg20Comunidades selectedReg;
	View selectedView;
	String previousDateFormated = null;
	String currentDateFormated = null;
	CalendarHelper calendarHelper;
	TextView tvCover;
	LinearLayout layoutCalendarView;
	Reg00 extraInfo;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property_selection);

		layoutCalendarView = (LinearLayout) findViewById(R.id.layoutCalendarView);
		calendarHelper = new CalendarHelper(layoutCalendarView, this);
		database = new RegDataBase(this);
		ArrayList<Reg> extraInfoArray = database.getRegsFromType(RegType.REG00);
		if(extraInfoArray != null && extraInfoArray.size() > 0){
			extraInfo = (Reg00) extraInfoArray.get(0);
			if(extraInfo != null){
				formatExtraInfoDate(extraInfo.date);
			}
		}
		
		lvPropertys = (ListView) findViewById(R.id.lvProperties);
		
		lvPropertys.setAdapter(new PropertyAdapter(
				SelectionPropertyActivity.this, database.getCommunitys(),
				SelectionPropertyActivity.this));

		tvCover = (TextView) findViewById(R.id.tvCover);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);

		btnReadings = (Button) findViewById(R.id.btnReadings);
		btnReadings.setOnClickListener(this);
		
		layoutCalendarView.setVisibility(View.INVISIBLE);
	}

	void formatExtraInfoDate(String date){
		String[] splittedDate = date.split("/");
		if(splittedDate[2].length()>=4) {
			splittedDate[2] = splittedDate[2].substring(2, 4);
			StringBuilder formattedDateBuilder = new StringBuilder();
			for(int i=0;i<=2;i++){
				String entry = splittedDate[i]+"/";
				formattedDateBuilder.append(entry);
			}
			 extraInfo.date = formattedDateBuilder.toString().substring(0,formattedDateBuilder.length()-1);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSave:
			if (checkDate()) {
				clickSave();
			} else {
				DialogWarning dialog = new DialogWarning(
						SelectionPropertyActivity.this,
						getString(R.string.advertencia_fecha),
						new DialogWarning.OnAcceptWarningListener() {
							@Override
							public void onAccept() {
								clickSave();

							}
						});

				dialog.show();
			}

			break;
		case R.id.btnReadings:
			clickReadings();
			break;
		}

	}

	private boolean checkDate() {
		try {
			if (selectedReg == null || previousDateFormated == null
					|| previousDateFormated.equals("")
					|| currentDateFormated == null
					|| currentDateFormated.equals("")) {
				return true;
			}

			int DAY = 0, MONTH = 1, YEAR = 2;

			String[] splittedInitDate = previousDateFormated.split("/");
			if(splittedInitDate[YEAR].toCharArray().length > 2){
				splittedInitDate[YEAR] = splittedInitDate[YEAR].substring(2, 4);
			}
			String[] splittedEndDate = currentDateFormated.split("/");

			if (Integer.valueOf(splittedInitDate[YEAR]) <= Integer
					.valueOf(splittedEndDate[YEAR])) {
				return true;
			} else if (Integer.valueOf(splittedInitDate[YEAR]) > Integer
					.valueOf(splittedEndDate[YEAR])) {
				return false;
			}

			if (Integer.valueOf(splittedInitDate[MONTH]) < Integer
					.valueOf(splittedEndDate[MONTH])) {
				return true;
			} else if (Integer.valueOf(splittedInitDate[MONTH]) > Integer
					.valueOf(splittedEndDate[MONTH])) {
				return false;
			}

			if (Integer.valueOf(splittedInitDate[DAY]) < Integer
					.valueOf(splittedEndDate[DAY])) {
				return true;
			} else if (Integer.valueOf(splittedInitDate[DAY]) > Integer
					.valueOf(splittedEndDate[DAY])) {
				return false;
			}
			return true;
		} catch (Exception ex) {
			Log.e("CHECK DATE", String.valueOf(ex));
			return true;
		}
	}

	private void clickReadings() {
		if (selectedReg == null) {
			showSelectCommunityToast();
		} else {
			Intent i = new Intent(SelectionPropertyActivity.this,
					ReadingActivity.class);
			i.putExtra(Reg20Constants.COMMUNITY,
					selectedReg.getCommunityNumber());
			startActivityForResult(i, 7337);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 7337) {
			updateSelectedFloorsColorState();
		}
	}

	private void clickSave() {
		if (selectedReg == null) {
			showSelectCommunityToast();
		} else {
			int selectedCommunity = selectedReg.getCommunityNumber();

			ArrayList<Reg> selectedRegs = database
					.getPropertyByCommunity(selectedCommunity);

			for (Reg newReg : selectedRegs) {
				((Reg20Comunidades) newReg).setViewed(true);
				((Reg20Comunidades) newReg).initDate = previousDateFormated;
				((Reg20Comunidades) newReg).endDate = currentDateFormated;
				database.asyncUpdateReg(newReg, this);
			}
			// reload();
			selectedReg.setViewed(true);
			selectedReg.initDate = previousDateFormated;
			selectedReg.endDate = currentDateFormated;
			database.asyncUpdateReg(selectedReg, this);
			try {
				calendarHelper.loadValuesFromReg20(selectedReg);
			} catch (Exception ex) {
				Toast.makeText(this, "Error al cargar la fecha seleccionada",
						Toast.LENGTH_SHORT).show();
			}
			updateSelectedDateColorState();
		}
	}

	private void showSelectCommunityToast() {
		Toast.makeText(this, getString(R.string.selecciona_comunidad),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onSelected(Reg reg, View view) {

		if (reg != null && view != null) {
			if (selectedView != null) {
				selectedView.setBackgroundColor(getResources().getColor(
						android.R.color.transparent));
			}

			tvCover.setVisibility(View.GONE);
			layoutCalendarView.setVisibility(View.VISIBLE);
			
			selectedReg = (Reg20Comunidades) reg;
			currentDateFormated = selectedReg.endDate;
			
			if((selectedReg.initDate == null || selectedReg.initDate.equals(""))&& extraInfo != null){
					selectedReg.initDate = extraInfo.date;		
			}
			
			previousDateFormated = selectedReg.initDate;
			
			view.setBackgroundResource(R.drawable.azul_alpha_marco_nine);
			selectedView = view;
			try {
				calendarHelper.loadValuesFromReg20(selectedReg);
			} catch (Exception ex) {
				Toast.makeText(this, "Error al cargar la fecha seleccionada",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void updateSelectedDateColorState() {
		try {
			ImageView imgStateDate = (ImageView) selectedView
					.findViewById(R.id.imgDateState);
			if (selectedReg != null) {
				if (!selectedReg.initDate.equals("")&& !selectedReg.endDate.equals("")) {
					imgStateDate.setImageDrawable(getResources().getDrawable(R.drawable.state_green));
					
					// K.O.B. 1/4/14 -> Original code -> } else if (!selectedReg.initDate.equals("")||!selectedReg.initDate.equals("")){					
				} else if (!selectedReg.initDate.equals("")|| !selectedReg.endDate.equals("")) {
					imgStateDate.setImageDrawable(getResources().getDrawable(R.drawable.state_yellow));
				} else {
					imgStateDate.setImageDrawable(getResources().getDrawable(R.drawable.state_red));
				}
			}
		} catch (Exception ex) {
			Log.e("STATE IMAGE UPDATE", String.valueOf(ex));
		}
	}

	private void updateSelectedFloorsColorState() {
		if (selectedReg != null) {
			int state = database.getFloorsState(selectedReg);
			ImageView imgStateFloors = (ImageView) selectedView
					.findViewById(R.id.imgFloorState);
			switch (state) {
			case -1:
				imgStateFloors.setImageDrawable(getResources().getDrawable(
						R.drawable.state_red));
				break;
			case 0:
				imgStateFloors.setImageDrawable(getResources().getDrawable(
						R.drawable.state_yellow));
				break;
			case 1:
				imgStateFloors.setImageDrawable(getResources().getDrawable(
						R.drawable.state_green));
				break;
			default:
				imgStateFloors.setImageDrawable(getResources().getDrawable(
						R.drawable.state_grey));
			}
		}

	}

	@Override
	public void onDateClick(String formattedDate,
			CalendarSelectionType selectionType) {
		if (selectionType == CalendarSelectionType.CURRENT) {
			currentDateFormated = formattedDate;
		} else {
			previousDateFormated = formattedDate;
		}

	}
}
