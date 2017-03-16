package com.aratech.radio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

public class RadioDataBaseHelper {

	private static final String DATABASE_NAME = "esclavoUni" + ".db";

	private RadioDataBaseHelper() {
	}

	public static File getDataBaseFile() {
		File dir = new File(Environment.getExternalStorageDirectory(),
				"Mirakonta");
		return new File(dir, DATABASE_NAME);
	}

	private static SQLiteDatabase getDataBase() {
		return SQLiteDatabase.openDatabase(getDataBaseFile().getPath(), null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	public static int getReadforSerial(String serialName) {

		SQLiteDatabase db = null;
		int readValue = -1;
		try {
			db = getDataBase();

			Cursor c = db.rawQuery("SELECT * FROM esclavoUni WHERE cic =?",
					new String[] { serialName });
			if (c.moveToFirst()) {
				int columnReadPosition = c.getColumnIndex("Lectura");
				readValue = c.getInt(columnReadPosition);
			}
			db.close();
		} catch (Exception ex) {
			if (db != null) {
				db.close();
			}
			readValue = -2;
		}
		return readValue;
	}

	public static boolean cleanReadingsDataBase() {
		if (existsDatabase()) {
			SQLiteDatabase db = null;
			try {
				db = getDataBase();
				db.delete("esclavoUni", null, null);
				db.close();
				return true;
			} catch (Exception ex) {
				if (db != null) {
					db.close();
				}
				return false;
			}
		} else {
			return true;
		}
	}

	public static int getReadingsCount() {

		SQLiteDatabase db = null;
		int counter = 0;
		try {
			db = getDataBase();
			Cursor c = db.rawQuery("SELECT * FROM esclavoUni", null);
			if (c.moveToFirst()) {
				do {
					counter++;
				} while (c.moveToNext());
			}
			db.close();
		} catch (Exception ex) {
			if (db != null) {
				db.close();
			}
			counter = -1;
		}
		return counter;
	}

	public static boolean existsDatabase() {

		return getDataBaseFile().exists();
	}
}
