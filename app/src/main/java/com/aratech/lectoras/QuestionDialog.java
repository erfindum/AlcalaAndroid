package com.aratech.lectoras;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class QuestionDialog extends Dialog implements
		android.view.View.OnClickListener {

	TextView txtMessage;
	OnQuestionRespondedListener mListener;
	Button btnYes, btnNo;

	public QuestionDialog(Context context, String question,
			OnQuestionRespondedListener listener) {
		super(context);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_question);
		setTitle("¡CUIDADO!");

		mListener = listener;

		txtMessage = (TextView) findViewById(R.id.tvQuestion);
		txtMessage.setText(question);

		btnYes = (Button) findViewById(R.id.btnYes);
		btnYes.setOnClickListener(this);

		btnNo = (Button) findViewById(R.id.btnNo);
		btnNo.setOnClickListener(this);

	}

	public enum QuestionResponse {
		YES, NO
	}

	public interface OnQuestionRespondedListener {
		public void onQuestionResponse(QuestionResponse response);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnYes:
			dismiss();
			mListener.onQuestionResponse(QuestionResponse.YES);
			break;
		case R.id.btnNo:
			dismiss();
			mListener.onQuestionResponse(QuestionResponse.NO);

			break;
		}
	}

	@Override
	public void onBackPressed() {
		dismiss();
		mListener.onQuestionResponse(QuestionResponse.NO);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
}
