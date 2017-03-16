package com.aratech.lectoras.data.classes;

import com.aratech.lectoras.beans.Reg.RegType;

public final class RegDataBaseSQL_statements {

	private RegDataBaseSQL_statements() {
	}

	public static String getSelectForRegTable(RegType regType) {
		StringBuilder resultBuilder = new StringBuilder("SELECT * FROM ");

		switch (regType) {

		case REG00:
			resultBuilder.append(Reg00Constants.TABLE_NAME);
			break;
		case REG10_CONCEPTOS:
			resultBuilder.append(Reg10Constants.TABLE_NAME);
			break;
		case REG20_COMUNIDADES:
			resultBuilder.append(Reg20Constants.TABLE_NAME);
			break;
		case REG40:
			resultBuilder.append(Reg40Constants.TABLE_NAME);
			break;
		case REG45:
			resultBuilder.append(Reg45Constants.TABLE_NAME);
			break;
		case REG60:
			resultBuilder.append(Reg60Constants.TABLE_NAME);
			break;
		case REG90:
			resultBuilder.append(Reg90Constants.TABLE_NAME);
			break;
		case REG99:
			resultBuilder = new StringBuilder("SELECT DISTINCT (");
			resultBuilder.append(Reg99Constants.READ_CODE);
			resultBuilder.append("), ");
			resultBuilder.append(Reg99Constants.READ_TEXT);
			resultBuilder.append(" FROM ");
			resultBuilder.append(Reg99Constants.TABLE_NAME);
			resultBuilder.append(" ORDER BY ");
			resultBuilder.append(Reg99Constants.READ_CODE);
		}

		return resultBuilder.toString();
	}

	public static final String REG00_TABLE_CREATE = "CREATE TABLE "
			+ Reg00Constants.TABLE_NAME + "(" + Reg00Constants.DATE
			+ " text , " + Reg00Constants.DATE_START + " text , "
			+ Reg00Constants.DATE_END + " text , " + Reg00Constants.EMPTY
			+ " text );";

	public static final String REG00_TABLE_DROP = "DROP TABLE IF EXISTS "
			+ Reg00Constants.TABLE_NAME;

	public static final String REG10_TABLE_CREATE = "CREATE TABLE "
			+ Reg10Constants.TABLE_NAME + "(" + Reg10Constants.COMMUNITY
			+ " text , " + Reg10Constants.CONCEPT + " text , "
			+ Reg10Constants.TITLE + " text , " + Reg10Constants.EMPTY
			+ " text );";

	public static final String REG10_TABLE_DROP = "DROP TABLE IF EXISTS "
			+ Reg10Constants.TABLE_NAME;

	public static final String REG20_TABLE_CREATE = "CREATE TABLE "
			+ Reg20Constants.TABLE_NAME + "(" + Reg20Constants.COMMUNITY
			+ " text , " + Reg20Constants.PROPERTY + " text , "
			+ Reg20Constants.ADDRESS + " text , " + Reg20Constants.DATE_START
			+ " text , " + Reg20Constants.DATE_END + " text , "
			+ Reg20Constants.VIEWED + " text , " + Reg20Constants.ADMIN_NAME
			+ " text );";

	public static final String REG20_TABLE_DROP = "DROP TABLE IF EXISTS "
			+ Reg20Constants.TABLE_NAME;

	public static final String REG40_TABLE_CREATE = "CREATE TABLE "
			+ Reg40Constants.TABLE_NAME + "(" + Reg40Constants.COMMUNITY
			+ " integer, " + Reg40Constants.PROPERTY + " text , "
			+ Reg40Constants.OWNER + " text , " + Reg40Constants.FLOOR
			+ " text , " + Reg40Constants.VIEWED + " text , "
			+ Reg40Constants.READER_EXTENSION + " text , "
			+ Reg40Constants.SERIAL + " text );";

	public static final String REG40_TABLE_DROP = "DROP TABLE IF EXISTS "
			+ Reg40Constants.TABLE_NAME;

	public static final String REG45_TABLE_CREATE = "CREATE TABLE "
			+ Reg45Constants.TABLE_NAME + "(" + Reg40Constants.COMMUNITY
			+ " integer, " + Reg45Constants.PROPERTY + " text , "
			+ Reg45Constants.OWNER + " text , "
			+ Reg45Constants.CONCEPT_FACTURED + " text , "
			+ Reg45Constants.CONCEPT_TEXT + " text , " + Reg45Constants.AMOUNT
			+ " text , " + Reg45Constants.CONSUMPTION + " text );";

	public static final String REG45_TABLE_DROP = "DROP TABLE IF EXISTS "
			+ Reg45Constants.TABLE_NAME;

	/*
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
	 */
	
