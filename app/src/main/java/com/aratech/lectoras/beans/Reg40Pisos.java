package com.aratech.lectoras.beans;

import android.content.ContentValues;
import android.database.Cursor;

import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg40Constants;

public class Reg40Pisos extends Reg {

	/*
	 * Tipo (N2), Comunidad (N3), Finca (A4), Propietario (A4), Piso (A14),
	 * Ampliación del lector c(70)
	 */

	public String community, property, owner, floor, readerExtension, viewed, serial;

	int COMMUNITY_POSITION = 1;
	int PROPERTY_POSITION = 2;
	int OWNER_POSITION = 3;
	int FLOOR_POSITION = 4;
	int VIEWED_POSITION = 5;
	int READER_EXTENSION_POSITION = 6;
	
	int SERIAL_POSITION = 7;

	protected Reg40Pisos(String lineSource) {
		super(lineSource);
		community = getItemByPositionForImport(COMMUNITY_POSITION);
		property = getItemByPositionForImport(PROPERTY_POSITION);		
		community = removeStartingZerosFromString(community);
		property = removeStartingZerosFromString(property);
		
		owner = getItemByPositionForImport(OWNER_POSITION);
		owner = removeStartingZerosFromString(owner);
		floor = getItemByPositionForImport(FLOOR_POSITION);
		readerExtension = getItemByPositionForImport(READER_EXTENSION_POSITION);
		viewed = getItemByPositionForImport(VIEWED_POSITION);
		if(viewed.isEmpty()){
			viewed = "0";
		}
		serial = getItemByPositionForImport(SERIAL_POSITION);
		if(serial.contains("\"")){
			int serialValue = Integer.valueOf(serial);
			serial = String.valueOf(serialValue);
		}

        //TODO: MES AND KICODE FIX THIS SHIT ON 27/3/15
        readerExtension = readerExtension.replace(community, "");
        readerExtension = readerExtension.replace(property, "");
        readerExtension = readerExtension.replace(floor, "");
	}

	
	
	protected Reg40Pisos(Cursor cursorFrom) {
		super(cursorFrom);
		community = getItemByPositionForExport(COMMUNITY_POSITION);
		property = getItemByPositionForExport(PROPERTY_POSITION);		
		community = removeStartingZerosFromString(community);
		property = removeStartingZerosFromString(property);
		
		owner = getItemByPositionForExport(OWNER_POSITION);
		floor = getItemByPositionForExport(FLOOR_POSITION);
		readerExtension = getItemByPositionForExport(READER_EXTENSION_POSITION);
		viewed = getItemByPositionForExport(VIEWED_POSITION);
		serial = getItemByPositionForExport(SERIAL_POSITION);

        //TODO: MES AND KICODE FIX THIS SHIT ON 27/3/15
        readerExtension = readerExtension.replace(community, "");
        readerExtension = readerExtension.replace(property, "");
        readerExtension = readerExtension.replace(floor, "");
	}
	public void setViewed(boolean viewed){
		this.viewed = viewed ? "1" : "0";
	}
	
	public boolean isViewed(){
		return this.viewed.equals("1");
	}
	@Override
	public RegType getType() {
		return RegType.REG40;
	}

	@Override
	public ContentValues getImportContentValuesForDataBase() {
		ContentValues result = new ContentValues();
		result.put(Reg40Constants.COMMUNITY, community);
		result.put(Reg40Constants.PROPERTY, property);
		result.put(Reg40Constants.FLOOR, floor);
		result.put(Reg40Constants.OWNER, owner);
		result.put(Reg40Constants.READER_EXTENSION, readerExtension);
		result.put(Reg40Constants.VIEWED, viewed);
		result.put(Reg40Constants.SERIAL, serial);
		return result;
	}

	@Override
	public String getTableNameForDataBase() {
		return Reg40Constants.TABLE_NAME;
	}

	@Override
	public ContentValues getExportFieldNamesForDataBase() {
		ContentValues result = getImportContentValuesForDataBase();

		return result;
	}

	@Override
	public String[] getFieldNamesForDataBase() {
		return new String[]{
				Reg40Constants.COMMUNITY,
				Reg40Constants.PROPERTY, 
				Reg40Constants.OWNER, 
				Reg40Constants.FLOOR,
				Reg40Constants.READER_EXTENSION,
				Reg40Constants.VIEWED, 
				Reg40Constants.SERIAL
		};
	}

	@Override
	public String getExportLine() {
		
		StringBuilder resultBuilder = new StringBuilder("40");
		int numberOfZerosToAppend = 4 - community.length();
		
		resultBuilder.append(appendZeroToString(community, numberOfZerosToAppend));
		
		numberOfZerosToAppend = 4 - property.length();		
		resultBuilder.append(appendZeroToString(property, numberOfZerosToAppend));		

		numberOfZerosToAppend = 4 - owner.length();		
		resultBuilder.append(appendZeroToString(owner, numberOfZerosToAppend));
		
		/*numberOfZerosToAppend = 6 - floor.length();
		resultBuilder.append(appendZeroToString(floor, numberOfZerosToAppend)); */

		resultBuilder.append(0);
		
		//numberOfZerosToAppend = 70 - readerExtension.length();
		resultBuilder.append(appendBlankSpacesToString(readerExtension, 1, true));
		
		return resultBuilder.toString();
	}
	@Override
	public boolean supportComunity() {
		return true;
	}

}
