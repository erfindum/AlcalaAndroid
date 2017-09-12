package com.aratech.lectoras;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aratech.lectoras.data.classes.ReadingDatabaseHelper;
import com.aratech.lectoras.data.classes.ReadingDatabaseSQL_statements;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by RAAJA on 04-05-2017.
 */

public class ReadingExportActivity extends Activity {

    Button btnExport, btnDelete;
    EditText etFileName;
    public static final String exportDirectory = "LECTURAS EXPORTADAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_export);
        btnExport = (Button) findViewById(R.id.btnExportar);
        btnDelete = (Button) findViewById(R.id.btnEliminar);
        etFileName = (EditText) findViewById(R.id.etFileName);
        setListeners();
    }

    private void setListeners(){
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etFileName.getText().toString().equals("")){
                    Toast.makeText(ReadingExportActivity.this, "Introduce un nombre para el fichero", Toast.LENGTH_LONG).show();
                }else{
                    new AntennaReadingAsyncTask().execute("export_readings",etFileName.getText().toString());
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayEliminarDialog();
            }
        });
    }

    private void displayEliminarDialog(){
        final AlertDialog removeReadingsDialog = new AlertDialog.Builder(ReadingExportActivity.this).create();
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_antena_eliminar,null);
        removeReadingsDialog.setView(dialogView);
        Button removeReadings,cancel;
        removeReadings = (Button) dialogView.findViewById(R.id.btnOk);
        cancel = (Button) dialogView.findViewById(R.id.btnCancel);
        removeReadings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeReadingsDialog.dismiss();
                new AntennaReadingAsyncTask().execute("delete_readings");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeReadingsDialog.dismiss();
            }
        });
        removeReadingsDialog.show();
    }

    private void createLoadingDialog(){

    }

    private String getCurrentDate(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-dd-MM-yyyy");
        return sdf.format(cal.getTime());
    }

    private class AntennaReadingAsyncTask extends AsyncTask<String,Void,String>{
        AlertDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = new AlertDialog.Builder(ReadingExportActivity.this).create();
            View loadingView = getLayoutInflater().inflate(R.layout.dialog_wait,null);
            loadingDialog.setView(loadingView);
            TextView loadingTitle = (TextView) loadingView.findViewById(R.id.tvDialog);
            loadingTitle.setText("Exportación de lecturas...");
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            SQLiteDatabase readingDatabase = new ReadingDatabaseHelper(ReadingExportActivity.this)
                                .getWritableDatabase();
            if(strings[0].equals("export_readings")){
                String[] projection = {ReadingDatabaseSQL_statements.CIC,ReadingDatabaseSQL_statements.ANTENNA_TYPE,
                        ReadingDatabaseSQL_statements.DATE,ReadingDatabaseSQL_statements.ANTENNA_READINGS};
                Cursor cursor = readingDatabase.query(ReadingDatabaseSQL_statements.READING_DATABSE_TABLE_NAME,
                        projection,null,null,null,null,null);
                if(cursor==null){
                    return "Lecturas de la antena no disponibles";
                }
                if(cursor.getCount()==0){
                    cursor.close();
                    return "Lecturas de la antena no disponibles";
                }
                cursor.moveToFirst();
                StringBuilder readingStringBuilder = new StringBuilder();
                do{
                    String read = cursor.getString(cursor.getColumnIndex(ReadingDatabaseSQL_statements.CIC))
                            +"/"+cursor.getString(cursor.getColumnIndex(ReadingDatabaseSQL_statements.DATE))
                            +"/"+cursor.getString(cursor.getColumnIndex(ReadingDatabaseSQL_statements.ANTENNA_TYPE))
                            +"/"+cursor.getString(cursor.getColumnIndex(ReadingDatabaseSQL_statements.ANTENNA_READINGS));
                    readingStringBuilder.append(read);
                    readingStringBuilder.append("\n");
                }while (cursor.moveToNext());
                String result = "";
                try{
                    File exportDirectoryFile = new File(Environment.getExternalStorageDirectory()+File.separator+exportDirectory);
                    if(!exportDirectoryFile.exists()){
                        exportDirectoryFile.mkdirs();
                    }
                    File exportFile = new File(exportDirectoryFile,strings[1]+".txt");
                    if(!exportFile.exists()){
                        exportFile.createNewFile();
                    }else{
                        exportFile = new File(exportDirectoryFile,strings[1]+getCurrentDate()+".txt");
                        exportFile.createNewFile();
                    }
                    BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(exportFile));
                    byte[] readingBuffer = readingStringBuilder.toString().getBytes();
                    outStream.write(readingBuffer);
                    outStream.flush();
                    outStream.close();
                    result = "Lecturas exportadas correctamente";
                }catch (FileNotFoundException e){
                    result = "Error al generar el archivo de salida";
                }
                catch (IOException e){
                    result  = "Error al manipular el archivo de salida";
                }
                return result;
            }
            if(strings[0].equals("delete_readings")){
               boolean isDeleted =  ReadingExportActivity.this.deleteDatabase(ReadingDatabaseSQL_statements.READING_DATABASE_NAME);
                if(isDeleted) {
                    return "Lecturas eliminadas con éxito";
                }else{
                    return "Error al eliminar lecturas";
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loadingDialog.dismiss();
            if(s == null ){
                s = "Resultado desconocido";
            }
            Toast.makeText(ReadingExportActivity.this,s,Toast.LENGTH_LONG).show();
        }
    }

}
