package com.freehui.wechat2;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.FriendDescriptor;
import com.freehui.wechat2.baseclass.NoTieleActivity;
import com.freehui.wechat2.baseclass.SerializableObject;
import com.freehui.wechat2.util.UserActions;

public class ActivityNewfriends extends NoTieleActivity {

	View backbtn;
	NewfriendListBroadcastReceiver newfriendbcr;
	ArrayList<FriendDescriptor> requestlist;
	ListView requestListView;
	FriendlistAdapter requestlistAdapter;
	LocalBroadcastManager localbroad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_newfriends);
		setBack(findViewById(R.id.register_back));
		
		localbroad = LocalBroadcastManager.getInstance(this);
		requestlist = (ArrayList<FriendDescriptor>) ((SerializableObject) getIntent()
				.getExtras().get("request")).get();

		requestListView = (ListView) findViewById(R.id.requestlist);
		requestlistAdapter = new FriendlistAdapter(this, requestlist);
		requestListView.setAdapter(requestlistAdapter);
		requestListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(ActivityNewfriends.this,
						ActivityAcceptRequest.class);
				intent.putExtra("user", requestlistAdapter.get(arg2));
				bootActivity(intent);
			}
		});

		newfriendbcr = new NewfriendListBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(getResources().getString(R.string.addnotify_broadcast));
		filter.addAction(getResources().getString(R.string.addfriend_broadcast));
		filter.addAction(getResources().getString(R.string.refusenotify_broadcast));
		localbroad.registerReceiver(newfriendbcr, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		localbroad.unregisterReceiver(newfriendbcr);
	}

	class RequestClick implements OnClickListener {

		FriendDescriptor user;

		RequestClick(FriendDescriptor user) {

			this.user = user;
		}

		public void onClick(View view) { // TODO Auto-generated method stub

			Intent intent = new Intent(ActivityNewfriends.this,
					ActivityAcceptRequest.class);
			intent.putExtra("user", user);
			bootActivity(intent);
		}
	}

	class AddfriendClick implements OnClickListener {

		FriendDescriptor user;

		AddfriendClick(FriendDescriptor user) {

			this.user = user;
		}

		public void onClick(View arg1) {
			
			WechatSocket.getInstance().confirmaddfriend(user.getphone(),user.getname());
			Intent intent = new Intent();
			intent.setAction(getResources().getString(
					R.string.addnotify_broadcast));
			
			intent.putExtra("type", UserActions.PULL_ADDNOTIFI);
			intent.putExtra("data", "{\"name\":\"" + user.getname()
					+ "\",\"phone\":\"" + user.getphone() + "\"}");
			
			localbroad.sendBroadcast(intent);
		}
	}

	public class FriendlistAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<FriendDescriptor> myfriendlist;

		public FriendlistAdapter(Context context,
				ArrayList<FriendDescriptor> list) {
			// TODO Auto-generated constructor stub

			inflater = LayoutInflater.from(context);
			myfriendlist = list;
		}

		/*
		 * 添加一个列表项
		 */
		public void add(FriendDescriptor friend) {

			myfriendlist.add(friend);
		}

		/*
		 * 删除一个列表项
		 */
		public void del(String phone) {

			int i;
			FriendDescriptor friend;

			for (i = 0; i < myfriendlist.size(); i++) {

				friend = (FriendDescriptor) myfriendlist.get(i);
				if (friend.getphone().equals(phone)) {

					myfriendlist.remove(i);
				}
			}
		}

		/*
		 * 提交所有添加和删除
		 */
		public void commit() {

			notifyDataSetChanged();
		}

		public FriendDescriptor get(int index) {

			return myfriendlist.get(index);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return myfriendlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return myfriendlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			FriendDescriptor friend = myfriendlist.get(position);
			ViewHolder holder;

			if (convertView == null) {

				holder = new ViewHolder();

				convertView = (ViewGroup) inflater.inflate(R.layout.newrequest,
						null);

				// 缓存View内的子控件
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.value = (TextView) convertView.findViewById(R.id.value);
				holder.user_image = (ImageView) convertView.findViewById(R.id.user_image);
						
				Button btn = (Button) convertView.findViewById(R.id.send_addfriend_request);
				btn.setOnClickListener(new AddfriendClick(friend));
				
				convertView.setTag(holder);

			} else {

				// 既然缓存的View已经存在，我们再取出他的缓存TextView
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(friend.getname());
			holder.value.setText(friend.getprivate());
			holder.user_image.setImageBitmap(getImageFromLocal(friend.getphone()));

			return convertView;
		}
	}

	class ViewHolder {
		TextView value;
		TextView name;
		ImageView user_image;
	}

	/*
	 * 添加请求
	 */
	void addRequest(FriendDescriptor friend) {

		if (friend != null)
			requestlistAdapter.add(friend);
	}

	/*
	 * 删除请求
	 */
	void delRequest(String phone) {

		int i;

		if (phone != null)
			requestlistAdapter.del(phone);
	}

	void commitRequest() {

		requestlistAdapter.commit();
	}

	class NewfriendListBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub

			JSONObject obj;
			FriendDescriptor friend = new FriendDescriptor();
			Bundle bundle = arg1.getExtras();
			String data = bundle.getString("data");

			try {
				obj = new JSONObject(data);
				friend.setname(obj.getString("name"));
				friend.setphone(obj.getString("phone"));
				friend.setprivate(obj.getString("value"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			switch (bundle.getInt("type")) {

			case UserActions.PULL_ADDREQUEST: // 好友添加请求
				addRequest(friend);
				break;
			case UserActions.PULL_ADDNOTIFI: // 对方答应添加好友
				// 从好友申请列表里删除
				delRequest(friend.getphone());
				break;
			case UserActions.PULL_REFUSENOTIFI:// 对方拒绝添加好友
				// 从好友申请列表里删除
				delRequest(friend.getphone());
				break;
			}

			commitRequest();
		}
	}
}
