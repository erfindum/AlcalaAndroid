package com.aratech.lectoras.data.classes;

/**
 * Created by RAAJA on 29-04-2017.
 */

public class ReadingDatabaseSQL_statements {

    public static final String READING_DATABASE_NAME = "Lectura_de_antena.db";
    public static final int DATABASE_VERSION = 1;

    public static final String READING_DATABSE_TABLE_NAME = "lecturas_de_la_antena";

    public static final String CIC = "cic";
    public static final String ANTENNA_TYPE = "tipo_de_antena";
    public static final String DATE = "fecha";
    public static final String ANTENNA_READINGS = "lecturas_de_antena";

    public static final String CREATE_READING_TABLE = "CREATE TABLE "+READING_DATABSE_TABLE_NAME+
            "("+CIC+" text , "+ANTENNA_TYPE +" text , "+DATE+" text , "+ ANTENNA_READINGS + " text );";

    public static final String DROP_READING_TABLE = "DROP TABLE IF EXISTS "+READING_DATABSE_TABLE_NAME;
}
