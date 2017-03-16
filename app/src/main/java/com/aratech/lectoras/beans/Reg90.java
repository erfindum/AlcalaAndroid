package com.aratech.lectoras.beans;

import android.content.ContentValues;
import android.database.Cursor;

import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg90Constants;

public class Reg90 extends Reg {
	/* Tipo(N2), Total registros (A11), Vacío (A17) */

	String totalRegs, empty;

	int TOTAL_REGS_POSITION = 1;
	int EMPTY_POSITION = 2;

	protected Reg90(String lineSource) {
		super(lineSource);
		totalRegs = getItemByPositionForImport(TOTAL_REGS_POSITION);
		empty = getItemByPositionForImport(EMPTY_POSITION);
	}

	protected Reg90(Cursor cursorFrom) {
		super(cursorFrom);
		totalRegs = getItemByPositionForExport(TOTAL_REGS_POSITION);
		empty = getItemByPositionForExport(EMPTY_POSITION);
	}
	
	@Override
	public RegType getType() {
		return RegType.REG90;
	}

	@Override
	public ContentValues getImportContentValuesForDataBase() {
		ContentValues result = new ContentValues();
		result.put(Reg90Constants.TOTAL_REG, totalRegs);
		if (getSplitedLine()[EMPTY_POSITION] != null)
			result.put(Reg90Constants.EMPTY, empty);
		return result;
	}

	@Override
	public String getTableNameForDataBase() {
		return Reg90Constants.TABLE_NAME;
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
