package com.aratech.lectoras;

import com.aratech.lectoras.beans.Reg60Lecturas;
import com.aratech.radio.RadioDataBaseHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class DialogRadio extends Dialog implements
		android.view.View.OnClickListener {

	private String currentRadioMac, antennaType;
	private Reg60Lecturas currentReg;
	private OnEndRadioReadListener listener;
	private Activity mContext;

	Button btnAccept, btnCancel;
	TextView tvMac, tvUpdate, antennaInfo;
	RadioButton rbMarcar, rbNoMarcar;
	
	private boolean endAll = false, isCancelled;
	
	public final static int RESULT_OK = 1, RESULT_NO_DATA = 2,
			RESULT_EXCEPTION = -1, RESULT_NO_DATABASE = -2;

	public DialogRadio(Activity context, String mac,String antennaType,
			Reg60Lecturas readToComplete, OnEndRadioReadListener endListener) {
		super(context);

		mContext = context;
		
		setContentView(R.layout.dialog_radio);
		setTitle("RADIO");
		currentRadioMac = mac;
		listener = endListener;

		tvMac = (TextView) findViewById(R.id.tvMac);
		tvMac.setText(mac);

		antennaInfo = (TextView) findViewById(R.id.antennaInfoType);
		this.antennaType = antennaType;
		if(antennaType.equals("abering")) {
			antennaInfo.setText(context.getString(R.string.antena_1_abering));
		}
		if(antennaType.equals("wmbus")){
			antennaInfo.setText(context.getString(R.string.antena_2_wmbus));
		}

		tvUpdate = (TextView) findViewById(R.id.tvUpdate);

		rbMarcar = (RadioButton) findViewById(R.id.rbMarcar);
		rbNoMarcar = (RadioButton) findViewById(R.id.rbDesMarcar);
		
		btnAccept = (Button) findViewById(R.id.btnOk);
		btnAccept.setOnClickListener(this);

		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);

	}

	@Override
	public void show() {
		super.show();
		startRead();
		startUpdateThread();
	}

	private void startUpdateThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				do {
					try {
						synchronized (this) {
							int count = RadioDataBaseHelper.getReadingsCount(antennaType
									,getContext().getContentResolver());
							setTvUpdateText(String.valueOf(count));
							wait(3000);
						}
					} catch (Exception ex) {
						Log.e("Error al actualizar lecturas", String.valueOf(ex));
					}
				} while (!endAll);
				onUpdateThreadEnd();
			}
		}).start();
	}
	
	private void setTvUpdateText(final String newText) {
		mContext.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				tvUpdate.setText(newText);
			}
		});
	}

	private boolean getMarcar(){
		return rbMarcar.isChecked();
	}
	
	private void onUpdateThreadEnd(){
		mContext.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(!isCancelled){
					try {
                        int count = RadioDataBaseHelper
                                .getReadingsCount(antennaType,getContext().getContentResolver());
                        DialogRadio.this.dismiss();
                        if (count > 0) {
                            listener.radioEnd(RESULT_OK, getMarcar());
                        } else {
                            listener.radioEnd(RESULT_NO_DATA, getMarcar());
                        }
					} catch (Exception ex) {
						DialogRadio.this.dismiss();
						listener.radioEnd(RESULT_EXCEPTION, getMarcar());
					}
				}else{
					DialogRadio.this.dismiss();
				}
				
			}
		});

		
	}
	private void startRead() {
		Intent intent = new Intent();
		Bundle params = new Bundle();
		if(antennaType.equals("abering")){
			params.putString("MAC", currentRadioMac);
		}
		if(antennaType.equals("wmbus")){
			params.putString("MAC_WMBUS", currentRadioMac);
		}
		intent.putExtras(params);
		intent.setClassName("com.mirakontaservice",
				"com.mirakontaservice.MKService");
		getContext().startService(intent);
	}

	private void stopRead() {
		Intent intent = new Intent();
		intent.setClassName("com.mirakontaservice",
				"com.mirakontaservice.MKService");
		getContext().stopService(intent);
	}

	private void clickAccept() {		
			stopRead();
			isCancelled = false;
			endAll = true;			
	}

	private void clickCancel() {
		stopRead();
		isCancelled = true;
		endAll = true;
	}

	@Override
	public void onBackPressed() {
	}

	public interface OnEndRadioReadListener {
		public abstract void radioEnd(int result, boolean marcar);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOk) {
			clickAccept();
		} else if (v.getId() == R.id.btnCancel) {
			clickCancel();
		}

	}
}
