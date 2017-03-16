package com.aratech.lectoras.beans;

import android.content.ContentValues;
import android.database.Cursor;

import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg60Constants;

public class Reg60Lecturas extends Reg {

	/*
	 * Tipo (N2), Comunidad (A3), Finca (A4), Propietario (A4), Concepto (A2),
	 * Lec/Anterio (A7), Lec/Actual (A7), Indicador de haber leído (0/1),
	 * Importe (N10) en el que se considerarán las dos últimas cifras como
	 * decimales, consumo n(5), fecha anterior c(10) (dd/mm/aaaa)
	 */

	public String community, property, owner, concept, previousRead,
			currentRead, haveRead, amount, average, type, date, previousType,
			serial;

	public boolean saved;
	
	public int COMMUNITY_POSITION = 1;
	public int PROPERTY_POSITION = 2;
	public int OWNER_POSITION = 3;
	public int CONCEPT_POSITION = 4;
	public int PREVIOUS_READ_POSITION = 5;
	public int CURRENT_READ_POSITION = 6;
	public int HAVE_READ_POSITION = 7;
	public int AMOUNT_POSITION = 8;
	public int AVERAGE_POSITION = 9;	
	public int DATE_POSITION = 10;
	public int SERIAL_POSITION = 11;
	public int PREVIOUS_TYPE_POSITION = 12;
	public int TYPE_POSITION = 13;
	public int SAVE_POSITION = 14;

	protected Reg60Lecturas(String lineSource) {
		super(lineSource);
		
		community = getItemByPositionForImport(COMMUNITY_POSITION);
		property = getItemByPositionForImport(PROPERTY_POSITION);		
		community = removeStartingZerosFromString(community);
		property = removeStartingZerosFromString(property);
		
		owner = getItemByPositionForImport(OWNER_POSITION);
		owner = removeStartingZerosFromString(owner);
		
		concept = getItemByPositionForImport(CONCEPT_POSITION);
		previousRead = getItemByPositionForImport(PREVIOUS_READ_POSITION);
		currentRead = getItemByPositionForImport(CURRENT_READ_POSITION);
		currentRead = removeStartingZerosFromString(currentRead);
		haveRead = getItemByPositionForImport(HAVE_READ_POSITION);
		amount = getItemByPositionForImport(AMOUNT_POSITION);
		average = getItemByPositionForImport(AVERAGE_POSITION);
		date = getItemByPositionForImport(DATE_POSITION);
		previousType = getItemByPositionForImport(PREVIOUS_TYPE_POSITION);
		serial = getItemByPositionForImport(SERIAL_POSITION);
		try{
			int serialNumber = Integer.valueOf(serial);
			if(serialNumber == 0){
				serial = "";
			}
		}catch(Exception ex){		
		}
		type = "";
		saved = false;
	}

	protected Reg60Lecturas(Cursor cursorFrom) {
		super(cursorFrom);
		community = getItemByPositionForExport(COMMUNITY_POSITION);
		property = getItemByPositionForExport(PROPERTY_POSITION);
		community = removeStartingZerosFromString(community);
		property = removeStartingZerosFromString(property);
		
		owner = getItemByPositionForExport(OWNER_POSITION);
		concept = getItemByPositionForExport(CONCEPT_POSITION);
		previousRead = getItemByPositionForExport(PREVIOUS_READ_POSITION);
		currentRead = getItemByPositionForExport(CURRENT_READ_POSITION);
		haveRead = getItemByPositionForExport(HAVE_READ_POSITION);	
			
		date = getItemByPositionForExport(DATE_POSITION);
		previousType = getItemByPositionForExport(PREVIOUS_TYPE_POSITION);
		serial = getItemByPositionForExport(SERIAL_POSITION);
		amount = getItemByPositionForExport(AMOUNT_POSITION);
		average = getItemByPositionForExport(AVERAGE_POSITION);
		type = getItemByPositionForExport(TYPE_POSITION);
		saved = getItemByPositionForExport(SAVE_POSITION).length() > 0;
	}

	@Override
	public RegType getType() {
		return RegType.REG60;
	}
/*	public int COMMUNITY_POSITION = 1;
	public int PROPERTY_POSITION = 2;
	public int OWNER_POSITION = 3;
	public int CONCEPT_POSITION = 4;
	public int PREVIOUS_READ_POSITION = 5;
	public int CURRENT_READ_POSITION = 6;
	public int HAVE_READ_POSITION = 7;
	public int AVERAGE_POSITION = 8;
	public int TYPE_POSITION = 9;
	public int DATE_POSITION = 10;
	public int PREVIOUS_TYPE_POSITION = 11;
	public int SERIAL_POSITION = 12;
	public int AMOUNT_POSITION = 13;*/
	@Override
	public ContentValues getImportContentValuesForDataBase() {
		ContentValues result = new ContentValues();
		result.put(Reg60Constants.COMMUNITY, community);
		result.put(Reg60Constants.PROPERTY, property);
		result.put(Reg60Constants.OWNER, owner);
		result.put(Reg60Constants.CONCEPT, concept);
		result.put(Reg60Constants.PREVIOUS_READ, previousRead);
		result.put(Reg60Constants.CURRENT_READ, currentRead);
		result.put(Reg60Constants.HAVE_READ, haveRead);
		result.put(Reg60Constants.AVERAGE, average);
		result.put(Reg60Constants.TYPE, type);
		result.put(Reg60Constants.DATE, date);
		result.put(Reg60Constants.PREVIOUS_TYPE, previousType);
		result.put(Reg60Constants.SERIAL, serial);
		result.put(Reg60Constants.AMOUNT, amount);
		String mSave = "";
		if(saved == true){
			mSave = "OK";
		}
		result.put(Reg60Constants.SAVED, mSave);
		return result;
	}

	@Override
	public String getTableNameForDataBase() {
		return Reg60Constants.TABLE_NAME;
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
	public String getExportLine() {
		StringBuilder resultBuilder = new StringBuilder("60");
		int numberOfZerosToAppend = 4 - community.length();
		
		resultBuilder.append(appendZeroToString(community, numberOfZerosToAppend));
		
		numberOfZerosToAppend = 4 - property.length();		
		resultBuilder.append(appendZeroToString(property, numberOfZerosToAppend));		

		numberOfZerosToAppend = 4 - owner.length();		
		resultBuilder.append(appendZeroToString(owner, numberOfZerosToAppend));
		
		numberOfZerosToAppend = 2 - concept.length();		
		resultBuilder.append(appendZeroToString(concept, numberOfZerosToAppend));
		
		numberOfZerosToAppend = 7 - previousRead.length();		
		resultBuilder.append(appendZeroToString(previousRead, numberOfZerosToAppend));
		
		numberOfZerosToAppend = 7 - currentRead.length();		
		resultBuilder.append(appendZeroToString(currentRead, numberOfZerosToAppend));
		
		resultBuilder.append(haveRead);
		
		// Esto está así en el código original, ni idea de porqué
		String amount ="0";
		resultBuilder.append(amount);
		//
		
		int numberOfBlanksToAppend = 30 - type.length();		
		resultBuilder.append(appendBlankSpacesToString(type, numberOfBlanksToAppend, true));
		
		return resultBuilder.toString();
	}
	
	@Override
	public boolean supportComunity() {
		return false;
	}

	public boolean isSaved(){
		return this.saved;
	}
	public void setSaved(boolean isSaved){
		saved = isSaved;
	}
}
