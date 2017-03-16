package com.aratech.lectoras.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aratech.lectoras.R;
import com.aratech.lectoras.ReadingActivity;
import com.aratech.lectoras.SelectionPropertyActivity;
import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg20Comunidades;

public class PropertyAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Reg> mItems;
	private OnRegSelectedListener mListener;
	
	public PropertyAdapter(Context currentContext, ArrayList<Reg> items, OnRegSelectedListener onRegListener) {
		mContext = currentContext;
		mItems = items;
		mListener = onRegListener;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void reloadItems(ArrayList<Reg> items){
		this.mItems = items;
		this.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final PropertyHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_community_info, null);
			
			holder = new PropertyHolder();
			holder.tvCommunity = (TextView) convertView.findViewById(R.id.tvCommunity);
			holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress); 
			holder.imgStateDate = (ImageView) convertView.findViewById(R.id.imgDateState);
			holder.imgStateFloors = (ImageView) convertView.findViewById(R.id.imgFloorState);
		}else{
			holder = (PropertyHolder) convertView.getTag();
		}

		
		holder.item = (Reg20Comunidades) getItem(position);
		
		holder.tvCommunity.setText(String.valueOf(holder.item.getCommunityNumber()));
		holder.tvAddress.setText(holder.item.getCommunityAddress());
		
		if(!holder.item.initDate.equals("") && !holder.item.endDate.equals("")){
			holder.imgStateDate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.state_green));
		}else if(!holder.item.initDate.equals("") || !holder.item.initDate.equals("")){
			holder.imgStateDate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.state_yellow));
		}else{
			holder.imgStateDate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.state_red));
		}
		
		int state = ((SelectionPropertyActivity) mContext).database
				.getFloorsState(holder.item);

		switch (state) {
		case -1:
			holder.imgStateFloors.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.state_red));
			break;
		case 0:
			holder.imgStateFloors.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.state_yellow));
			break;
		case 1:
			holder.imgStateFloors.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.state_green));
			break;
		default:
			holder.imgStateFloors.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.state_grey));
		}
		
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Reg20Comunidades clickedReg = holder.item;
				mListener.onSelected(clickedReg, v);
				
			}
		});
		convertView.setTag(holder);
		
		return convertView;
	}

	public static class PropertyHolder{
		public Reg20Comunidades item; 
        
		public TextView tvCommunity;
		public TextView tvAddress;
		
		public ImageView imgStateDate, imgStateFloors;
		                      
	}
	
	public interface OnRegSelectedListener{
		
		public abstract void onSelected(Reg selectedReg, View selectedView);
	}

	
}
