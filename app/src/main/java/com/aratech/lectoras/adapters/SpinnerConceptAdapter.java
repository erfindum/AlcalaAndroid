package com.aratech.lectoras.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg10;
import com.aratech.lectoras.beans.Reg20Comunidades;
import com.aratech.lectoras.beans.Reg99TipoLectura;

public class SpinnerConceptAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Reg> regs;
	private int[] conceptsRestrictions;
	private ArrayList<Reg> allowedRegs;
	private HashMap<String, Reg> separatedConcepts;
	
	public SpinnerConceptAdapter(Context context,
			ArrayList<Reg> regsForAdapter) {
		mContext = context;
		regs = regsForAdapter;
		allowedRegs = regsForAdapter;
		separateConcepts(regsForAdapter);
	}

	private void separateConcepts(ArrayList<Reg> concepts){
		separatedConcepts = new HashMap<String, Reg>();
		for(Reg newConcept: concepts){
			separatedConcepts.put(((Reg10) newConcept).concept,newConcept);			
		}		
	}
	
	@Override
	public int getCount() {
		return allowedRegs.size();
	}

	@Override
	public Object getItem(int position) {
		return allowedRegs.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	public void setConceptsRestrictions(int[] restrictions){
		if(restrictions != null && restrictions.length > 0){
			conceptsRestrictions = restrictions;
			allowedRegs = new ArrayList<Reg>();
			for(int restriction : restrictions){
				allowedRegs.add(separatedConcepts.get(String.valueOf(restriction)));
			}
		}
	}
	
	public void removeConceptsRestrictions(){
		allowedRegs = regs;
		conceptsRestrictions = null;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView newItem;
		Reg10 selectedReg = (Reg10) getItem(position);
		if(convertView == null){
			newItem = new TextView(mContext);
			newItem.setPadding(10, 10, 10, 10);
			newItem.setTextSize(20);
			
		}else{
			newItem = (TextView) convertView;
		}
		newItem.setText(selectedReg.title);
		newItem.setTag(selectedReg);
		return newItem;
	}
}
