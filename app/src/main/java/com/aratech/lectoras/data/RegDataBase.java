package com.aratech.lectoras.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aratech.lectoras.R;
import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg.RegType;
import com.aratech.lectoras.beans.Reg10;
import com.aratech.lectoras.beans.Reg20Comunidades;
import com.aratech.lectoras.beans.Reg40Pisos;
import com.aratech.lectoras.beans.Reg60Lecturas;
import com.aratech.lectoras.beans.Reg99TipoLectura;
import com.aratech.lectoras.data.classes.RegDataBaseHelper;
import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements;
import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg10Constants;
import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg20Constants;
import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg40Constants;
import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg45Constants;
import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg60Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class RegDataBase {

	private SQLiteDatabase database;
	private RegDataBaseHelper dbHelper;

	private String EXCEPTION_TAG = "DATABASE_ERROR";

	public RegDataBase(Context context) {
		dbHelper = new RegDataBaseHelper(context);
	}

	public void backupDatabase(String outFileName) {
		try {
			String inFileName = "/data/data/com.aratech.lectoras/databases/"
					+ RegDataBaseHelper.DATABASE_NAME;
			File dbFile = new File(inFileName);
			FileInputStream fis = new FileInputStream(dbFile);

			File exportDirectory = new File(getExternalDirectory(),
					"BACKUP_LECTORAS");
			if (!exportDirectory.exists()) {
				exportDirectory.mkdir();
			}
			File outFile = new File(exportDirectory, outFileName);

			// Open the empty db as the output stream
			OutputStream output = new FileOutputStream(outFile);

			// Transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}

			// Close the streams
			output.flush();
			output.close();
			fis.close();
		} catch (Exception ex) {
			Log.e("Error backup", String.valueOf(ex));
		}
	}

	public static File getExternalDirectory() {
		String resultPath = "/mnt/external_sd";
		File directory = new File(resultPath);
		if (directory.exists() && directory.isDirectory()
				&& directory.canWrite()) {
			return directory;
		} else {
			return Environment.getExternalStorageDirectory();
		}

	}

	public void restoreDatabase(File backupDB) {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "/data/com.aratech.lectoras/databases/"
						+ RegDataBaseHelper.DATABASE_NAME;// "//data//package name//databases//database_name";
				// String backupDBPath = "database_name";
				File currentDB = new File(data, currentDBPath);
				// File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(backupDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(currentDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}
		} catch (Exception e) {
		}
	}

	public void saveRegs(Reg[] regsToSave) {
		for (Reg newRegToSave : regsToSave) {
			this.saveReg(newRegToSave);
		}
	}

	public ArrayList<Reg> getTarifas(int communityNumber, int propertyNumber,
			int ownerNumber) {
		ArrayList<Reg> result = new ArrayList<Reg>();
		try {
			StringBuilder statementBuilder = new StringBuilder(
					RegDataBaseSQL_statements
							.getSelectForRegTable(RegType.REG45));
			statementBuilder.append(" WHERE ");
			statementBuilder.append(Reg45Constants.COMMUNITY);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(communityNumber);
			statementBuilder.append("%' AND ");
			statementBuilder.append(Reg45Constants.PROPERTY);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(propertyNumber);
			statementBuilder.append("%' AND ");
			statementBuilder.append(Reg45Constants.OWNER);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(ownerNumber);
			statementBuilder.append("%'");

			open();
			Cursor c = database.rawQuery(statementBuilder.toString(), null);
			try {
				result = getRegsFromCursor(c, RegType.REG45);
			} catch (Exception ex) {
				Log.e("Error al cargar lectura!!!",
						ex != null ? ex.getMessage() : " EX-NULL :P ");
			}
			close();
		} catch (Exception ex) {
			close();
		}
		return result;
	}

	public HashMap<Reg.RegType, ArrayList<Reg>> getRegsForExport() {

		HashMap<Reg.RegType, ArrayList<Reg>> result = new HashMap<Reg.RegType, ArrayList<Reg>>();

		result.put(RegType.REG20_COMUNIDADES,
				getRegsFromType(RegType.REG20_COMUNIDADES));
		result.put(RegType.REG40, getRegsFromType(RegType.REG40));
		result.put(RegType.REG60, getRegsFromType(RegType.REG60));

		return result;
	}

	public HashMap<Reg.RegType, ArrayList<Reg>> getRegsForExport(
			int communityNumber) {

		HashMap<Reg.RegType, ArrayList<Reg>> result = new HashMap<Reg.RegType, ArrayList<Reg>>();

		result.put(RegType.REG20_COMUNIDADES,
				getRegsFromType(RegType.REG20_COMUNIDADES, communityNumber));
		result.put(RegType.REG40,
				getRegsFromType(RegType.REG40, communityNumber));
		result.put(RegType.REG60,
				getRegsFromType(RegType.REG60, communityNumber));

		return result;
	}

	public ArrayList<Reg> getCommunitys() {
		return getRegsFromType(RegType.REG20_COMUNIDADES);
	}

	public ArrayList<Reg> getCommunitys(String[] orderArgs) {
		return getRegsFromType(RegType.REG20_COMUNIDADES, orderArgs);
	}

	public boolean isReadingCompleted(Reg40Pisos ownerFloor) {
		try {
			ArrayList<Reg> result = getReadings(
					Integer.valueOf(ownerFloor.community),
					Integer.valueOf(ownerFloor.property),
					Integer.valueOf(ownerFloor.owner));

			for (Reg newReg : result) {
				if (((Reg60Lecturas) newReg).haveRead == null
						|| ((Reg60Lecturas) newReg).haveRead.equals("")
						|| Integer.valueOf(((Reg60Lecturas) newReg).haveRead) == 0) {
					return false;
				}
			}

		} catch (Exception ex) {
			Log.e("DATABASE", "Error al obtener lectura");
			return false;
		}
		return true;
	}

	public Reg60Lecturas getReading(Reg40Pisos ownerFloor, Reg10 conccept) {
		Reg60Lecturas result = null;
		try {
			result = (Reg60Lecturas) getReading(
					Integer.valueOf(ownerFloor.community),
					Integer.valueOf(ownerFloor.property),
					Integer.valueOf(ownerFloor.owner),
					Integer.valueOf(conccept.concept));

		} catch (Exception ex) {
			Log.e("DATABASE", "Error al obtener lectura");
		}
		return result;
	}

	public ArrayList<Reg> getPropertyByCommunity(int communityNumber) {
		return this.getRegsFromType(RegType.REG20_COMUNIDADES, communityNumber);
	}

	public ArrayList<Reg> getFloorByCommunity(int communityNumber) {
		ArrayList<Reg> result = this.getRegsFromType(RegType.REG40,
				communityNumber);
		// result.addAll(this.getRegsFromType(RegType.REG45, communityNumber));
		return result;
	}

	public ArrayList<Reg> getConcepts(int communityNumber) {
		return this.getRegsFromType(RegType.REG10_CONCEPTOS, communityNumber);
	}

	public ArrayList<Reg> getConcepts(int communityNumber, int propertyNumber,
			int ownerNumber) {
		ArrayList<Reg> result = new ArrayList<Reg>();

		ArrayList<Reg> availableConcepts = getConcepts(communityNumber);
		ArrayList<Reg> readingsForOwner = getReadings(communityNumber,
				propertyNumber, ownerNumber);

		for (Reg reading : readingsForOwner) {
			Reg60Lecturas castedReading = (Reg60Lecturas) reading;
			try {

					int readingConceptNumber = Integer
							.valueOf(castedReading.concept);
					for (Reg concept : availableConcepts) {
						Reg10 castedConcept = (Reg10) concept;
						int conceptNumber = Integer
								.valueOf(castedConcept.concept);
						if (conceptNumber == readingConceptNumber) {
							result.add(castedConcept);
						}
					}

				
			} catch (Exception ex) {

			}

		}
		/*
		 * try { StringBuilder statementBuilder = new StringBuilder(
		 * RegDataBaseSQL_statements .getSelectForRegTable(RegType.REG60));
		 * statementBuilder.append(" WHERE ");
		 * statementBuilder.append(Reg60Constants.COMMUNITY);
		 * statementBuilder.append(" LIKE '%");
		 * statementBuilder.append(communityNumber);
		 * statementBuilder.append("%' AND ");
		 * statementBuilder.append(Reg60Constants.OWNER);
		 * statementBuilder.append(" LIKE '");
		 * statementBuilder.append(ownerNumber); statementBuilder.append("'");
		 * 
		 * open(); Cursor c = database.rawQuery(statementBuilder.toString(),
		 * null); try { result = getRegsFromCursor(c, RegType.REG60); } catch
		 * (Exception ex) { Log.e("Error al cargar lectura!!!", ex != null ?
		 * ex.getMessage() : " EX-NULL :P "); } close(); } catch (Exception ex)
		 * { close(); }
		 */
		return result;
	}

	public ArrayList<Reg> getReadingsByCommunity(int communityNumber) {
		return this.getRegsFromType(RegType.REG60, communityNumber);
	}

	public ArrayList<Reg> getReadingTypes() {
		ArrayList<Reg> result = getRegsFromType(RegType.REG99, null);
		Reg99TipoLectura emptyType = new Reg99TipoLectura();
		result.add(0, emptyType);
		return result;
	}

	public ArrayList<Reg> getRegsFromType(RegType type) {

		return getRegsFromType(type, null);
	}

	public ArrayList<Reg> getRegsFromType(RegType type, String[] orderArgs) {

		StringBuilder statementBuilder = new StringBuilder(
				RegDataBaseSQL_statements.getSelectForRegTable(type));
		if (orderArgs != null) {
			statementBuilder.append(" ORDER BY ");
			for (int i = 0; i < orderArgs.length; i++) {
				statementBuilder.append(orderArgs[i]);
				if ((i + 1) < orderArgs.length) {
					statementBuilder.append(", ");
				}
			}
		}
		open();
		Cursor c = database.rawQuery(statementBuilder.toString(), null);

		ArrayList<Reg> result = getRegsFromCursor(c, type);
		close();
		return result;
	}

	public ArrayList<Reg> getRegsFromType(RegType type, int communityNumber,
			String[] orderArgs) {
		StringBuilder statementBuilder = new StringBuilder(
				RegDataBaseSQL_statements.getSelectForRegTable(type));
		statementBuilder.append(" WHERE ");
		statementBuilder.append(Reg20Constants.COMMUNITY);
		statementBuilder.append("=?");
		if (orderArgs != null) {
			statementBuilder.append(" ORDER BY ");
			for (int i = 0; i < orderArgs.length; i++) {
				statementBuilder.append(orderArgs[i]);
				if ((i + 1) < orderArgs.length) {
					statementBuilder.append(", ");
				}
			}
		}

		open();
		Cursor c = database.rawQuery(statementBuilder.toString(),
				new String[] { String.valueOf(communityNumber) });

		ArrayList<Reg> result = getRegsFromCursor(c, type);
		close();
		return result;
	}

	public ArrayList<Reg> getReadings(int communityNumber, int propertyNumber,
			int ownerNumber) {
		ArrayList<Reg> result = new ArrayList<Reg>();
		try {
			StringBuilder statementBuilder = new StringBuilder(
					RegDataBaseSQL_statements
							.getSelectForRegTable(RegType.REG60));
			statementBuilder.append(" WHERE ");
			statementBuilder.append(Reg60Constants.COMMUNITY);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(communityNumber);
			statementBuilder.append("%' AND ");
			statementBuilder.append(Reg60Constants.PROPERTY);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(propertyNumber);
			statementBuilder.append("%' AND ");
			statementBuilder.append(Reg60Constants.OWNER);
			statementBuilder.append(" LIKE '");
			statementBuilder.append(ownerNumber);
			statementBuilder.append("'");

			open();
			Cursor c = database.rawQuery(statementBuilder.toString(), null);
			try {
				result = getRegsFromCursor(c, RegType.REG60);
			} catch (Exception ex) {
				Log.e("Error al cargar lectura!!!",
						ex != null ? ex.getMessage() : " EX-NULL :P ");
			}
			close();
		} catch (Exception ex) {
			close();
		}
		return result;
	}

	public ArrayList<Reg> getReadingsWithRadioByCommunity(
			Reg20Comunidades community) {
		ArrayList<Reg> result = new ArrayList<Reg>();
		try {
			StringBuilder statementBuilder = new StringBuilder(
					RegDataBaseSQL_statements
							.getSelectForRegTable(RegType.REG60));
			statementBuilder.append(" WHERE ");
			statementBuilder.append(Reg60Constants.COMMUNITY);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(community.getCommunityNumber());
			statementBuilder.append("%' AND ");
			statementBuilder.append(Reg60Constants.PROPERTY);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(community.getPropertyNumber());
			statementBuilder.append("%' AND TRIM(");
			statementBuilder.append(Reg60Constants.SERIAL);
			statementBuilder.append(") NOT LIKE '' ");

			open();
			Cursor c = database.rawQuery(statementBuilder.toString(), null);
			try {
				result = getRegsFromCursor(c, RegType.REG60);

			} catch (Exception ex) {
				Log.e("Error al cargar lectura!!!", String.valueOf(ex));
			}
			close();
		} catch (Exception ex) {
			close();
			Log.e("Error al cargar las lecturas!", String.valueOf(ex));
		}
		return result;
	}

	/**
	 * @return -2 error, -1 si no está empezado, 0 si está empezado, 1 si está
	 *         terminado
	 */

	public int getReadingsState(Reg40Pisos piso) {
		return this.getReadingsState(Integer.valueOf(piso.community),
				Integer.valueOf(piso.property), Integer.valueOf(piso.owner));
	}

	/**
	 * @return -2 error, -1 si no está empezado, 0 si está empezado, 1 si está
	 *         terminado
	 */

	public int getReadingsState(int communityNumber, int propertyNumber,
			int ownerNumber) {
		int result = -2;
		try {

			ArrayList<Reg> readingsForOwner = getReadings(communityNumber,
					propertyNumber, ownerNumber);

		

			if (readingsForOwner.size() == 0) {
				result = -2;
			} else {

				int completed = 0;
				int unStarted = 0;
				int totalSize = readingsForOwner.size();
				
				for (int i = 0; i < totalSize; i++) {

					Reg60Lecturas reading = (Reg60Lecturas) readingsForOwner.get(i);

					if (!reading.saved) {
						unStarted++;
					} else {
						completed++;
					}
				}
				if (completed == totalSize) {
					result = 1;
				} else if (unStarted == totalSize) {
					result = -1;
				} else {
					result = 0;
				}
			}
		} catch (Exception ex) {
			result = -2;
		}
		return result;
	}

	public Reg getReading(int communityNumber, int propertyNumber,
			int ownerNumber, int conceptNumber) {
		Reg result = null;
		try {
			StringBuilder statementBuilder = new StringBuilder(
					RegDataBaseSQL_statements
							.getSelectForRegTable(RegType.REG60));
			statementBuilder.append(" WHERE ");
			statementBuilder.append(Reg60Constants.COMMUNITY);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(communityNumber);
			statementBuilder.append("%' AND ");
			statementBuilder.append(Reg60Constants.PROPERTY);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(propertyNumber);
			statementBuilder.append("%' AND ");
			statementBuilder.append(Reg60Constants.OWNER);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(ownerNumber);
			statementBuilder.append("%' AND ");
			statementBuilder.append(Reg60Constants.CONCEPT);
			statementBuilder.append(" LIKE '%");
			statementBuilder.append(conceptNumber);
			statementBuilder.append("%'");

			open();
			Cursor c = database.rawQuery(statementBuilder.toString(), null);
			try {
				result = getRegsFromCursor(c, RegType.REG60).get(0);
			} catch (Exception ex) {
				Log.e("Error al cargar lectura!!!",
						ex != null ? ex.getMessage() : " EX-NULL :P ");
			}
			close();
		} catch (Exception ex) {
			close();
		}
		return result;
	}

	public int getFloorsState(Reg20Comunidades community) {
		int result = -2;
		try {

			ArrayList<Reg> floors = getFloorByCommunity(community
					.getCommunityNumber());
			if (floors.size() > 0) {
				int completed = 0;
				int unStarted = 0;

				for (Reg floor : floors) {
					int readingState = getReadingsState((Reg40Pisos) floor);
					switch (readingState) {
					case 1:
						completed++;
						break;
					case -1:
						unStarted++;
						break;
					}
				}
				if (completed == floors.size()) {
					result = 1;
				} else if (unStarted == floors.size()) {
					result = -1;
				} else {
					result = 0;
				}

			}

		} catch (Exception ex) {
			result = -2;
		}
		return result;
	}

	public ArrayList<Reg> getFloorsByCommunityAndProperty(int communityNumber,
			int propertyNumber, String[] orderArgs) {
		return getFloorsByCommunityAndProperty(RegType.REG40, communityNumber,
				propertyNumber, orderArgs);
	}

	public ArrayList<Reg> getFloorsByCommunityAndProperty(RegType type,
			int communityNumber, int propertyNumber, String[] orderArgs) {
		StringBuilder statementBuilder = new StringBuilder(
				RegDataBaseSQL_statements.getSelectForRegTable(type));
		statementBuilder.append(" WHERE ");
		statementBuilder.append(Reg40Constants.COMMUNITY);
		statementBuilder.append("=?");
		statementBuilder.append(" AND ");
		statementBuilder.append(Reg40Constants.PROPERTY);
		statementBuilder.append("=?");
		if (orderArgs != null) {
			statementBuilder.append(" ORDER BY ");
			for (int i = 0; i < orderArgs.length; i++) {
				statementBuilder.append(orderArgs[i]);
				if ((i + 1) < orderArgs.length) {
					statementBuilder.append(", ");
				}
			}
		}

		open();
		Cursor c = database.rawQuery(
				statementBuilder.toString(),
				new String[] { String.valueOf(communityNumber),
						String.valueOf(propertyNumber) });

		ArrayList<Reg> result = getRegsFromCursor(c, type);
		close();
		return result;
	}

	public ArrayList<Reg> getRegsFromType(RegType type, int communityNumber) {
		return getRegsFromType(type, communityNumber, null);

	}

	private ArrayList<Reg> getRegsFromCursor(Cursor c, RegType type) {
		ArrayList<Reg> result = new ArrayList<Reg>();
		try {
			if (c.moveToFirst()) {
				do {
					Reg newLoadedReg = Reg.loadRegObjectFromCursor(c, type);
					if (newLoadedReg != null) {
						result.add(newLoadedReg);
					}
				} while (c.moveToNext());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public HashMap<Reg.RegType, ArrayList<Reg>> getRegsForExport(
			int comunityFrom, int comunityTo) {
		return null;
	}

	public void saveReg(Reg regToSave) {
		try {
			open();
			database.insert(regToSave.getTableNameForDataBase(), null,
					regToSave.getImportContentValuesForDataBase());
			close();
		} catch (Exception ex) {
			close();
			StringBuilder exceptionMessageBuilder = new StringBuilder(
					"Error al insertar un registro").append("\n")
					.append("Exception:\n").append(ex.getMessage());

			Log.e(EXCEPTION_TAG, exceptionMessageBuilder.toString());
		}

	}

	public void deleteCommunityFromDataBase(Reg20Comunidades communityToDelete) {
		StringBuilder whereBuilder = new StringBuilder(Reg20Constants.COMMUNITY);
		whereBuilder.append(" =?");
		String[] whereArgs = new String[] { String.valueOf(communityToDelete
				.getCommunityNumber()) };
		open();
		database.delete(Reg10Constants.TABLE_NAME, whereBuilder.toString(),
				whereArgs);
		database.delete(Reg20Constants.TABLE_NAME, whereBuilder.toString(),
				whereArgs);
		database.delete(Reg40Constants.TABLE_NAME, whereBuilder.toString(),
				whereArgs);
		database.delete(Reg45Constants.TABLE_NAME, whereBuilder.toString(),
				whereArgs);
		database.delete(Reg60Constants.TABLE_NAME, whereBuilder.toString(),
				whereArgs);
		close();

	}

	public void updateReg(Reg regToUpdate) {
		switch (regToUpdate.getType()) {
		case REG20_COMUNIDADES:
			updateProperty((Reg20Comunidades) regToUpdate);
			break;
		case REG40:
			updateFloor((Reg40Pisos) regToUpdate);
			break;
		case REG60:
			updateReading((Reg60Lecturas) regToUpdate);
			break;
		default:
			break;
		}
	}

	public void asyncUpdateReg(Reg regToUpdate, Activity context) {
		new AsyncDataBaseUpdate(context, true,
				context.getString(R.string.actualiza_registros))
				.execute(regToUpdate);
	}

	private void updateProperty(Reg20Comunidades communityRegToUpdate) {
		try {

			StringBuilder whereBuilder = new StringBuilder(
					Reg20Constants.COMMUNITY);
			whereBuilder.append(" =? AND ");
			whereBuilder.append(Reg20Constants.PROPERTY);
			whereBuilder.append(" =?");

			String[] args = { communityRegToUpdate.getCommunityStringNumber(),
					communityRegToUpdate.getPropertyStringNumber() };
			open();
			int registersUpdated = database.update(
					communityRegToUpdate.getTableNameForDataBase(),
					communityRegToUpdate.getExportFieldNamesForDataBase(),
					whereBuilder.toString(), args);

			close();
		} catch (Exception ex) {
			close();
		}
	}

	private void updateFloor(Reg40Pisos floorToUpdate) {
		try {

			StringBuilder whereBuilder = new StringBuilder(
					Reg40Constants.COMMUNITY);
			whereBuilder.append(" =? AND ");
			whereBuilder.append(Reg40Constants.PROPERTY);
			whereBuilder.append(" =? AND ");
			whereBuilder.append(Reg40Constants.OWNER);
			whereBuilder.append(" =? AND ");
			whereBuilder.append(Reg40Constants.FLOOR);
			whereBuilder.append(" =?");

			String[] args = { floorToUpdate.community, floorToUpdate.property,
					floorToUpdate.owner, floorToUpdate.floor };
			open();
			int registersUpdated = database.update(
					floorToUpdate.getTableNameForDataBase(),
					floorToUpdate.getExportFieldNamesForDataBase(),
					whereBuilder.toString(), args);

			close();
		} catch (Exception ex) {
			close();
		}
	}

	private void updateReading(Reg60Lecturas regToUpdate) {
		try {

			StringBuilder whereBuilder = new StringBuilder(
					Reg60Constants.COMMUNITY);
			whereBuilder.append(" =? AND ");
			whereBuilder.append(Reg60Constants.PROPERTY);
			whereBuilder.append(" =? AND ");
			whereBuilder.append(Reg60Constants.OWNER);
			whereBuilder.append(" =? AND ");
			whereBuilder.append(Reg60Constants.CONCEPT);
			whereBuilder.append(" =?");

			String[] args = { regToUpdate.community, regToUpdate.property,
					regToUpdate.owner, regToUpdate.concept };
			open();
			int registersUpdated = database.update(
					regToUpdate.getTableNameForDataBase(),
					regToUpdate.getExportFieldNamesForDataBase(),
					whereBuilder.toString(), args);

			close();
		} catch (Exception ex) {
			close();
		}

	}

	public void clean() {
		try {
			open();
			dbHelper.cleanDataBase(database);
			close();
		} catch (Exception ex) {
			close();
			StringBuilder exceptionMessageBuilder = new StringBuilder(
					"Error al limpiar la base de datos").append("\n")
					.append("Exception:\n").append(ex.getMessage());

			Log.e(EXCEPTION_TAG, exceptionMessageBuilder.toString());
		}
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		dbHelper.close();
	}

	private class AsyncDataBaseUpdate extends AsyncTask<Reg, Void, Void> {

		AlertDialog waitDialog;
		TextView textDialog;
		Activity mContext;
		boolean showWaitDialog;
		String messageToShow;

		public AsyncDataBaseUpdate(Activity currentContext,
				boolean withWaitDialog, String messageForDialog) {
			mContext = currentContext;
			showWaitDialog = withWaitDialog;
			messageToShow = messageForDialog;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showWaitDialog) {
				waitDialog = new AlertDialog.Builder(mContext).create();
				View v = mContext.getLayoutInflater().inflate(
						R.layout.dialog_wait, null);
				waitDialog.setView(v);

				if (messageToShow != null && !messageToShow.equals("")) {
					textDialog = (TextView) v.findViewById(R.id.tvDialog);
					textDialog.setText(messageToShow);
				}
				waitDialog.show();
			}
		}

		@Override
		protected Void doInBackground(Reg... params) {
			for (Reg newReg : params) {
				updateReg(newReg);
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (showWaitDialog)
				waitDialog.dismiss();
		};
	}

}
