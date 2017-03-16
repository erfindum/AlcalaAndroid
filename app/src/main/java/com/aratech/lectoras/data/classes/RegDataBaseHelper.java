package com.aratech.lectoras.data.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class RegDataBaseHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "lectura_contadores.db";
	public static final int DATABASE_VERSION = 1;



	public RegDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		createAllTables(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(RegDataBaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		dropAllTables(db);
		onCreate(db);
	}
	
	public void cleanDataBase(SQLiteDatabase db){
		dropAllTables(db);
		createAllTables(db);
	}
	
	private void createAllTables(SQLiteDatabase db){
		db.execSQL(RegDataBaseSQL_statements.REG00_TABLE_CREATE);
		db.execSQL(RegDataBaseSQL_statements.REG10_TABLE_CREATE);
		db.execSQL(RegDataBaseSQL_statements.REG20_TABLE_CREATE);
		db.execSQL(RegDataBaseSQL_statements.REG40_TABLE_CREATE);
		db.execSQL(RegDataBaseSQL_statements.REG45_TABLE_CREATE);
		db.execSQL(RegDataBaseSQL_statements.REG60_TABLE_CREATE);
		db.execSQL(RegDataBaseSQL_statements.REG90_TABLE_CREATE);
		db.execSQL(RegDataBaseSQL_statements.REG99_TABLE_CREATE);
	}
	
	private void dropAllTables(SQLiteDatabase db){
		db.execSQL(RegDataBaseSQL_statements.REG00_TABLE_DROP);
		db.execSQL(RegDataBaseSQL_statements.REG10_TABLE_DROP);
		db.execSQL(RegDataBaseSQL_statements.REG20_TABLE_DROP);
		db.execSQL(RegDataBaseSQL_statements.REG40_TABLE_DROP);
		db.execSQL(RegDataBaseSQL_statements.REG45_TABLE_DROP);
		db.execSQL(RegDataBaseSQL_statements.REG60_TABLE_DROP);
		db.execSQL(RegDataBaseSQL_statements.REG90_TABLE_DROP);
		db.execSQL(RegDataBaseSQL_statements.REG99_TABLE_DROP);
	}
}
