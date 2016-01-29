package com.freehui.wechat2.util;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.LinearLayout;

import com.example.wechat2.R;

public class LoginRegisterOnFocusChangeListener implements
		OnFocusChangeListener {

	LinearLayout target;

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub

		if (arg1 == true) {

			target.setBackgroundResource(R.drawable.register_login_lineralayout_press);
		} else {

			target.setBackgroundResource(R.drawable.register_login_lineralayout_default);
		}
	}

	public LoginRegisterOnFocusChangeListener(LinearLayout target) {

		this.target = target;
	}
}
