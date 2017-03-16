package com.aratech.lectoras.beans;

import android.content.ContentValues;
import android.database.Cursor;

import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg99Constants;

public class Reg99TipoLectura extends Reg {
	/* Código de lectura A(2), Texto lectura A(30) */

	public String readCode, readText;

	public final static String EMPTY_TYPE = "-123";
	
	int READ_CODE_POSITION = 1;
	int READ_TEXT_POSITION = 2;

	protected Reg99TipoLectura(String lineSource) {
		super(lineSource);
		readCode = getItemByPositionForImport(READ_CODE_POSITION);
		readText = getItemByPositionForImport(READ_TEXT_POSITION);
	}

	protected Reg99TipoLectura(Cursor cursorFrom) {
		super(cursorFrom);
		readCode = getItemByPositionForExport(READ_CODE_POSITION);
		readText = getItemByPositionForExport(READ_TEXT_POSITION);
	}
	
	public Reg99TipoLectura(){
		readCode = EMPTY_TYPE;
		readText = "";
	}

	@Override
	public RegType getType() {
		return RegType.REG99;
	}

	@Override
	public ContentValues getImportContentValuesForDataBase() {
		ContentValues result = new ContentValues();
		result.put(Reg99Constants.READ_CODE, readCode);
		if (getSplitedLine()[READ_TEXT_POSITION] != null)
			result.put(Reg99Constants.READ_TEXT, readText);
		return result;
	}
                   
	@Override
	public String getTableNameForDataBase() {
		return Reg99Constants.TABLE_NAME;
	}

	@Override
	public ContentValues getExportFieldNamesForDataBase() {		
		return getImportContentValuesForDataBase();
	}

	@Override
	public String[] getFieldNamesForDataBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportComunity() {
		// TODO Auto-generated method stub
		return false;
	}

}
