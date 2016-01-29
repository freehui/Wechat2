package com.freehui.wechat2;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.NoTieleActivity;

public class ActivityConfirmadd extends NoTieleActivity implements OnClickListener{
	
	EditText vmessage;
	Button send_addfriend_request;
	String phone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_confirmadd);
		setBack(findViewById(R.id.register_back));
		
		phone = getIntent().getExtras().getString("phone");
		
		vmessage = (EditText)findViewById(R.id.vmessage);
		vmessage.setSelection(vmessage.getText().length());
		vmessage.setText("我是"+WechatSocket.getInstance().username);
		
		send_addfriend_request = (Button) findViewById(R.id.send_addfriend_request);
		send_addfriend_request.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.equals(send_addfriend_request)){
			
			WechatSocket ws = WechatSocket.getInstance();

			ws.addfriend(phone, vmessage.getText().toString());
			
			close();
		}
	}
}
