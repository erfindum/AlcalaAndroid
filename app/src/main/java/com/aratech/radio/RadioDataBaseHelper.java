package com.aratech.radio;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class RadioDataBaseHelper {

	private static final String aberingUri = "content://mirakontaservice.contentproviders.esclavosmk/esclavosuni";
	private static final String wmbusUri = "content://mirakontaservice.contentproviders.esclavoswmbus/esclavoswmbus";



	private RadioDataBaseHelper() {
	}

	/*public static File getDataBaseFile() {
		File dir = new File(Environment.getExternalStorageDirectory(),
				"Mirakonta");
		return new File(dir, DATABASE_NAME);
	}

	private static SQLiteDatabase getDataBase() {
		return SQLiteDatabase.openDatabase(getDataBaseFile().getPath(), null,
				SQLiteDatabase.OPEN_READWRITE);
	} */

	public static int getReadforSerial(String serialName, String antennaType, ContentResolver contentResolver) {

		int readValue = -1;
		try {
            if(antennaType.equals("abering")){
                String[] projection = new String[]{"_id", "cic","Lectura"};
                String[] selectionArgs = new String[]{serialName};
                Cursor c = contentResolver.query(Uri.parse(aberingUri),projection,"cic =?",selectionArgs,null);
                if (c!=null && c.moveToFirst()) {
                    int columnReadPosition = c.getColumnIndex("Lectura");
                    readValue = c.getInt(columnReadPosition);
                    c.close();
                }
            }

            if(antennaType.equals("wmbus")) {
                String[] projection = new String[]{"_id", "cic","Lectura"};
                String[] selectionArgs = new String[]{serialName};
                Cursor c = contentResolver.query(Uri.parse(wmbusUri),projection,"cic =?",selectionArgs,null);
                if (c!=null && c.moveToFirst()) {
                    int columnReadPosition = c.getColumnIndex("Lectura");
                    readValue = c.getInt(columnReadPosition);
                    c.close();
                }
            }
		} catch (Exception ex) {
			readValue = -2;
		}
		return readValue;
	}

	public static boolean cleanReadingsDataBase(String antennaType, ContentResolver contentResolver) {
        try {
            if(antennaType.equals("abering")){
                contentResolver.delete(Uri.parse(aberingUri),null,null);
            }
            if (antennaType.equals("wmbus")){
                contentResolver.delete(Uri.parse(wmbusUri),null,null);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

	public static int getReadingsCount(String antennaType, ContentResolver contentResolver) {

		int counter = 0;
		try {
            Cursor c = getDbCursor(antennaType,contentResolver);
			if (c!=null && c.moveToFirst()) {
				do {
					counter++;
				} while (c.moveToNext());
                c.close();
			}
		} catch (Exception ex) {
			counter = -1;
		}
		return counter;
	}

    private static Cursor getDbCursor(String antennaType, ContentResolver contentResolver){
        if(antennaType.equals("abering")){
            Uri uri = Uri.parse(aberingUri);
            return contentResolver.query(uri,new String[]{"_id"},null,null,null);
        }
        if(antennaType.equals("wmbus")){
            Uri uri = Uri.parse(wmbusUri);
            return contentResolver.query(uri,new String[]{"_id"},null,null,null);
        }
        return  null;
    }

	/*public static boolean existsDatabase() {

		return getDataBaseFile().exists();
	} */
}
