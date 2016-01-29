package com.freehui.wechat2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.FriendDescriptor;
import com.freehui.wechat2.baseclass.NoTieleActivity;
import com.freehui.wechat2.util.UserActions;

public class ActivityAcceptRequest extends NoTieleActivity implements OnClickListener{
	
	Button ok;
	Button no;
	
	FriendDescriptor user;
	ImageView s;
	
	String name;
	String phone;
	
	LocalBroadcastManager localbroad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acceptrequest);
		
		Bundle bundle = getIntent().getExtras();
	
		
		localbroad = LocalBroadcastManager.getInstance(this);
		
		ok = (Button)findViewById(R.id.ok);
		no = (Button)findViewById(R.id.no);
		
		ok.setOnClickListener(this);
		no.setOnClickListener(this);
		
		user = (FriendDescriptor)bundle.get("user");
		
		((ImageView)findViewById(R.id.user_image))
			.setImageBitmap(getImageFromLocal(user.getphone()));
		((TextView)findViewById(R.id.user_name)).setText(user.getname());
		String v = user.getprivate();
		if (v != null && v.equals(""))
			((TextView)findViewById(R.id.sureaddfriend_value)).setText(v);
		else
			findViewById(R.id.valueborder).setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		WechatSocket sock = WechatSocket.getInstance();
		
		Intent intent = new Intent();
		
		if (view.equals(ok)){
			
			sock.confirmaddfriend(user.getphone(),user.getname());
			
			intent.setAction(getResources().getString(R.string.addnotify_broadcast));

			intent.putExtra("type", UserActions.PULL_ADDNOTIFI);
			intent.putExtra("data", "{\"name\":\"" + user.getname()
					+ "\",\"phone\":\"" + user.getphone() + "\"}");
			
		}else if (view.equals(no)){
			
			sock.refuseaddfriend(user.getphone(), user.getname());
			
			intent.setAction(getResources().getString(R.string.refusenotify_broadcast));
			
			intent.putExtra("type", UserActions.PULL_REFUSENOTIFI);
			intent.putExtra("data", "{\"name\":\"" + user.getname()
					+ "\",\"phone\":\"" + user.getphone() + "\"}");
		}else {
			
			return;
		}
		
		localbroad.sendBroadcast(intent);	
		finish();
	}
}
