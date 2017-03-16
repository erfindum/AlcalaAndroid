package com.aratech.lectoras.data.classes;

import com.aratech.lectoras.R;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public abstract class Keypad implements OnClickListener {

	private Activity mContext;
	StringBuilder currentValueBuilder;
	TextView mText;

	public Keypad(Activity context, TextView tvToShow) {
		mContext = context;
		mText = tvToShow;

		((Button) mContext.findViewById(R.id.btn0)).setOnClickListener(this);
		((Button) mContext.findViewById(R.id.btn1)).setOnClickListener(this);
		((Button) mContext.findViewById(R.id.btn2)).setOnClickListener(this);
		((Button) mContext.findViewById(R.id.btn3)).setOnClickListener(this);
		((Button) mContext.findViewById(R.id.btn4)).setOnClickListener(this);
		((Button) mContext.findViewById(R.id.btn5)).setOnClickListener(this);
		((Button) mContext.findViewById(R.id.btn6)).setOnClickListener(this);
		((Button) mContext.findViewById(R.id.btn7)).setOnClickListener(this);
		((Button) mContext.findViewById(R.id.btn8)).setOnClickListener(this);
		((Button) mContext.findViewById(R.id.btn9)).setOnClickListener(this);

		((Button) mContext.findViewById(R.id.btnDel)).setOnClickListener(this);

		currentValueBuilder = new StringBuilder();
	}

	public void setCurrentValue(String value) {
		currentValueBuilder = new StringBuilder(value);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn0:
			currentValueBuilder.append("0");
			break;
		case R.id.btn1:
			currentValueBuilder.append("1");
			break;
		case R.id.btn2:
			currentValueBuilder.append("2");
			break;
		case R.id.btn3:
			currentValueBuilder.append("3");
			break;
		case R.id.btn4:
			currentValueBuilder.append("4");
			break;
		case R.id.btn5:
			currentValueBuilder.append("5");
			break;
		case R.id.btn6:
			currentValueBuilder.append("6");
			break;
		case R.id.btn7:
			currentValueBuilder.append("7");
			break;
		case R.id.btn8:
			currentValueBuilder.append("8");
			break;
		case R.id.btn9:
			currentValueBuilder.append("9");
			break;
		case R.id.btnDel:
			delCharacter();
			break;
		}
		updateValue();
	}

	private void updateValue() {
		if (mText != null) {
			mText.setText(currentValueBuilder.toString());
			
		}
		onKeypadValueChange(currentValueBuilder.toString());
	}

	private void delCharacter() {
		if (currentValueBuilder.length() > 0) {
			String newValue = currentValueBuilder.substring(0,
					currentValueBuilder.length() - 1);
			currentValueBuilder = new StringBuilder(newValue);
		}
	}

	public abstract void onKeypadValueChange(String newValue);
	
}
