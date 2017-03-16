package com.aratech.lectoras;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DialogWarning extends Dialog implements android.view.View.OnClickListener {

	private OnAcceptWarningListener listener;

	Button btnAccept, btnCancel;
	TextView tvWarningText;
	


	public DialogWarning(Context context, String message, OnAcceptWarningListener onAcceptWarningListener) {
		super(context);

		setContentView(R.layout.dialog_warning);
		setTitle("ADVERTENCIA");
		listener = onAcceptWarningListener;
		
		tvWarningText = (TextView)findViewById(R.id.tvWarningText);
		tvWarningText.setText(message);
		
		btnAccept = (Button) findViewById(R.id.btnOk);
		btnAccept.setOnClickListener(this);
		
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		
	}

	private void clickAccept() {
		
		listener.onAccept();
		this.dismiss();
	}

	private void clickCancel() {
		this.dismiss();
	}

	@Override
	public void onBackPressed() {
	}

	public interface OnAcceptWarningListener {
		public abstract void onAccept();
	}

	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnOk){
			clickAccept();
		}else if(v.getId() == R.id.btnCancel){
			clickCancel();
		}
		
	}
}
