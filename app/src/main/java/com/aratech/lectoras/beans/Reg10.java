package com.aratech.lectoras.beans;

import android.content.ContentValues;
import android.database.Cursor;

import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg10Constants;

public class Reg10 extends Reg {

	/*
	 * Tipo (N2), Concepto de recibo (A2), Título del concepto (A21), Vacío
	 * (A10)
	 */

	public String community, concept, title, empty;

	int COMMUNITY_POSITION = 1;
	int CONCEPT_POSITION = 2;
	int TITLE_POSITION = 3;
	int EMPTY_POSITION = 4;

	protected Reg10(String lineSource) {
		super(lineSource);
		community = getItemByPositionForImport(COMMUNITY_POSITION);
		concept = getItemByPositionForImport(CONCEPT_POSITION);		
		community = removeStartingZerosFromString(community);
		concept = removeStartingZerosFromString(concept);
		
		title = getItemByPositionForImport(TITLE_POSITION);
		empty = getItemByPositionForImport(EMPTY_POSITION);
	}

	protected Reg10(Cursor cursorFrom) {
		super(cursorFrom);
		community = getItemByPositionForExport(COMMUNITY_POSITION);		
		community = removeStartingZerosFromString(community);
		
		concept = getItemByPositionForExport(CONCEPT_POSITION);
		title = getItemByPositionForExport(TITLE_POSITION);
		empty = getItemByPositionForExport(EMPTY_POSITION);
	}
	@Override
	public RegType getType() {
		return RegType.REG10_CONCEPTOS;
	}

	@Override
	public ContentValues getImportContentValuesForDataBase() {
		ContentValues result = new ContentValues();
		result.put(Reg10Constants.COMMUNITY, community);
		result.put(Reg10Constants.CONCEPT, concept);
		result.put(Reg10Constants.TITLE, title);
		result.put(Reg10Constants.EMPTY, empty);
		return result;
	}

	@Override
	public String getTableNameForDataBase() {

		return Reg10Constants.TABLE_NAME;
	}

	@Override
	public ContentValues getExportFieldNamesForDataBase() {
		ContentValues result = getImportContentValuesForDataBase();
		return result;
	}

	@Override
	public String[] getFieldNamesForDataBase() {
		return new String[] { Reg10Constants.COMMUNITY, Reg10Constants.CONCEPT,
				Reg10Constants.TITLE, Reg10Constants.EMPTY
		};
	}

	@Override
	public boolean supportComunity() {
		return true;
	}



}
