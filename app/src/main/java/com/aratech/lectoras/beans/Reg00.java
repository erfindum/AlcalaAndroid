package com.aratech.lectoras.beans;

import android.content.ContentValues;
import android.database.Cursor;

import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg00Constants;

public class Reg00 extends Reg {
	/*
	 * Tipo (N2), Fecha(dd/mm/aaaa), Fecha inicio (dd/mm/aaaa), Fecha fin
	 * (dd/mm/aaaa), Vacío(A3) que, si contiene EUR trae los valores en euros.
	 */

	public String date;

	String initDate;

	String endDate;

	String empty;

	int DATE_POSITION = 1;
	int INIT_DATE_POSITION = 2;
	int END_DATE_POSITION = 3;
	int EMPTY_POSITION = 4;

	protected Reg00(String lineSource) {
		super(lineSource);
		date = getItemByPositionForImport(DATE_POSITION);
		initDate = getItemByPositionForImport(INIT_DATE_POSITION);
		endDate = getItemByPositionForImport(END_DATE_POSITION);
		empty = getItemByPositionForImport(EMPTY_POSITION);
	}

	protected Reg00(Cursor cursorFrom) {
		super(cursorFrom);
		date = getItemByPositionForExport(DATE_POSITION);
		initDate = getItemByPositionForExport(INIT_DATE_POSITION);
		endDate = getItemByPositionForExport(END_DATE_POSITION);
		empty = getItemByPositionForExport(EMPTY_POSITION);
	}
	
	@Override
	public RegType getType() {
		return RegType.REG00;
	}

	@Override
	public ContentValues getImportContentValuesForDataBase() {
		ContentValues result = new ContentValues();
		result.put(Reg00Constants.DATE, date);
		result.put(Reg00Constants.DATE_START, initDate);
		result.put(Reg00Constants.DATE_END, endDate);
		result.put(Reg00Constants.EMPTY, empty);
		return result;
	}

	@Override
	public String getTableNameForDataBase() {
		return Reg00Constants.TABLE_NAME;
	}

	@Override
	public ContentValues getExportFieldNamesForDataBase() {
		ContentValues result = getImportContentValuesForDataBase();
		return result;

	}

	@Override
	public String[] getFieldNamesForDataBase() {

		return new String[] { Reg00Constants.DATE, Reg00Constants.DATE_START,
				Reg00Constants.DATE_END, Reg00Constants.EMPTY,
		};
	}

	@Override
	public boolean supportComunity() {

		return false;
	}

	@Override
	public String getExportLine() {
		// TODO Auto-generated method stub
		return null;
	}

}
