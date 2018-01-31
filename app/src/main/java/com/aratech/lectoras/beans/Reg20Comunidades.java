package com.aratech.lectoras.beans;

import com.aratech.lectoras.data.classes.RegDataBaseSQL_statements.Reg20Constants;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.Objects;

public class Reg20Comunidades extends Reg {

	/*
	 * Tipo (N2), Comunidad (A3), Finca (A4), Domicilio de la finca (A26),
	 * Nombre del administrador c(60)
	 */

	public String community, property, address, initDate, endDate, viewed, adminName;

	int COMMUNITY_POSITION = 1;
	int PROPERTY_POSITION = 2;
	int ADDRES_POSITION = 3;
	int INIT_DATE_POSITION = 4;
	int END_DATE_POSITION = 5;
	int VIEWED_POSITION = 6;
	int ADMIN_NAME_POSITION = 7;

	protected Reg20Comunidades(String lineSource) {
		super(lineSource);
		community = getItemByPositionForImport(COMMUNITY_POSITION);
		property = getItemByPositionForImport(PROPERTY_POSITION);		
		community = removeStartingZerosFromString(community);
		property = removeStartingZerosFromString(property);
		
		address = getItemByPositionForImport(ADDRES_POSITION);
		initDate = "";
		endDate = "";
		viewed ="";
		adminName = getItemByPositionForImport(4);
	}

	protected Reg20Comunidades(Cursor cursorFrom) {
		super(cursorFrom);
		community = getItemByPositionForExport(COMMUNITY_POSITION);
		property = getItemByPositionForExport(PROPERTY_POSITION);
		
		community = removeStartingZerosFromString(community);
		property = removeStartingZerosFromString(property);
		
		address = getItemByPositionForExport(ADDRES_POSITION);
		initDate = getItemByPositionForExport(INIT_DATE_POSITION);
		endDate = getItemByPositionForExport(END_DATE_POSITION);
		viewed = getItemByPositionForExport(VIEWED_POSITION);
		adminName = getItemByPositionForExport(ADMIN_NAME_POSITION);
	}

	public int getCommunityNumber() {
		try {
			return Integer.valueOf(community);
		} catch (Exception ex) {
			return -1;
		}
	}

	public String getCommunityStringNumber(){
		return community;
	}
	public int getPropertyNumber(){
		try {
			return Integer.valueOf(property);
		} catch (Exception ex) {
			return -1;
		}
	}
	public String getPropertyStringNumber(){
		return property;
	}
	public String getCommunityAddress(){
		return this.address;
	}
	
	public void setViewed(boolean viewed){
		this.viewed = viewed ? "1" : "0";
	}
	
	public boolean isViewed(){
		return this.viewed.equals("1");
	}
	@Override
	public RegType getType() {
		return RegType.REG20_COMUNIDADES;
	}

	@Override
	public ContentValues getImportContentValuesForDataBase() {
		ContentValues result = new ContentValues();
		result.put(Reg20Constants.COMMUNITY, community);
		result.put(Reg20Constants.PROPERTY, property);
		result.put(Reg20Constants.ADDRESS, address);
		result.put(Reg20Constants.ADMIN_NAME, adminName);
		return result;
	}

	@Override
	public String getTableNameForDataBase() {
		return Reg20Constants.TABLE_NAME;
	}

	@Override
	public ContentValues getExportFieldNamesForDataBase() {
		ContentValues result = getImportContentValuesForDataBase();
		result.put(Reg20Constants.DATE_START, initDate);
		result.put(Reg20Constants.DATE_END, endDate);
		result.put(Reg20Constants.VIEWED, viewed);
		return result;
	}

	@Override
	public String[] getFieldNamesForDataBase() {
		return new String[] { Reg20Constants.COMMUNITY,
				Reg20Constants.PROPERTY, Reg20Constants.ADDRESS,
				Reg20Constants.DATE_START, Reg20Constants.DATE_END,
				Reg20Constants.VIEWED, Reg20Constants.ADMIN_NAME };
	}

	@Override
	public boolean supportComunity() {
		return false;
	}

	@Override
	public String getExportLine() {
		//StringBuilder resultBuilder = new StringBuilder(this.getType().getId());
		StringBuilder resultBuilder = new StringBuilder("20");
		int numberOfZerosToAppend = 4 - community.length();
		
		if(numberOfZerosToAppend > 0) {
			resultBuilder.append(appendZeroToString(community, numberOfZerosToAppend));
		}else{
			resultBuilder.append(community);
		}
		
		numberOfZerosToAppend = 4 - property.length();
		if(numberOfZerosToAppend > 0) {
			resultBuilder.append(appendZeroToString(property, numberOfZerosToAppend));
		}else {
			resultBuilder.append(property);
		}
		
		resultBuilder.append(initDate);
		resultBuilder.append(endDate);
		
		return resultBuilder.toString();
	}

	@Override
	public boolean equals(Object o) {
		boolean isEqual = false;
		if(o!=null && o instanceof Reg20Comunidades){
			Reg20Comunidades reg20Comunidades = (Reg20Comunidades) o;
			isEqual = this.community.equals(reg20Comunidades.community);
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31*result+community.hashCode();
		return result;
	}
}
