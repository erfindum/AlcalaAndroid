package com.aratech.lectoras.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.aratech.lectoras.R;
import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg20Comunidades;

public class ComunityAdapter extends BaseAdapter {

	private ArrayList<Reg> items;
	private Context context;
	private ArrayList<Reg20Comunidades> selectedItems;
	
	public ComunityAdapter(Context currentContext, ArrayList<Reg> regs){
		items = regs;
		context = currentContext;
		selectedItems = new ArrayList<Reg20Comunidades>();
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	public ArrayList<Reg20Comunidades> getSelectedItems(){
		return selectedItems;
	}
	
	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView == null){
			convertView = inflater.inflate(R.layout.row_check, null);
		}
		TextView tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
		TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
		CheckBox cbSelected = (CheckBox) convertView.findViewById(R.id.chRow);
		final Reg20Comunidades currentItem = (Reg20Comunidades) getItem(position);
		tvNumber.setText(String.valueOf(currentItem.getCommunityNumber()));
		tvName.setText(currentItem.getCommunityAddress());
		if(selectedItems != null){
			cbSelected.setChecked(selectedItems.contains(currentItem));
		}
		cbSelected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					selectedItems.add(currentItem);
				}else{
					selectedItems.remove(currentItem);
				}
				
			}
		});
		return convertView;
	}

}
