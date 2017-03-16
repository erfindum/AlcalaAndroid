package com.aratech.lectoras;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aratech.lectoras.DialogRadio.OnEndRadioReadListener;
import com.aratech.lectoras.adapters.FloorAdapter;
import com.aratech.lectoras.adapters.FloorAdapter.OnRegChangedListener;
import com.aratech.lectoras.adapters.SpinnerConceptAdapter;
import com.aratech.lectoras.adapters.SpinnerPropertyAdapter;
import com.aratech.lectoras.adapters.SpinnerTypeAdapter;
import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg10;
import com.aratech.lectoras.beans.Reg20Comunidades;
import com.aratech.lectoras.beans.Reg40Pisos;
import com.aratech.lectoras.beans.Reg60Lecturas;
import com.aratech.lectoras.beans.Reg99TipoLectura;
import com.aratech.lectoras.data.RegDataBase;
import com.aratech.lectoras.data.classes.Keypad;
import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg20Constants;
import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg40Constants;
import com.aratech.radio.RadioDataBaseHelper;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.aratech.lectoras.R.id.dialogAntenaReading;

public class ReadingActivity extends Activity implements OnRegChangedListener,
		OnClickListener, OnEndRadioReadListener {

	public RegDataBase database;
	private CustomSpinnerSelection spinnerPropertys, spinnerTypes,
			spinnerConcepts;
	private ListView lvFloors;
	Reg99TipoLectura currentSelectedType;
	private View selectedView;
	private Reg40Pisos selectedReg;
	private Reg10 currentedSelectedConcept;
	private Reg60Lecturas currentReading;
	private Reg20Comunidades currentCommunity;
	TextView tvPreviousRead, tvDifference, tvCurrentRead, tvAusente, tvCover,
			tvPreviousType;
	private CheckBox cbAusente;

	private Button btnSave, btnRadio, btnTarifas, btnMac, btnInfo;

	private String macAberingAddress,macWmbusAddress, adminName;

	LinearLayout layoutKeypad;

	Keypad keypad;
	private int previousScreenSelectedCommunity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readings_selection_keypad);

		database = new RegDataBase(this);

		previousScreenSelectedCommunity = getIntent().getExtras().getInt(
				Reg20Constants.COMMUNITY);

		tvCover = (TextView) findViewById(R.id.tvCover);

		layoutKeypad = (LinearLayout) findViewById(R.id.layout_keypadGlobal);
		layoutKeypad.setVisibility(View.INVISIBLE);
		tvAusente = (TextView) findViewById(R.id.tvAusente);
		cbAusente = (CheckBox) findViewById(R.id.cbAusente);
		cbAusente.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					tvAusente.setTextColor(getResources().getColor(
							R.color.state_red));
				} else {
					tvAusente.setTextColor(getResources().getColor(
							android.R.color.black));
				}
			}
		});
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);

		btnRadio = (Button) findViewById(R.id.btnRadio);
		btnRadio.setOnClickListener(this);

		btnTarifas = (Button) findViewById(R.id.btnTarifas);
		btnTarifas.setOnClickListener(this);

		btnMac = (Button) findViewById(R.id.btnMac);
		btnMac.setOnClickListener(this);

		btnInfo = (Button) findViewById(R.id.btnInfo);
		btnInfo.setOnClickListener(this);

		loadMacAddress();

		spinnerPropertys = (CustomSpinnerSelection) findViewById(R.id.spinnerComunity);
		spinnerConcepts = (CustomSpinnerSelection) findViewById(R.id.spinnerConcept);
		spinnerTypes = (CustomSpinnerSelection) findViewById(R.id.spinnerType);
		lvFloors = (ListView) findViewById(R.id.lvFloors);
		tvPreviousRead = (TextView) findViewById(R.id.tvPreviousRead);
		tvPreviousType = (TextView) findViewById(R.id.tvPreviousType);
		tvDifference = (TextView) findViewById(R.id.tvDifference);
		tvCurrentRead = (TextView) findViewById(R.id.tvCurrentRead);
		keypad = new Keypad(this, tvCurrentRead) {
			@Override
			public void onKeypadValueChange(String newValue) {
				if (currentReading != null) {
					currentReading.currentRead = newValue;
					setDifference();
				}

			}
		};
		new AsyncInitLoadSpinners().execute();

	}

	protected void setDifference() {
		try {
			if (currentReading != null) {
				double realCurrentRead = Double
						.valueOf(currentReading.currentRead);
				DecimalFormat df = new DecimalFormat("##");
				df.setRoundingMode(RoundingMode.DOWN);
				int cRead = Integer.valueOf(df.format(realCurrentRead));
				int pRead = Integer.valueOf(currentReading.previousRead);

				int result = cRead - pRead;
				tvDifference.setText(String.valueOf(result));
				if (result < 0) {
					tvDifference.setTextColor(getResources().getColor(
							R.color.state_red));
				} else {
					tvDifference.setTextColor(getResources().getColor(
							android.R.color.black));
				}
			}
		} catch (Exception ex) {
			Log.e("SET_DIFFERENCE", "Error al calcular diferencia");
		}

	}

	private void loadSpinnerPropertys() {
		ArrayList<Reg> communitys = database.getCommunitys();
		spinnerPropertys.setAdapter(new SpinnerPropertyAdapter(
				ReadingActivity.this, communitys));

		spinnerPropertys
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						Reg20Comunidades selectedCommunity = (Reg20Comunidades) view
								.getTag();
						if (currentCommunity == null
								|| currentCommunity.getCommunityNumber() != selectedCommunity
										.getCommunityNumber()) {
							setCurrentSelectedCommunity(selectedCommunity);

						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}

				});

		if (spinnerPropertys.getAdapter().getCount() > 0) {
			for (int i = 0; i < spinnerPropertys.getAdapter().getCount(); i++) {
				Reg20Comunidades item = (Reg20Comunidades) spinnerPropertys
						.getAdapter().getItem(i);
				if (item.getCommunityNumber() == previousScreenSelectedCommunity) {
					spinnerPropertys.setSelection(i);
					break;
				}
			}
		}

	}

	private View.OnTouchListener spinnerOnTouchScrollToFirst = new View.OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				try {

					Spinner selectedSpinner = (Spinner) v;
					int previousSelectedPosition = Integer
							.valueOf(selectedSpinner.getSelectedItemPosition());
					// selectedSpinner.setSelection(previousSelectedPosition);

				} catch (Exception ex) {

				}
			}
			return false;
		}
	};

	private void setCurrentSelectedCommunity(Reg20Comunidades selectedCommunity) {
		currentCommunity = selectedCommunity;
		new AsyncTask<Void, Void, Void>() {

			AlertDialog waitDialog;
			TextView textDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				waitDialog = new AlertDialog.Builder(ReadingActivity.this)
						.create();
				View v = getLayoutInflater()
						.inflate(R.layout.dialog_wait, null);
				waitDialog.setView(v);
				textDialog = (TextView) v.findViewById(R.id.tvDialog);
				textDialog.setText("Cargando pisos...");
				waitDialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						loadFloorList();
					}

				});

				return null;
			}

			protected void onPostExecute(Void result) {
				waitDialog.dismiss();
				tvCover.setVisibility(View.VISIBLE);
				layoutKeypad.setVisibility(View.INVISIBLE);
			};
		}.execute();

	}

	private void loadFloorList() {
		ArrayList<Reg> selectedFloors = database
				.getFloorsByCommunityAndProperty(
						currentCommunity.getCommunityNumber(),
						currentCommunity.getPropertyNumber(),
						new String[] { Reg40Constants.FLOOR });
		if (lvFloors.getAdapter() != null)
			((FloorAdapter) lvFloors.getAdapter()).reloadItems(selectedFloors);
		else
			lvFloors.setAdapter(new FloorAdapter(ReadingActivity.this,
					selectedFloors, ReadingActivity.this));
	}

	private void loadSpinnerTypes() {
		ArrayList<Reg> types = database.getReadingTypes();
		spinnerTypes.setAdapter(new SpinnerTypeAdapter(ReadingActivity.this,
				types));
		spinnerTypes.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Reg99TipoLectura selectedType = (Reg99TipoLectura) view
						.getTag();/*
								 * if (selectedType != null &&
								 * !selectedType.readCode
								 * .equals(Reg99TipoLectura.EMPTY_TYPE)) {
								 * currentSelectedType = selectedType; }else{
								 * currentSelectedType = null; }
								 */

				currentSelectedType = selectedType;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		// spinnerTypes.setOnTouchListener(spinnerOnTouchScrollToFirst);

		if (spinnerTypes.getAdapter().getCount() > 0) {
			spinnerTypes.setSelection(0);
		}
	}

	private void loadSpinnerConcepts(ArrayList<Reg> concepts) {

		spinnerConcepts.setAdapter(new SpinnerConceptAdapter(
				ReadingActivity.this, concepts));
		spinnerConcepts.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Reg10 selectedType = (Reg10) view.getTag();
				currentedSelectedConcept = selectedType;
				loadCurrentReading();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		// spinnerConcepts.setOnTouchListener(spinnerOnTouchScrollToFirst);

		if (spinnerConcepts.getAdapter().getCount() > 0) {
			spinnerConcepts.setSelection(0);
		}

	}

	private class AsyncInitLoadSpinners extends AsyncTask<Void, Void, Void> {

		AlertDialog waitDialog;
		TextView textDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			waitDialog = new AlertDialog.Builder(ReadingActivity.this).create();
			View v = getLayoutInflater().inflate(R.layout.dialog_wait, null);
			waitDialog.setView(v);
			textDialog = (TextView) v.findViewById(R.id.tvDialog);
			textDialog.setText("Cargando tipos...");
			waitDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					loadSpinnerPropertys();
					loadSpinnerTypes();
					ArrayList<Reg> concepts = database
							.getConcepts(previousScreenSelectedCommunity);
					loadSpinnerConcepts(concepts);
				}
			});

			return null;
		}

		protected void onPostExecute(Void result) {
			waitDialog.dismiss();
		};
	}

	@Override
	public void onSelected(Reg reg, View view)
    {
		/*
		 * if (selectedView != null) {
		 * selectedView.setBackgroundColor(getResources().getColor(
		 * android.R.color.transparent)); }
		 */

		tvCover.setVisibility(View.GONE);
		layoutKeypad.setVisibility(View.VISIBLE);

		selectedReg = (Reg40Pisos) reg;
		ArrayList<Reg> conceptsForSelectedReg = database.getConcepts(
				Integer.valueOf(selectedReg.community),
				Integer.valueOf(selectedReg.property),
				Integer.valueOf(selectedReg.owner));

		loadSpinnerConcepts(conceptsForSelectedReg);

		/*
		 * view.setBackgroundResource(R.drawable.azul_alpha_marco_nine);//.
		 * setBackgroundColor(getResources().getColor(R.color.light_green));
		 */
		selectedView = view;

		/*if (spinnerTypes.getAdapter().getCount() > 0) {
			spinnerTypes.setSelection(0);
		}
*/
		if (spinnerConcepts.getAdapter().getCount() > 0)
        {
			spinnerConcepts.setSelection(0);
		}

		changeBtnInfoColor();
		//loadCurrentReading();

	}

	private void loadCurrentReading() {
		if (this.selectedReg != null && this.currentedSelectedConcept != null) {
			keypad.setCurrentValue("0");
			currentReading = database.getReading(selectedReg,
					currentedSelectedConcept);

			if (currentReading != null) {
				loadExistentReading(currentReading);
			} else {
				Toast.makeText(
						ReadingActivity.this,
						"No se han encontrado lecturas para el piso y el concepto seleccionados",
						Toast.LENGTH_LONG).show();
			}
		} else if (this.selectedReg == null
				&& this.currentedSelectedConcept != null) {
			Toast.makeText(ReadingActivity.this, "Seleccione un piso",
					Toast.LENGTH_LONG).show();
		} else if (this.selectedReg != null
				&& this.currentedSelectedConcept == null) {
			Toast.makeText(ReadingActivity.this, "Seleccione un concepto",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(ReadingActivity.this,
					"Seleccione un concepto y un piso", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void loadExistentReading(Reg60Lecturas reg) {
		if (reg != null) {
			tvPreviousRead.setText(reg.previousRead);
			if (reg.previousType != null
					&& reg.previousType.trim().length() > 0) {
				tvPreviousType.setText(reg.previousType);
			} else {
				tvPreviousType.setText("-");
			}

			if (reg.currentRead != null && !reg.currentRead.equals("")) {
				tvCurrentRead.setText(reg.currentRead);
				keypad.setCurrentValue(reg.currentRead);
				try {
					double realCurrentRead = Double.valueOf(reg.currentRead);
					DecimalFormat df = new DecimalFormat("##");
					df.setRoundingMode(RoundingMode.DOWN);
					int cRead = Integer.valueOf(df.format(realCurrentRead));
					int pRead = Integer.valueOf(reg.previousRead);

					int result = cRead - pRead;
					tvDifference.setText(String.valueOf(result));
					if (result < 0) {
						tvDifference.setTextColor(getResources().getColor(
								R.color.state_red));
					} else {
						tvDifference.setTextColor(getResources().getColor(
								android.R.color.black));
					}

					if (Integer.valueOf(reg.currentRead) == 0) {
						tvCurrentRead.setText("0");
					}

				} catch (Exception ex) {
					Log.e("CALCULOS", "Error al calcular diferencias");
				}
			} else {
				tvDifference.setText("0");
				tvDifference.setTextColor(getResources().getColor(
						android.R.color.black));
				tvCurrentRead.setText("0");

			}

			if (reg.haveRead != null && !reg.haveRead.equals("")) {
				int readed = Integer.valueOf(reg.haveRead);
				cbAusente.setChecked(readed == 1);
			}

			if (reg.type != null && !reg.type.equals("")) {

				int nType = Integer.valueOf(reg.type);
				if(spinnerTypes.getAdapter() != null){
					SpinnerTypeAdapter typeAdapter = (SpinnerTypeAdapter) spinnerTypes.getAdapter();
					int selectedPosition = typeAdapter.getPositionByReadCodeOfType(nType);
					if(selectedPosition >= 0){
						spinnerTypes.setSelection(selectedPosition);
					}else if (spinnerTypes.getAdapter().getCount() > 0) {
						spinnerTypes.setSelection(0);
					}else{
						Toast.makeText(ReadingActivity.this, "Error al cargar los Tipos", Toast.LENGTH_LONG).show();
					}
				}		
				
			} else if (spinnerTypes.getAdapter().getCount() > 0) {
				spinnerTypes.setSelection(0);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSave:
			if (cbAusente.isChecked()) {
				clickSave();
			} else {
				if (!checkCurrentBigThanPrevious()) {
					new DialogWarning(ReadingActivity.this,
							getString(R.string.advertencia_lectura_menor),
							new DialogWarning.OnAcceptWarningListener() {

								@Override
								public void onAccept() {
									clickSave();

								}
							}).show();
				} else if (!checkAverage()) {
					new DialogWarning(ReadingActivity.this,
							getString(R.string.advertencia_lectura_setenta),
							new DialogWarning.OnAcceptWarningListener() {

								@Override
								public void onAccept() {
									clickSave();

								}
							}).show();
				} else {

					clickSave();
				}
			}
			break;
		case R.id.btnRadio:
            showChooseAntennaDialog();
			break;
		case R.id.btnTarifas:
			clickTarifas();
			break;
		case R.id.btnMac:
			showMACDialog();
			break;
		case R.id.btnInfo:
			if (selectedReg != null) {
				showInfoDialog();
			}
		}

	}

	private boolean checkCurrentBigThanPrevious() {
		try {
			if (cbAusente.isChecked()) {
				return true;
			}
			if (currentReading == null || currentReading.currentRead == null
					|| currentReading.currentRead.equals("")) {
				return false;
			}

			if (Integer.valueOf(currentReading.currentRead) < Integer
					.valueOf(currentReading.previousRead)) {
				return false;
			}

			return true;
		} catch (Exception ex) {
			Log.e("CHECK CURRENT BIG", String.valueOf(ex));
			return true;
		}
	}

	private boolean checkAverage() {
		try {
			if (cbAusente.isChecked()) {
				return true;
			}

			if (currentReading == null || currentReading.previousRead == null
					|| currentReading.previousRead.equals("")
					|| Integer.valueOf(currentReading.previousRead) <= 0) {
				return true;
			}
			int average = Integer.valueOf(currentReading.previousRead);
			int difference = Integer.valueOf(tvDifference.getText().toString());
			if (difference > (average + (average * 0.7))) {
				return false;
			}

			return true;
		} catch (Exception ex) {
			Log.e("CHECK AVERAGE", String.valueOf(ex));
			return true;
		}
	}

	private void clickTarifas() {
		if (this.selectedReg != null) {
			ArrayList<Reg> tarifas = database.getTarifas(
					currentCommunity.getCommunityNumber(),
					currentCommunity.getPropertyNumber(),
					Integer.valueOf(selectedReg.owner));
			if (tarifas != null && tarifas.size() > 0) {
				DialogTarifas dialog = new DialogTarifas(this, tarifas);
				dialog.show();
			} else {
				Toast.makeText(this, "No se han encontrado tarifas",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, "Seleccione un piso", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void clickRadio(String antenaMacAddress, String type) {
        DialogRadio dialog = new DialogRadio(this, antenaMacAddress,type,
                currentReading, this);
        dialog.show();
	}

	private void clickSave() {
		if (selectedReg != null && currentReading != null
				&& currentedSelectedConcept != null) {
			if ((currentReading.currentRead != null && !currentReading.currentRead
					.equals("")) || cbAusente.isChecked()) {

				if (cbAusente.isChecked()) {
					currentReading.haveRead = "1";
				} else {
					currentReading.haveRead = "0";
				}

				selectedReg.viewed = "1";
				if (currentSelectedType != null) {
					if (currentSelectedType.readCode
							.equals(Reg99TipoLectura.EMPTY_TYPE)) {
						currentReading.type = "";
					} else {
						currentReading.type = currentSelectedType.readCode;
					}
					
				}

				database.updateReg(selectedReg);
				currentReading.setSaved(true);
				database.updateReg(currentReading);

				updateSelectedColorState();

			} else {
				Toast.makeText(this,
						"Introduzca una lectura válida o marque cómo ausente",
						Toast.LENGTH_LONG).show();
			}
		} else if (selectedReg == null && currentReading != null
				&& currentedSelectedConcept != null) {
			Toast.makeText(this, "No hay ningún piso seleccionado",
					Toast.LENGTH_LONG).show();
		} else if (selectedReg != null && currentReading == null
				&& currentedSelectedConcept != null) {
			Toast.makeText(this, "No se ha encontrado ninguna lectura",
					Toast.LENGTH_LONG).show();
		} else if (selectedReg != null && currentReading != null
				&& currentedSelectedConcept == null) {
			Toast.makeText(this, "No hay ningún concepto seleccionado",
					Toast.LENGTH_LONG).show();
		}
	}

	private void updateSelectedColorState() {
		try {
			if (selectedReg != null) {
				int updatedState = database.getReadingsState(selectedReg);

				ImageView imgState = (ImageView) selectedView
						.findViewById(R.id.imgReadingsState);

				switch (updatedState) {
				case -1:
					imgState.setImageDrawable(getResources().getDrawable(
							R.drawable.state_red));
					break;
				case 0:
					imgState.setImageDrawable(getResources().getDrawable(
							R.drawable.state_yellow));
					break;
				case 1:
					imgState.setImageDrawable(getResources().getDrawable(
							R.drawable.state_green));
					break;
				default:
					imgState.setImageDrawable(getResources().getDrawable(
							R.drawable.state_grey));
				}
			}
		} catch (Exception ex) {
			Log.e("STATE IMAGE UPDATE", String.valueOf(ex));
		}
	}

	private void showMACDialog() {
		final AlertDialog readingDialog = new AlertDialog.Builder(this)
				.create();
		View v = getLayoutInflater().inflate(R.layout.dialog_mac, null);
		readingDialog.setView(v);

		final EditText aberingEdit = (EditText) v.findViewById(R.id.mac_one_edit);
        final EditText wmbusEdit = (EditText) v.findViewById(R.id.mac_two_edit);
		if (macAberingAddress != null && !macAberingAddress.equals("")) {
			aberingEdit.setText(macAberingAddress);
		}
        if(macWmbusAddress !=null && !macWmbusAddress.equals("")){
            wmbusEdit.setText(macWmbusAddress);
        }

		Button btnAceptar = (Button) v.findViewById(R.id.btnOk);
		btnAceptar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String aberingText = aberingEdit.getText().toString();
                String wmbusText = wmbusEdit.getText().toString();
                if(aberingText.equals("") && wmbusText.equals("")){
                    displayMacErrorToast("Introduce una MAC válida o cancela");
                    readingDialog.dismiss();
                    return;
                }
                String macAbering="";
                String macWmbus = "";
                if(!aberingText.equals("")) {
                    if (aberingText.replace(":", "").length() == 12) {

                        if (aberingText.length() == 17) {
                            macAbering = aberingText;
                        } else {
                            macAbering = getFormattedMac(aberingText);
                        }
                    } else {
                        displayMacErrorToast(getString(R.string.abering_mac_inválido));
                        return;
                    }
                }
                if(!wmbusText.equals("")) {
                    if (wmbusText.replace(":", "").length() == 12) {

                        if (wmbusText.length() == 17) {
                            macWmbus = wmbusText;
                        } else {
                            macWmbus = getFormattedMac(wmbusText);
                        }
                    } else {
                        displayMacErrorToast(getString(R.string.wmbus_mac_inválido));
                        return;
                    }
                }
                changeMacAddress(macAbering, "abering");
                changeMacAddress(macWmbus, "wmbus");

                readingDialog.dismiss();
            }
		});
		Button btnCancelar = (Button) v.findViewById(R.id.btnCancel);
		btnCancelar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				readingDialog.dismiss();
			}
		});
		readingDialog.show();
	}

    private String getFormattedMac(String unformattedMac){
        StringBuilder macBuilder = new StringBuilder();
        for (int i = 1; i <= 12; i += 2) {
            macBuilder.append(unformattedMac.substring(i - 1, i + 1));
            if (i + 2 < 12)
                macBuilder.append(":");
        }
        return macBuilder.toString();
    }

    private void displayMacErrorToast(String message){
        Toast.makeText(ReadingActivity.this,
               message ,
                Toast.LENGTH_LONG).show();
    }

	private void showChooseAntennaDialog(){
		final AlertDialog chooseAntennaDialog = new AlertDialog.Builder(this).create();
		View v = getLayoutInflater().inflate(R.layout.dialog_antena_chooser,null);
		chooseAntennaDialog.setView(v);

        final RadioButton aberingRadio = (RadioButton) v.findViewById(R.id.dialogAntenaAberingRadio);
        final RadioButton wmbusRadio = (RadioButton) v.findViewById(R.id.dialogAntenaWmbusRadio);
        aberingRadio.setChecked(true);

        Button startReadingButton = (Button) v.findViewById(dialogAntenaReading);
        startReadingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aberingRadio.isChecked()){
                    if(macAberingAddress!=null && !macAberingAddress.equals("")){
                        clickRadio(macAberingAddress,getString(R.string.antena_1_abering));
                        Log.d("AlcalaMac",macAberingAddress +" abering");
                    }else{
                        displayMacErrorToast(getString(R.string.abering_address_no_existen));
                        return;
                    }
                }
                if(wmbusRadio.isChecked()){
                    if(macWmbusAddress!=null && !macWmbusAddress.equals("")){
                        clickRadio(macWmbusAddress,getString(R.string.antena_2_wmbus));
                        Log.d("AlcalaMac",macAberingAddress +" wmbus");
                    }else{
                        displayMacErrorToast(getString(R.string.wmbus_address_no_existen));
                        return;
                    }
                }
                chooseAntennaDialog.dismiss();
            }
        });

        Button cancelButton = (Button) v.findViewById(R.id.dialogAntenaCancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseAntennaDialog.dismiss();
            }
        });

        chooseAntennaDialog.show();

	}

	private void showInfoDialog()
    {
		final AlertDialog infoDialog = new AlertDialog.Builder(this).create();
		View v = getLayoutInflater().inflate(R.layout.dialog_info, null);
		infoDialog.setView(v);
		final EditText inputRead = (EditText) v.findViewById(R.id.etInfo);

		if (selectedReg.readerExtension != null && !selectedReg.readerExtension.equals(""))
        {
			inputRead.setText(selectedReg.readerExtension);
		}

		TextView tvAdmin = (TextView) v.findViewById(R.id.tvAdmin);
		tvAdmin.setText(currentCommunity.adminName);

		TextView tvMedia = (TextView) v.findViewById(R.id.tvMedia);
		tvMedia.setText(currentReading.average);

		TextView tvFecha = (TextView) v.findViewById(R.id.tvFecha);
		tvFecha.setText(currentReading.date);

		Button btnAceptar = (Button) v.findViewById(R.id.btnOk);
		btnAceptar.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
            {
				String input = inputRead.getText().toString();

				selectedReg.readerExtension = input;
				database.asyncUpdateReg(selectedReg, ReadingActivity.this);
				changeBtnInfoColor();
				infoDialog.dismiss();

			}
		});

		Button btnCancelar = (Button) v.findViewById(R.id.btnCancel);
		btnCancelar.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
            {
				infoDialog.dismiss();
			}
		});

		infoDialog.show();
	}

	private void changeMacAddress(String newMacAddress,String type) {
		SharedPreferences prefs = getSharedPreferences("MacPrefs",
				Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();
        if(!newMacAddress.equals("") && type.equals("abering")) {
            editor.putString("MAC_abering", newMacAddress);
            macAberingAddress = newMacAddress;
        }
        if(!newMacAddress.equals("") && type.equals("wmbus")){
            editor.putString("MAC_wmbus",newMacAddress);
            macWmbusAddress = newMacAddress;
        }
		editor.apply();
	}

	private void loadMacAddress() {
		SharedPreferences prefs = getSharedPreferences("MacPrefs",
				Context.MODE_PRIVATE);

		macAberingAddress = prefs.getString("MAC_abering","");
        macWmbusAddress = prefs.getString("MAC_wmbus","");
	}

	private void changeBtnInfoColor() {
		if (selectedReg != null) {
			if (selectedReg.readerExtension != null
					&& !selectedReg.readerExtension.equals("")) {
				btnInfo.setTextColor(getResources().getColor(R.color.state_red));
				btnInfo.setText("INFO");
			} else {
				btnInfo.setTextColor(getResources().getColor(
						android.R.color.black));
				btnInfo.setText("Info");
			}
		}
	}

	@Override
	public void radioEnd(int result, boolean marcar) {
		switch (result) {
		case DialogRadio.RESULT_OK:
			new AsyncUpdateRadioRegs().execute(marcar);
			//K.O.B. 1/4/14 Original call without call of the method updateSelectedColorState
			updateSelectedColorState();
			break;
		case DialogRadio.RESULT_NO_DATA:
			Toast.makeText(this,
					"No se han encontrado datos para la lectura seleccionada",
					Toast.LENGTH_LONG).show();
			break;
		case DialogRadio.RESULT_NO_DATABASE:
			Toast.makeText(
					this,
					"No se ha encontrado la base de datos de lecturas de radio",
					Toast.LENGTH_LONG).show();
			break;
		case DialogRadio.RESULT_EXCEPTION:
			Toast.makeText(this,
					"Ha habido un problema y no se pueden cargar los datos",
					Toast.LENGTH_LONG).show();
			break;
		}

	}

	private class AsyncUpdateRadioRegs extends
			AsyncTask<Boolean, Void, Integer> {

		AlertDialog waitDialog;
		TextView textDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			waitDialog = new AlertDialog.Builder(ReadingActivity.this).create();
			View v = getLayoutInflater().inflate(R.layout.dialog_wait, null);
			waitDialog.setView(v);
			textDialog = (TextView) v.findViewById(R.id.tvDialog);
			textDialog.setText("Actualizando datos...");
			waitDialog.show();
		}

		@Override
		protected Integer doInBackground(Boolean... params) {

			ArrayList<Reg> readingsWithRadio = database
					.getReadingsWithRadioByCommunity(currentCommunity);
			boolean check = params[0];
			int counter = 0;

			for (int i = 0; i < readingsWithRadio.size(); i++) {
				try {
					Reg60Lecturas reading = (Reg60Lecturas) readingsWithRadio
							.get(i);

					int radioValue = RadioDataBaseHelper
							.getReadforSerial(reading.serial);

					switch (radioValue) {
					case -1:
					case -2:
						break;
					default:
						double valueOnCubicMetters = radioValue / 1000;
						DecimalFormat threeDecFormat = new DecimalFormat(
								"#.###");
						reading.currentRead = threeDecFormat
								.format(valueOnCubicMetters);
						if (check) {
							reading.haveRead = "1";
						} else {
							reading.haveRead = "0";
						}
						reading.saved = true;
						database.updateReg(reading);
						counter++;
					}
				} catch (Exception ex) {
					Log.e("UPDATE READINGS", String.valueOf(ex));
				}
			}

			return counter;
		}

		protected void onPostExecute(Integer result) {
			int counter = result;
			StringBuilder resultToastMessage = new StringBuilder();
			if (counter > 0) {
				RadioDataBaseHelper.cleanReadingsDataBase();
				loadFloorList();

				if (counter > 1) {
					resultToastMessage.append("Actualizados con éxito ")
							.append(counter).append(" registros");
				} else {
					resultToastMessage
							.append("Actualizado con éxito 1 registro");
				}
			} else {
				resultToastMessage
						.append("No se ha actualizado ningún registro con las lecturas de Radio");
			}
			waitDialog.dismiss();
			Toast.makeText(ReadingActivity.this, resultToastMessage,
					Toast.LENGTH_LONG).show();
		};
	}

}
