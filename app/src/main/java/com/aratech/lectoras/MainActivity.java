package com.aratech.lectoras;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.aratech.lectoras.data.RegDataBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends Activity implements OnClickListener{

	Button btnMaintenance, btnImport, btnExport,btnExportAntenna, btnFinalize, btnBackup, btnRestore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnMaintenance = (Button) findViewById(R.id.btnMantenimiento);
		btnMaintenance.setOnClickListener(this);
		
		btnImport = (Button) findViewById(R.id.btnImportar);
		btnImport.setOnClickListener(this);
		
		btnExport = (Button) findViewById(R.id.btnExportar);
		btnExport.setOnClickListener(this);

		btnExportAntenna = (Button) findViewById(R.id.btnExportarAntena);
		btnExportAntenna.setOnClickListener(this);

		btnFinalize = (Button) findViewById(R.id.btnSalir);
		btnFinalize.setOnClickListener(this);
		
		btnBackup = (Button) findViewById(R.id.btnBackup);
		btnBackup.setOnClickListener(this);
		
		btnRestore = (Button) findViewById(R.id.btnRestore);
		btnRestore.setOnClickListener(this);
		
	}
	
	private void startSelectionActivity() {
		Intent newIntent = new Intent(this, SelectionPropertyActivity.class);
		startActivity(newIntent);
	}
	
	private void startImportActivity() {
		Intent newIntent = new Intent(this, ImportActivity.class);
		startActivity(newIntent);
	}
	
	private void startExportActivity() {
		Intent newIntent = new Intent(this, ExportActivity.class);
		startActivity(newIntent);
	}

    private void startAntennaExportActivity(){
        startActivity(new Intent(this,ReadingExportActivity.class));
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnMantenimiento:
			startSelectionActivity();
			break;
		case R.id.btnImportar:
			startImportActivity();
			break;
		case R.id.btnExportar:
			startExportActivity();
			break;
        case R.id.btnExportarAntena:
            startAntennaExportActivity();
            break;
		case R.id.btnSalir:
			this.finish();
			break;
		case R.id.btnBackup:
			startBackup();
			break;
		case R.id.btnRestore:
			startRestore();
			break;
		}
		
	}

	private void startBackup() {
		try{
			RegDataBase database = new RegDataBase(this);
			Calendar calendarForDate = GregorianCalendar.getInstance();
			StringBuilder nameFileBuilder = new StringBuilder();
			
			if(calendarForDate.get(Calendar.DAY_OF_MONTH) < 10)
				nameFileBuilder.append("0");
			
			nameFileBuilder.append(calendarForDate.get(Calendar.DAY_OF_MONTH));
			
			if(calendarForDate.get(Calendar.MONTH ) + 1 < 10)
				nameFileBuilder.append("0");
			
			nameFileBuilder.append(calendarForDate.get(Calendar.MONTH ) + 1);
			
			
			nameFileBuilder.append(calendarForDate.get(Calendar.YEAR));
			
			nameFileBuilder.append("_");
			
			nameFileBuilder.append(calendarForDate.get(Calendar.HOUR_OF_DAY));
			nameFileBuilder.append(calendarForDate.get(Calendar.MINUTE));
			nameFileBuilder.append(calendarForDate.get(Calendar.SECOND));

			nameFileBuilder.append("_BACKUP.db");
			
			database.backupDatabase(nameFileBuilder.toString());
			
			Toast.makeText(this, "¡Copia realizada con éxito!, archivo:\n" + nameFileBuilder.toString(), Toast.LENGTH_LONG).show();
			
		}catch(Exception ex){
			Toast.makeText(this, "Error al realizar la copia de seguridad", Toast.LENGTH_SHORT).show();
		}
		
	}

	private void startRestore()
    {
		File backupDirectory = new File(RegDataBase.getExternalDirectory(),"BACKUP_LECTORAS");
		
		if(backupDirectory.exists() && backupDirectory.isDirectory())
        {
			ArrayList<File> validFiles = new ArrayList<File>();
			for(File fil: backupDirectory.listFiles())
            {
				if(fil.isFile() && fil.getName().contains("BACKUP"))
                {
					validFiles.add(fil);
				}
			}

			if(validFiles.size() > 0 )
            {
				DialogRestore restoreDial = new DialogRestore(MainActivity.this, validFiles);
				restoreDial.show();
			}
            else
            {
				Toast.makeText(this, "No se han encontrado archivos válidos en el directorio BACKUP_LECTORAS", Toast.LENGTH_SHORT).show();
			}
		}
        else
        {
			Toast.makeText(this, "No se ha encontrado el directorio BACKUP_LECTORAS", Toast.LENGTH_SHORT).show();
		}
	}
}
