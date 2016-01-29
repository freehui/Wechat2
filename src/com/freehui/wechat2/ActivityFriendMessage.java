package com.freehui.wechat2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.freehui.wechat2.windows.WechatAlert;
import com.freehui.wechat2.windows.WechatAlert.WechatAlertonConfirm;

public class ActivityFriendMessage extends NoTieleActivity implements
		OnClickListener {

	FriendDescriptor friend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_friendmessage);
		setBack(findViewById(R.id.register_back));

		friend = (FriendDescriptor) getIntent().getSerializableExtra("friend");
		if (friend == null) {

			finish();
			return;
		}

		((TextView) findViewById(R.id.user_name)).setText(friend.getname());
		((TextView) findViewById(R.id.wechat_id)).setText("微信号:"
				+ friend.getphone());
		
		friend.setimage(getImageFromServer(friend.getphone()));
		((ImageView) findViewById(R.id.user_image)).setImageBitmap(getImageFromServer(friend.getphone()));
	
		findViewById(R.id.send).setOnClickListener(this);
		findViewById(R.id.no).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()){
			
			case R.id.send:
				Intent intent = new Intent(this,ActivityChat.class);
				intent.putExtra("phone", friend.getphone());
				intent.putExtra("name", friend.getname());
				finish();
				bootActivity(intent);		  		
				break;
			case R.id.no:
				new WechatAlert(this).
					setTitle("删除联系人").
					setValue("将联系人"+ friend.getname() +"删除，将同时删除与该联系人的聊天记录").
					setConfirmText("删除").
					setCancelText("取消").
					setOnConfirm(new WechatAlertonConfirm() {
						
						@Override
						public void onConfirm() {
							// TODO Auto-generated method stub
							
							pupopwaiting("请稍候");
							
							SQLiteDatabase db = WechatLocalSQLite.getDB();
							db.execSQL("delete from wechat_temp_message where dstphone = ? and srcphone = ?", 
									new String[]{WechatSocket.getInstance().phone,friend.getphone()});
							
							//发送删除好友的本地广播
							Intent intent = new Intent();
							intent.putExtra("phone", friend.getphone());
							intent.setAction(getString(R.string.localbrard_deletefriend));
							LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
							
							close();
						}
					}).show();
				break;
		}
	}
}
