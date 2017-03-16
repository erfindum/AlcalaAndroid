package com.aratech.lectoras;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aratech.lectoras.beans.Reg;
import com.aratech.lectoras.beans.Reg45Tarifas;

public class DialogTarifas extends Dialog {

	
	
	public DialogTarifas(Context context, ArrayList<Reg> tarifas) {
		super(context);
		
		setContentView(R.layout.dialog_tarifas);
		setTitle("TARIFAS");
		LinearLayout containerLayout = (LinearLayout) findViewById(R.id.layoutTarifa);
		for(Reg tarifa: tarifas){
			Reg45Tarifas convertReg = (Reg45Tarifas) tarifa;
			
			View element = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.tarifa_row, null);
			((TextView) element.findViewById(R.id.tvText)).setText(convertReg.conceptText);
			((TextView) element.findViewById(R.id.tvConsumo)).setText(convertReg.consumption);
			String formattedAmount = getFormattedAmountValue(convertReg.amount, 2);
			((TextView) element.findViewById(R.id.tvImporte)).setText(formattedAmount);
			containerLayout.addView(element);
		}
		((Button) findViewById(R.id.btnVolver)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				dismiss();				
			}
		});
	}

	private String getFormattedAmountValue(String originalString, int numberOfDecimals){
		String result = "-1";
		if(numberOfDecimals <= 0){
			return originalString;
		}else{
			StringBuilder dividerBuilder = new StringBuilder("1");
			for(int i = 0; i < numberOfDecimals; i++){
				dividerBuilder.append("0");
			}
			int divider = Integer.valueOf(dividerBuilder.toString());
			double originalNumber = Integer.valueOf(originalString);
					
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			result = twoDForm.format(originalNumber / divider);
		}
		return result;
	}
	
}
