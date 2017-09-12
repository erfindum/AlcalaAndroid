package com.aratech.lectoras;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SplashActivity extends Activity {

	protected static final long WAIT_DEFAULT = 1500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				synchronized (this) {
					try{
						wait(WAIT_DEFAULT);
					}catch(Exception ex){
						Log.e("LECTORAS - SPLASH", ex.toString());
					}
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				startMainActivity();
				finish();
			};
		}.execute();
	}

	private void startMainActivity() {
		Intent newIntent = new Intent(this, MainActivity.class);
		startActivity(newIntent);
	}

}
