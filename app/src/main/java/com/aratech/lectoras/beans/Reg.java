package com.aratech.lectoras.beans;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class Reg {

	private static final String DEFAULT_SEPARATOR = ";";
	protected String originalLine;
	private String[] splitedLine;

	protected Reg(){};
	
	protected Reg(String lineSource) {
		originalLine = lineSource;
		setSplitedLine(lineSource.split(DEFAULT_SEPARATOR));
		trimSplittedLine();
		removeStringItemsFromSplittedLine();
	}

	private void removeStringItemsFromSplittedLine(){
		if (splitedLine != null)
			for (int i = 0; i < splitedLine.length; i++) {
				splitedLine[i] = splitedLine[i] != null ? splitedLine[i].replace("\"", "") : "";
			}
	}
	protected Reg(Cursor cursorFrom) {
		int columnNumber = cursorFrom.getColumnCount();
		splitedLine = new String[columnNumber];
		for (int i = 0; i < columnNumber; i++) {
			splitedLine[i] = cursorFrom.getString(i);
		}
		trimSplittedLine();
	}

	private void trimSplittedLine() {
		if (splitedLine != null)
			for (int i = 0; i < splitedLine.length; i++) {
				splitedLine[i] = splitedLine[i] != null ? splitedLine[i].trim() : "";
			}
	}

	protected String appendZeroToString(String originalString,
			int numberOfZeroToAppend) {
		StringBuilder resultBuilder = new StringBuilder();
		if (numberOfZeroToAppend > 0) {
			for (int i = 0; i < numberOfZeroToAppend; i++) {
				resultBuilder.append(0);
			}
		}
		resultBuilder.append(originalString);
		return resultBuilder.toString();
	}

	protected String appendBlankSpacesToString(String originalString, int numberOfBlankSpacesToapend, boolean appendLeft){
		StringBuilder resultBuilder = new StringBuilder();
	
		if (numberOfBlankSpacesToapend > 0) {
			for (int i = 0; i < numberOfBlankSpacesToapend; i++) {
				resultBuilder.append(" ");
			}
		}
		
		if(appendLeft)
			resultBuilder.append(originalString);
		else{
			resultBuilder = new StringBuilder(originalString).append(resultBuilder);
		}
		
		return resultBuilder.toString();
	}
	protected String removeStartingZerosFromString(String originalString) {
		if (originalString.startsWith("0")) {
			do {
				originalString = originalString.replaceFirst("0", "");

			} while (originalString.startsWith("0"));
		}
		return originalString;
	}

	protected String getItemByPositionForImport(int position) {
		if (isEmptyPosition(position)) {
			return "";
		} else {
			return getSplitedLine()[position];
		}
	}

	protected String getItemByPositionForExport(int position) {
		// En la bbdd coincide el orden con el de importación del fichero, así
		// que cómo las posiciones empezaban
		// en 1 para saltarse el primer campo de la importación (posición 0),
		// para la exportación tengo que coger
		// el primer registro que sí que necesito que sea 0.
		if (isEmptyPosition(position - 1)) {
			return "";
		} else {
			return getSplitedLine()[position - 1];
		}
	}

	protected boolean isEmptyPosition(int position) {
		return getSplitedLine()[position] == null
				|| getSplitedLine()[position].isEmpty();
	}

	public String getExportLine() {
		return null;
	};

	public abstract RegType getType();

	public abstract ContentValues getImportContentValuesForDataBase();

	public abstract ContentValues getExportFieldNamesForDataBase();

	public abstract String getTableNameForDataBase();

	public abstract String[] getFieldNamesForDataBase();

	public abstract boolean supportComunity();

	public static Reg loadRegObjectFromCursor(Cursor cursorFrom,
			RegType returnType) {

		Reg resultReg = null;

		switch (returnType) {
		case REG00:
			resultReg = new Reg00(cursorFrom);
			break;
		case REG10_CONCEPTOS:
			resultReg = new Reg10(cursorFrom);
			break;
		case REG20_COMUNIDADES:
			resultReg = new Reg20Comunidades(cursorFrom);
			break;
		case REG40:
			resultReg = new Reg40Pisos(cursorFrom);
			break;
		case REG45:
			resultReg = new Reg45Tarifas(cursorFrom);
			break;
		case REG60:
			resultReg = new Reg60Lecturas(cursorFrom);
			break;
		case REG90:
			resultReg = new Reg90(cursorFrom);
			break;
		case REG99:
			resultReg = new Reg99TipoLectura(cursorFrom);
		}
		return resultReg;
	}

	public static Reg loadRegObjectFromImportLine(String line) {
		Reg resultReg = null;
		try {
			if (line != null) {
				int id = Integer.valueOf(line.split(DEFAULT_SEPARATOR)[0]);

				if (id == (RegType.REG00.getId())) {
					resultReg = new Reg00(line);
				} else if (id == (RegType.REG10_CONCEPTOS.getId())) {
					resultReg = new Reg10(line);
				} else if (id == (RegType.REG20_COMUNIDADES.getId())) {
					resultReg = new Reg20Comunidades(line);
				} else if (id == (RegType.REG40.getId())) {
					resultReg = new Reg40Pisos(line);
				} else if (id == (RegType.REG60.getId())) {
					resultReg = new Reg60Lecturas(line);
				} else if (id == (RegType.REG90.getId())) {
					resultReg = new Reg90(line);
				} else if (id == (RegType.REG99.getId())) {
					resultReg = new Reg99TipoLectura(line);
				} else if (id >= Reg45Tarifas.MINIMUM_ID
						&& id <= Reg45Tarifas.MAXIMUM_ID) {
					resultReg = new Reg45Tarifas(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultReg = null;
		}
		return resultReg;
	}

	protected String[] getSplitedLine() {
		return splitedLine;
	}

	protected void setSplitedLine(String[] splitedLine) {
		this.splitedLine = splitedLine;
	}

	public enum RegType {
		REG00, REG10_CONCEPTOS, REG20_COMUNIDADES, REG40, REG60, REG90, REG99, REG45;

		public int getId() {
			int result = -1;
			switch (this) {
			case REG00:
				result = 0;
				break;
			case REG10_CONCEPTOS:
				result = 10;
				break;
			case REG20_COMUNIDADES:
				result = 20;
				break;
			case REG40:
				result = 40;
				break;
			case REG45:
				result = 45;
				break;
			case REG60:
				result = 60;
				break;
			case REG90:
				result = 90;
				break;
			case REG99:
				result = 99;
			}
			return result;
		}
	}
}
