package com.freehui.wechat2.baseclass;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ReturnActivity extends SlideActivity {
	
	public void setBack(View view){
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				close();
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
}
