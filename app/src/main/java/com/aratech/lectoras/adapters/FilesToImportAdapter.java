package com.aratech.lectoras.adapters;

import java.io.File;
import java.util.ArrayList;

import com.aratech.lectoras.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class FilesToImportAdapter extends BaseAdapter {

	private ArrayList<File> filesInDir;
	private TextView selectedView;
	private Context mContext;
	private OnFileSelectedListener listener;

	public FilesToImportAdapter(Context context, ArrayList<File> files,
			OnFileSelectedListener fileSelectedListener) {
		filesInDir = files;
		mContext = context;
		listener = fileSelectedListener;
	}

	@Override
	public int getCount() {
		if (filesInDir != null && filesInDir.size() > 0) {
			return filesInDir.size();
		} else
			return 1;
	}

	public File getSelectedFile() {
		if (selectedView != null) {
			return (File) selectedView.getTag();
		} else {
			return null;
		}
	}

	@Override
	public File getItem(int position) {
		if (filesInDir == null) {
			return null;
		} else {
			return filesInDir.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final TextView tv;

		if (filesInDir == null || filesInDir.size() == 0) {
			tv = getFormattedRow();
			tv.setText(R.string.no_archivos);
		} else {
			if (convertView != null && convertView instanceof TextView) {
				tv = (TextView) convertView;
			} else {
				tv = getFormattedRow();
			}
			tv.setTag(getItem(position));
			tv.setText(getItem(position).getName());
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setSelectedItem(tv);
				}
			});
		}
		return tv;
	}

	private TextView getFormattedRow() {
		TextView tv = new TextView(mContext);

		AbsListView.LayoutParams params = new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(params);
		tv.setPadding(10, 10, 10, 10);
		return tv;
	}

	private void setSelectedItem(TextView tv) {

		if (selectedView != null) {
			selectedView.setBackgroundColor(mContext.getResources().getColor(
					android.R.color.transparent));
		}
		tv.setBackgroundColor(mContext.getResources().getColor(
				R.color.light_green));
		selectedView = tv;
		listener.onFileSelected((File) tv.getTag());
	}

	public interface OnFileSelectedListener {
		public void onFileSelected(File file);
	}
}