	public static final String REG60_TABLE_CREATE = "CREATE TABLE "
			+ Reg60Constants.TABLE_NAME + "(" + Reg60Constants.COMMUNITY
			+ " text , " + Reg60Constants.PROPERTY + " text , "
			+ Reg60Constants.OWNER + " text , " + Reg60Constants.CONCEPT
			+ " text , " + Reg60Constants.PREVIOUS_READ + " text , "
			+ Reg60Constants.CURRENT_READ + " text , "
			+ Reg60Constants.HAVE_READ + " integer, " + Reg60Constants.AMOUNT
			+ " text , " + Reg60Constants.AVERAGE + " text , "
			+ Reg60Constants.DATE + " text , " + Reg60Constants.SERIAL
			+ " text , " + Reg60Constants.PREVIOUS_TYPE + " text , "+ Reg60Constants.TYPE + " text , "
			+ Reg60Constants.SAVED+ " text );";

	public static final String REG60_TABLE_DROP = "DROP TABLE IF EXISTS "
			+ Reg60Constants.TABLE_NAME;

	public static final String REG90_TABLE_CREATE = "CREATE TABLE "
			+ Reg90Constants.TABLE_NAME + "(" + Reg90Constants.TOTAL_REG
			+ " text , " + Reg90Constants.EMPTY + " text );";

	public static final String REG90_TABLE_DROP = "DROP TABLE IF EXISTS "
			+ Reg90Constants.TABLE_NAME;

	public static final String REG99_TABLE_CREATE = "CREATE TABLE "
			+ Reg99Constants.TABLE_NAME + "(" + Reg99Constants.READ_CODE
			+ " integer PRIMARY KEY not null, " + Reg99Constants.READ_TEXT
			+ " text );";

	public static final String REG99_TABLE_DROP = "DROP TABLE IF EXISTS "
			+ Reg99Constants.TABLE_NAME;
	public static final String DATABASE_NAME = "LecturasContadoresDB";

	public final class Reg00Constants {
		private Reg00Constants() {
		}

		public static final String TABLE_NAME = "REG00";
		public static final String DATE = "Fecha";
		public static final String DATE_START = "Fechaini";
		public static final String DATE_END = "Fechafin";
		public static final String EMPTY = "Vacio";
	}

	public final class Reg10Constants {
		private Reg10Constants() {
		}

		public static final String TABLE_NAME = "REG10";
		public static final String COMMUNITY = "Comunidad";
		public static final String CONCEPT = "Concepto";
		public static final String TITLE = "Titulo";
		public static final String EMPTY = "Vacio";
	}

	public final class Reg20Constants {
		private Reg20Constants() {
		}

		public static final String TABLE_NAME = "REG20";
		public static final String COMMUNITY = "Comunidad";
		public static final String PROPERTY = "Finca";
		public static final String ADDRESS = "Domicilio";
		public static final String DATE_START = "Fechaini";
		public static final String DATE_END = "Fechafin";
		public static final String VIEWED = "Visto";
		public static final String ADMIN_NAME = "nombre_administrador";
	}

	public final class Reg40Constants {
		private Reg40Constants() {
		}

		public static final String TABLE_NAME = "REG40";
		public static final String COMMUNITY = "Comunidad";
		public static final String PROPERTY = "Finca";
		public static final String OWNER = "Propietario";
		public static final String FLOOR = "Piso";
		public static final String READER_EXTENSION = "Ampliacion_Lector";
		public static final String VIEWED = "Visto";
		public static final String SERIAL = "Serie";
	}

	public final class Reg45Constants {
		private Reg45Constants() {
		}

		public static final String TABLE_NAME = "REG45";
		public static final String COMMUNITY = "Comunidad";
		public static final String PROPERTY = "Finca";
		public static final String OWNER = "Propietario";
		public static final String CONCEPT_FACTURED = "Concepto_Facturado";
		public static final String CONCEPT_TEXT = "Texto_Concepto";
		public static final String AMOUNT = "Ampliacion_Lector";
		public static final String CONSUMPTION = "Consumo";
	}

	public final class Reg60Constants {
		private Reg60Constants() {
		}

		public static final String TABLE_NAME = "REG60";
		public static final String COMMUNITY = "Comunidad";
		public static final String PROPERTY = "Finca";
		public static final String OWNER = "Propietario";
		public static final String CONCEPT = "Concepto";
		public static final String PREVIOUS_READ = "Lec_anterior";
		public static final String CURRENT_READ = "Lec_actual";
		public static final String HAVE_READ = "Leido";
		public static final String AMOUNT = "Importe";
		public static final String AVERAGE = "Media";
		public static final String TYPE = "Tipo";
		public static final String DATE = "Fecha";
		public static final String PREVIOUS_TYPE = "Tipo_anterior";
		public static final String SERIAL = "Serie";
		public static final String SAVED = "Guardado";
	}

	public final class Reg90Constants {
		private Reg90Constants() {
		}

		public static final String TABLE_NAME = "REG90";
		public static final String TOTAL_REG = "Total_reg";
		public static final String EMPTY = "Vacio";
	}

	public final class Reg99Constants {
		private Reg99Constants() {
		}

		public static final String TABLE_NAME = "REG99";
		public static final String READ_CODE = "Codigo_Lectura";
		public static final String READ_TEXT = "Texto_Lectura";
	}
}
