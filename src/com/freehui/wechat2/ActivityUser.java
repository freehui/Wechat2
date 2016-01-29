package com.freehui.wechat2;
 
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.NoTieleActivity;

public class ActivityUser extends NoTieleActivity implements OnClickListener{
	
	Bundle bundle;
	Button addfriend;
	String phone;
	String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_user);
		setBack(findViewById(R.id.register_back));
		
		addfriend = (Button)findViewById(R.id.addfriend);
		addfriend.setOnClickListener(this);
		
		bundle = getIntent().getExtras();
		phone = bundle.getString("phone");
		name = bundle.getString("name");
		
		((TextView)findViewById(R.id.user_name)).setText(name);
		((TextView)findViewById(R.id.wechat_id)).setText("微信号:" + phone);
		((ImageView)findViewById(R.id.user_image)).setImageBitmap(getImageFromServer(phone));
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.equals(addfriend)){
			
			Intent intent = new Intent(ActivityUser.this,ActivityConfirmadd.class);
			intent.putExtra("name", name);
			intent.putExtra("phone", phone);
			bootActivity(intent);
		}
	}
}
