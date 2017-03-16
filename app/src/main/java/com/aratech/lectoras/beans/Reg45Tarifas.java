package com.aratech.lectoras.beans;

import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg45Constants;

import android.content.ContentValues;
import android.database.Cursor;

public class Reg45Tarifas extends Reg {

	/*
	 * Comunidad (A3), Finca (A4), Propietario (A4), Concepto facturado A(2),
	 * Texto concepto A(30), Importe A(15), Consumo A(7)
	 */

	public String community, property, owner, conceptFactured, conceptText, amount,
			consumption;

	public static final int MINIMUM_ID = 41;
	public static final int MAXIMUM_ID = 52;
	
	int COMMUNITY_POSITION = 1;
	int PROPERTY_POSITION = 2;
	int OWNER_POSITION = 3;
	int CONCEPT_POSITION = 4;
	int CONCEPT_TEXT_POSITION = 5;
	int AMOUNT_POSITION = 6;
	int CONSUMPTION_POSITION = 7;

	protected Reg45Tarifas(String lineSource) {
		super(lineSource);
		community = getItemByPositionForImport(COMMUNITY_POSITION);
		property = getItemByPositionForImport(PROPERTY_POSITION);		
		community = removeStartingZerosFromString(community);
		property = removeStartingZerosFromString(property);
		
		owner = getItemByPositionForImport(OWNER_POSITION);
		owner = removeStartingZerosFromString(owner);
		conceptFactured = getItemByPositionForImport(CONCEPT_POSITION);
		conceptText = getItemByPositionForImport(CONCEPT_TEXT_POSITION);
		amount = getItemByPositionForImport(AMOUNT_POSITION);
		consumption = getItemByPositionForImport(CONSUMPTION_POSITION);
	}

	protected Reg45Tarifas(Cursor cursorFrom) {
		super(cursorFrom);
		community = getItemByPositionForExport(COMMUNITY_POSITION);
		property = getItemByPositionForExport(PROPERTY_POSITION);		
		community = removeStartingZerosFromString(community);
		property = removeStartingZerosFromString(property);
		
		owner = getItemByPositionForExport(OWNER_POSITION);
		owner = removeStartingZerosFromString(owner);
		conceptFactured = getItemByPositionForExport(CONCEPT_POSITION);
		conceptText = getItemByPositionForExport(CONCEPT_TEXT_POSITION);
		amount = getItemByPositionForExport(AMOUNT_POSITION);
		consumption = getItemByPositionForExport(CONSUMPTION_POSITION);
	}
	
	@Override
	public RegType getType() {
		return RegType.REG45;
	}

	@Override
	public ContentValues getImportContentValuesForDataBase() {
		ContentValues result = new ContentValues();
		result.put(Reg45Constants.COMMUNITY, community);
		result.put(Reg45Constants.PROPERTY, property);
		result.put(Reg45Constants.OWNER, owner);
		result.put(Reg45Constants.CONCEPT_FACTURED, conceptFactured);
		result.put(Reg45Constants.CONCEPT_TEXT, conceptText);
		result.put(Reg45Constants.AMOUNT, amount);
		result.put(Reg45Constants.CONSUMPTION, consumption);
		return result;
	}

	@Override
	public String getTableNameForDataBase() {
		// TODO Auto-generated method stub
		return Reg45Constants.TABLE_NAME;
	}

	@Override
	public ContentValues getExportFieldNamesForDataBase() {
		ContentValues result = getImportContentValuesForDataBase();
		return result;
	}

	@Override
	public String[] getFieldNamesForDataBase() {
		return new String[]{				
				Reg45Constants.COMMUNITY, 
				Reg45Constants.PROPERTY, 
				Reg45Constants.OWNER,    
				Reg45Constants.CONCEPT_FACTURED,
				Reg45Constants.CONCEPT_TEXT, 
				Reg45Constants.AMOUNT, 
				Reg45Constants.CONSUMPTION
		};
	}

	@Override
	public boolean supportComunity() {
		
		return true;
	}

}
