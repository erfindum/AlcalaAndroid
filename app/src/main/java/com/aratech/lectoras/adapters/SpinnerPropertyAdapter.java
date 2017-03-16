package com.aratech.lectoras.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg20Comunidades;

public class SpinnerPropertyAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Reg> regs;

	public SpinnerPropertyAdapter(Context context,
			ArrayList<Reg> regsForAdapter) {
		mContext = context;
		regs = regsForAdapter;
	}

	@Override
	public int getCount() {
		return regs.size();
	}

	@Override
	public Object getItem(int position) {
		return regs.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView newItem;
		Reg20Comunidades selectedReg = (Reg20Comunidades) getItem(position);
		if(convertView == null){
			newItem = new TextView(mContext);
			newItem.setPadding(10, 10, 10, 10);
			newItem.setTextSize(20);
			
		}else{
			newItem = (TextView) convertView;
		}
		newItem.setText(selectedReg.getCommunityNumber() + " - " + selectedReg.getCommunityAddress());
		newItem.setTag(selectedReg);
		return newItem;
	}
}
