package com.aratech.lectoras.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg10;
import com.aratech.lectoras.beans.Reg20Comunidades;
import com.aratech.lectoras.beans.Reg99TipoLectura;

public class SpinnerTypeAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Reg> regs;

	public SpinnerTypeAdapter(Context context, ArrayList<Reg> regsForAdapter) {
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

		return Long.valueOf(((Reg99TipoLectura) regs.get(position)).readCode);
	}

	public int getPositionByReadCodeOfType(int readCode) {
		int result = -1;
		for (int i = 0; i < regs.size(); i++) {
			try {
				Reg99TipoLectura regType = (Reg99TipoLectura) regs.get(i);
				int mReadCode = Integer.valueOf(regType.readCode);
				if(mReadCode == readCode){
					return i;
				}
				
			} catch (Exception ex) {
			}
		}
		return result;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView newItem;
		Reg99TipoLectura selectedReg = (Reg99TipoLectura) getItem(position);
		if (convertView == null) {
			newItem = new TextView(mContext);
			newItem.setPadding(10, 10, 10, 10);
			newItem.setTextSize(20);
		} else {
			newItem = (TextView) convertView;
		}
		if (!selectedReg.readCode.equals(Reg99TipoLectura.EMPTY_TYPE)) {
			newItem.setText(selectedReg.readCode + " - " + selectedReg.readText);
		} else {
			newItem.setText("");
		}

		newItem.setTag(selectedReg);
		return newItem;
	}
}
