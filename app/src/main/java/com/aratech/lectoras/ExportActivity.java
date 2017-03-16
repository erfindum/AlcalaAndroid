package com.aratech.lectoras;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aratech.lectoras.adapters.ComunityAdapter;
import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg.RegType;
import com.aratech.lectoras.beans.Reg20Comunidades;
import com.aratech.lectoras.data.RegDataBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExportActivity extends Activity implements OnClickListener {

	Button btnExport;
	RadioButton rbExportPart, rbExportAll;
	RegDataBase dataBase;
	ListView lvComunities;
	ArrayList<Reg20Comunidades> selectedCommunitys;
	ArrayList<Reg> regsToLoad;
	File exportDirectory;
	public static String DEFAULT_FOLDER_NAME = "EXPORTACION_CONTADORES";
	EditText etFilename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export);

		btnExport = (Button) findViewById(R.id.btnExportar);
		btnExport.setOnClickListener(this);

		dataBase = new RegDataBase(this);

		etFilename = (EditText) findViewById(R.id.etFileName);
		
		lvComunities = (ListView) findViewById(R.id.lvComunities);

		rbExportPart = (RadioButton) findViewById(R.id.rbPartes);
		rbExportPart.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				lvComunities.setEnabled(isChecked);

			}
		});

		rbExportAll = (RadioButton) findViewById(R.id.rbTodo);
		exportDirectory = new File(Environment.getExternalStorageDirectory(),
				DEFAULT_FOLDER_NAME);
		if(!exportDirectory.exists()){
			exportDirectory.mkdir();
		}
		ArrayList<Reg> communitys = dataBase
				.getRegsFromType(Reg.RegType.REG20_COMUNIDADES);
		lvComunities.setAdapter(new ComunityAdapter(ExportActivity.this,
				communitys));
		lvComunities.setClickable(false);
	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnExportar:
			if (etFilename.getText().toString() != null
					&& !etFilename.getText().toString().equals("")) {
				startAsyncExport();
			}else{
				Toast.makeText(this, "Introduce un nombre para el fichero", Toast.LENGTH_LONG).show();
			}
			break;
		}

	}

	private void startAsyncExport() {
		HashMap<RegType, ArrayList<Reg>> endMap;
		regsToLoad = new ArrayList<Reg>();
		if (rbExportAll.isChecked()) {
			endMap = dataBase.getRegsForExport();
		} else {
			selectedCommunitys = ((ComunityAdapter) lvComunities.getAdapter())
					.getSelectedItems();
			
			endMap = new HashMap<Reg.RegType, ArrayList<Reg>>();
			for (Reg20Comunidades comunity : selectedCommunitys) {
				HashMap<RegType, ArrayList<Reg>> newMap = dataBase
						.getRegsForExport(comunity.getCommunityNumber());
				ArrayList<Reg> comunidades = newMap
						.get(RegType.REG20_COMUNIDADES);
				if (endMap.containsKey(RegType.REG20_COMUNIDADES)) {
					for (Reg item : comunidades) {
						endMap.get(RegType.REG20_COMUNIDADES).add(item);
					}
				} else {
					endMap.put(RegType.REG20_COMUNIDADES, comunidades);
				}

				ArrayList<Reg> pisos = newMap.get(RegType.REG40);
				if (endMap.containsKey(RegType.REG40)) {
					for (Reg item : pisos) {
						endMap.get(RegType.REG40).add(item);
					}
				} else {
					endMap.put(RegType.REG40, pisos);
				}

				ArrayList<Reg> lecturas = newMap.get(RegType.REG60);
				if (endMap.containsKey(RegType.REG60)) {
					for (Reg item : lecturas) {
						endMap.get(RegType.REG60).add(item);
					}
				} else {
					endMap.put(RegType.REG60, lecturas);
				}

			}
		}

		regsToLoad.addAll(endMap.get(RegType.REG20_COMUNIDADES));
		regsToLoad.addAll(endMap.get(RegType.REG40));
		regsToLoad.addAll(endMap.get(RegType.REG60));
		;
		if (regsToLoad != null && regsToLoad.size() > 0) {
			new AsyncWriteFile().execute();
		} else {
			Toast.makeText(ExportActivity.this, R.string.no_entradas,
					Toast.LENGTH_LONG).show();
		}
	}

	private class AsyncWriteFile extends AsyncTask<String, Void, String> {

		AlertDialog waitDialog;
		TextView textDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			waitDialog = new AlertDialog.Builder(ExportActivity.this).create();
			View v = getLayoutInflater().inflate(R.layout.dialog_wait, null);
			waitDialog.setView(v);
			textDialog = (TextView) v.findViewById(R.id.tvDialog);
			textDialog.setText("Exportando registros...");
			waitDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try {
				StringBuilder fileTextBuilder = new StringBuilder();
				for (Reg regToExport : regsToLoad) {
					fileTextBuilder.append(regToExport.getExportLine());
					fileTextBuilder.append("\n");
				}
				File file = new File(exportDirectory, etFilename.getText().toString() + ".txt");
				FileOutputStream fop = new FileOutputStream(file);

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				// get the content in bytes
				byte[] contentInBytes = fileTextBuilder.toString().getBytes();

				fop.write(contentInBytes);
				fop.flush();
				fop.close();
				result = new StringBuilder("Exportados con éxito ").append(regsToLoad.size()).append(" registros").toString();
			} catch (FileNotFoundException e) {

				result = "Error al generar el archivo de salida";
			} catch (IOException e) {
				result = "Error al manipular el archivo de salida";
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			waitDialog.dismiss();
			if(result == null){
				result = "Resultado desconocido";
			}
			Toast.makeText(ExportActivity.this, result, Toast.LENGTH_LONG).show();
		}
	}

}
