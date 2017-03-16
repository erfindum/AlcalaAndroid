package com.aratech.lectoras;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aratech.lectoras.QuestionDialog.OnQuestionRespondedListener;
import com.aratech.lectoras.QuestionDialog.QuestionResponse;
import com.aratech.lectoras.adapters.FilesToImportAdapter;
import com.aratech.lectoras.adapters.FilesToImportAdapter.OnFileSelectedListener;
import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg.RegType;
import com.aratech.lectoras.beans.Reg20Comunidades;
import com.aratech.lectoras.data.RegDataBase;
import com.aratech.lectoras.io.FileReader;
import com.aratech.lectoras.io.FileReader.FileReaderException;

import java.io.File;
import java.util.ArrayList;

public class ImportActivity extends Activity implements OnClickListener,
		OnFileSelectedListener {

	Button btnImport;
	RadioButton rbUpdateRegisters, rbDeleteRegisters;
	RegDataBase dataBase;
	ListView lvFiles;
	TextView filesState;
	ArrayList<Reg> regsToLoad;
	File importDirectory;
	static String DEFAULT_FOLDER_NAME = "IMPORTACION_CONTADORES";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);

		rbDeleteRegisters = (RadioButton) findViewById(R.id.rbElimina);

		btnImport = (Button) findViewById(R.id.btnImportar);
		btnImport.setOnClickListener(this);

		dataBase = new RegDataBase(this);

		lvFiles = (ListView) findViewById(R.id.lvFiles);
		filesState = (TextView) findViewById(R.id.tvState);

		importDirectory = new File(Environment.getExternalStorageDirectory(),
				DEFAULT_FOLDER_NAME);
		new AsyncFileSearch().execute(importDirectory);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnImportar:
			startAsyncImport();
			break;
		}

	}

	@Override
	public void onFileSelected(File file) {

		new AsyncReadFile().execute(file.getName());

	}

	private void startAsyncImport() {
		if (regsToLoad != null && regsToLoad.size() > 0) {
			if (rbDeleteRegisters.isChecked()) {
				dataBase.clean();
				new AsyncImport().execute();
			} else {
				checkCommunitiesToLoad();
			}
		} else {
			Toast.makeText(ImportActivity.this, R.string.no_entradas,
					Toast.LENGTH_LONG).show();
		}
	}

	private void checkCommunitiesToLoad() {

		final ArrayList<Reg20Comunidades> repeatedCommunities = new ArrayList<Reg20Comunidades>();
		for (Reg newReg : regsToLoad) {
			if (newReg instanceof Reg20Comunidades) {
				ArrayList<Reg> tempArray = dataBase.getRegsFromType(
						RegType.REG20_COMUNIDADES,
						((Reg20Comunidades) newReg).getCommunityNumber());
				boolean repeated = tempArray != null && tempArray.size() > 0;
				if (repeated) {
					repeatedCommunities.add((Reg20Comunidades) newReg);
				}
			}
		}
		if (repeatedCommunities.size() > 0) {
			StringBuilder questionBuilder = new StringBuilder("Se sobreescribirán algunos datos:\n\n");
			for(Reg20Comunidades repCom : repeatedCommunities){
				questionBuilder.append("- Comunidad: ").append(repCom.getCommunityNumber()).append("(").append(repCom.getCommunityAddress()).append(")\n");
			}
			questionBuilder.append("\n¿Desea Continuar?");
			QuestionDialog dialog = new QuestionDialog(this, questionBuilder.toString(),
					new OnQuestionRespondedListener() {

						@Override
						public void onQuestionResponse(QuestionResponse response) {
							if (response == QuestionResponse.YES) {
								for(Reg20Comunidades repCom : repeatedCommunities){
									dataBase.deleteCommunityFromDataBase(repCom);
								}
								new AsyncImport().execute();
							}

						}
					});
			dialog.show();
		} else {
			new AsyncImport().execute();
		}

	}

	public void updateState() {
		if (regsToLoad == null || regsToLoad.size() == 0) {
			filesState.setText(R.string.archivo_no_registros);
			filesState.setTextColor(getResources().getColor(R.color.dark_red));
		} else if (regsToLoad.size() == 1) {
			filesState.setText(R.string.archivo_cargado_uni);
			filesState
					.setTextColor(getResources().getColor(R.color.dark_green));
		} else {
			String message = getString(R.string.archivo_cargado_multi);
			message = message
					.replaceAll(
							getString(R.string.archivo_cargado_cantidad_para_reemplazar),
							String.valueOf(regsToLoad.size()));
			filesState.setText(message);
			filesState
					.setTextColor(getResources().getColor(R.color.dark_green));
		}
	}

	private class AsyncFileSearch extends
			AsyncTask<File, Void, ArrayList<File>> {

		AlertDialog waitDialog;
		TextView textDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			waitDialog = new AlertDialog.Builder(ImportActivity.this).create();
			View v = getLayoutInflater().inflate(R.layout.dialog_wait, null);
			waitDialog.setView(v);
			textDialog = (TextView) v.findViewById(R.id.tvDialog);
			textDialog.setText(R.string.cargando);
			waitDialog.show();
		}

		@Override
		protected ArrayList<File> doInBackground(File... params) {
			try {
				ArrayList<File> result = new ArrayList<File>();
				File directory = params[0];
				for (String path : directory.list()) {
					File newItem = new File(path);
					if (!newItem.isDirectory()) {
						result.add(newItem);
					}
				}
				return result;

			} catch (Exception ex) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<File> result) {
			super.onPostExecute(result);
			lvFiles.setAdapter(new FilesToImportAdapter(ImportActivity.this,
					result, ImportActivity.this));
			waitDialog.dismiss();
		}
	}

	private class AsyncReadFile extends AsyncTask<String, Void, ArrayList<Reg>> {

		AlertDialog waitDialog;
		TextView textDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			waitDialog = new AlertDialog.Builder(ImportActivity.this).create();
			View v = getLayoutInflater().inflate(R.layout.dialog_wait, null);
			waitDialog.setView(v);
			textDialog = (TextView) v.findViewById(R.id.tvDialog);
			textDialog.setText(R.string.leyendo);
			waitDialog.show();
		}

		@Override
		protected ArrayList<Reg> doInBackground(String... params) {
			ArrayList<Reg> result = null;
			try {

				FileReader reader = new FileReader(importDirectory
						+ File.separator + params[0]);
				result = reader.proccessFile();

			} catch (FileReaderException e) {

				result = null;
			}
			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<Reg> result) {
			super.onPostExecute(result);
			regsToLoad = result;
			updateState();
			waitDialog.dismiss();
		}
	}

	private class AsyncImport extends AsyncTask<Void, String, String> {

		AlertDialog waitDialog;
		TextView textDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			waitDialog = new AlertDialog.Builder(ImportActivity.this).create();
			View v = getLayoutInflater().inflate(R.layout.dialog_wait, null);
			waitDialog.setView(v);
			textDialog = (TextView) v.findViewById(R.id.tvDialog);
			waitDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				int counter = 0;
				for (Reg newReadedReg : regsToLoad) {
					counter++;
					publishProgress(getString(R.string.guardando)
							+ String.valueOf(counter) + " de "
							+ String.valueOf(regsToLoad.size()));
					dataBase.saveReg(newReadedReg);

				}
				result = "Agregados con éxito " + counter + " registros";
			} catch (Exception e) {

				result = "ERROR AL CARGAR LOS ARCHIVOS";
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			waitDialog.dismiss();
			if (result != null) {
				Toast.makeText(ImportActivity.this, result, Toast.LENGTH_LONG)
						.show();
			}

		}

		@Override
		protected void onProgressUpdate(String... values) {

			super.onProgressUpdate(values);
			textDialog.setText(values[0]);

		}
	}

}
