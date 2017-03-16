package com.aratech.lectoras;

import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aratech.lectoras.DialogWarning.OnAcceptWarningListener;
import com.aratech.lectoras.data.RegDataBase;

import java.io.File;
import java.util.ArrayList;

public class DialogRestore extends Dialog implements
		android.view.View.OnClickListener {
	Button btnCancel, btnAccept;
	ListView lvBackup;
	MainActivity mContext;
	ArrayList<File> mBackupFiles;
	File selectedFile;
	View selectedView;

	public DialogRestore(MainActivity context, ArrayList<File> backupFiles) {
		super(context);
		setContentView(R.layout.dialog_restore);
		mContext = context;
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		
		btnAccept = (Button) findViewById(R.id.btnOk);
		btnAccept.setOnClickListener(this);
		
		mBackupFiles = backupFiles;
		lvBackup = (ListView) findViewById(R.id.lvItems);
		lvBackup.setAdapter(new RestoreAdapter());

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnCancel) {
			clickCancel();
		} else if (v.getId() == R.id.btnOk) {
			if (selectedFile != null) {
				DialogWarning warning = new DialogWarning(mContext,
						"Los datos actuales serán sobreescritos,  �continuar?",
						new OnAcceptWarningListener() {

							@Override
							public void onAccept() {
								try {
									RegDataBase dataBase = new RegDataBase(
											mContext);
									dataBase.restoreDatabase(selectedFile);
									
									DialogRestore.this.dismiss();
									
									Toast.makeText(mContext, "éxito al restaurar!",
											Toast.LENGTH_LONG).show();
									
									
									
								} catch (Exception ex) {
									Toast.makeText(mContext, "Error al restaurar",
											Toast.LENGTH_LONG).show();
								}
							}
						});
				warning.show();
			} else {
				Toast.makeText(mContext, "Selecciona un archivo o cancela",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	private void clickCancel() {
		this.dismiss();
	}

	private class RestoreAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mBackupFiles.size();
		}

		@Override
		public Object getItem(int position) {
			return mBackupFiles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			File item = (File) getItem(position);
			convertView = new TextView(mContext);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			convertView.setLayoutParams(params);

			convertView.setPadding(10, 10, 10, 10);

			try {
				StringBuilder text = new StringBuilder();

				String[] splitted = item.getName().replace("_BACKUP", "")
						.replace(".db", "").split("_");

				char[] fecha = splitted[0].toCharArray();

				text.append(fecha[0]);
				text.append(fecha[1]);
				text.append("/");
				text.append(fecha[2]);
				text.append(fecha[3]);
				text.append("/");
				text.append(fecha[4]);
				text.append(fecha[5]);
				text.append(fecha[6]);
				text.append(fecha[7]);

				char[] hora = splitted[1].toCharArray();
				text.append("  -  ");
				
				text.append(hora[0]);
				text.append(hora[1]);
				text.append(":");
				text.append(hora[2]);
				text.append(hora[3]);
				text.append(":");
				text.append(hora[4]);
				text.append(hora[5]);

				((TextView) convertView).setText(text.toString());
				convertView.setTag(item);

				convertView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (v.getTag() != null) {
							if (selectedView != null) {
								selectedView
										.setBackgroundResource(android.R.color.transparent);
							}
							selectedFile = (File) v.getTag();
							selectedView = v;
							selectedView
									.setBackgroundResource(R.drawable.azul_alpha_marco_nine);
						}
					}
				});

			} catch (Exception ex) {
				((TextView) convertView).setText("ERROR!");
			}
			return convertView;
		}

	}

}
