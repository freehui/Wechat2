package com.freehui.wechat2.baseclass;

import android.os.Bundle;
import android.view.Window;

public class NoTieleActivity extends ReturnActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	}
}
