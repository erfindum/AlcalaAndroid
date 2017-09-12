package com.aratech.lectoras.data.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RAAJA on 29-04-2017.
 */

public class ReadingDatabaseHelper extends SQLiteOpenHelper {


    public ReadingDatabaseHelper(Context context) {
        super(context, ReadingDatabaseSQL_statements.READING_DATABASE_NAME, null , ReadingDatabaseSQL_statements.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ReadingDatabaseSQL_statements.CREATE_READING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ ReadingDatabaseSQL_statements.READING_DATABSE_TABLE_NAME);
        sqLiteDatabase.execSQL(ReadingDatabaseSQL_statements.CREATE_READING_TABLE);
    }
}
