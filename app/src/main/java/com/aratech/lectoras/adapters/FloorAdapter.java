package com.aratech.lectoras.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aratech.lectoras.R;
import com.aratech.lectoras.ReadingActivity;
import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg40Pisos;

import java.util.ArrayList;

public class FloorAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Reg> mItems;
	private OnRegChangedListener mListener;

	private Reg40Pisos selectedReg;

	public FloorAdapter(Context currentContext, ArrayList<Reg> items,
			OnRegChangedListener onRegListener) {
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

	public void reloadItems(ArrayList<Reg> newItems) {
		mItems = newItems;
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
    {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final Floor holder;
		if (convertView == null)
        {
			convertView = inflater.inflate(R.layout.row_floor_info, null);

			holder = new Floor();
			holder.tvOwner = (TextView) convertView.findViewById(R.id.tvOwner);
			holder.tvFloor = (TextView) convertView.findViewById(R.id.tvFloor);
			holder.tvRadio = (TextView) convertView.findViewById(R.id.tvRadio);
			holder.imgState = (ImageView) convertView
					.findViewById(R.id.imgReadingsState);
		}
        else
        {
			holder = (Floor) convertView.getTag();
		}

		Reg40Pisos piso = (Reg40Pisos) getItem(position);

		if (selectedReg != null && selectedReg.floor.equals(piso.floor))
        {
			convertView.setBackgroundResource(R.drawable.azul_alpha_marco_nine);
		}
        else
        {
			convertView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
		}

		holder.item = piso;

		holder.tvOwner.setText(holder.item.owner);
		holder.tvFloor.setText(holder.item.floor);

		try
        {
			int hasSerial;

			try
            {
				hasSerial = Integer.valueOf(holder.item.serial);
			}
            catch (NumberFormatException ex)
            {
				hasSerial = 0;
			}

			if(hasSerial == -1 || hasSerial == 1)
            {
				holder.tvRadio.setText("R");
				holder.tvRadio.setTextColor(mContext.getResources().getColor(
						R.color.state_red));
			}
			else
            {
				holder.tvRadio.setText("");			
			}
			
		}
        catch (Exception ex)
        {
			holder.tvRadio.setText("EX");
			holder.tvRadio.setTextColor(mContext.getResources().getColor(
					R.color.state_red));
		}

		int state = ((ReadingActivity) mContext).database.getReadingsState(holder.item);

		switch (state)
        {
            case -1:
                holder.imgState.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.state_red));
                break;
            case 0:
                holder.imgState.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.state_yellow));
                break;
            case 1:
                holder.imgState.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.state_green));
                break;
            default:
                holder.imgState.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.state_grey));
		}

		convertView.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
            {
				selectedReg = holder.item;
				mListener.onSelected(selectedReg, v);
				notifyDataSetChanged();
			}
		});

		convertView.setTag(holder);
		return convertView;
	}

	public static class Floor {
		public Reg40Pisos item;

		public TextView tvOwner;
		public TextView tvFloor;
		public TextView tvRadio;

		public ImageView imgState;

	}

	public interface OnRegChangedListener {

		public abstract void onSelected(Reg selectedReg, View selectedView);
	}

}
