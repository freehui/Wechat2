package com.freehui.wechat2.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wechat2.R;
import com.freehui.wechat2.ActivityFriendMessage;
import com.freehui.wechat2.ActivityNewfriends;
import com.freehui.wechat2.WechatSocket;
import com.freehui.wechat2.baseclass.BaseActivity;
import com.freehui.wechat2.baseclass.BaseFragment;
import com.freehui.wechat2.baseclass.FriendDescriptor;
import com.freehui.wechat2.baseclass.SerializableObject;
import com.freehui.wechat2.chinesetopinyin.ChineseToPinyin;
import com.freehui.wechat2.chinesetopinyin.PinyinComparator;
import com.freehui.wechat2.util.UserActions;

@SuppressLint({ "ShowToast", "DefaultLocale", "ValidFragment" })
public class Addresspage extends BaseFragment {

	View view;
	ListView friendlist;
	FriendlistAdapter friendlistadapter;
	LayoutInflater infalter;
	ChineseToPinyin ctp = ChineseToPinyin.getInstance();
	JSONArray allfriend = null;
	JSONArray friendrequest = null;
	/* 好友列表 */
	ArrayList<FriendDescriptor> friendarraylist = new ArrayList<FriendDescriptor>();
	/* 添加好友请求的列表 */
	ArrayList<FriendDescriptor> addrequestlist = new ArrayList<FriendDescriptor>();
	FriendListBroadcastReceiver friendlistbcr;
	TextView request_number;
	LocalBroadcastManager localbroad;
	
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.obj == null) {

				Log.w("TAG-NULL", "NULL");
				return;
			}

			switch (msg.what) {

			case UserActions.GET_ALL_FRIEND:
				try {
					allfriend = new JSONArray((String) msg.obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.w("TAG", "服务器发来的数据出错");
				}
				flushFriendList(allfriend);
				break;
			case UserActions.GET_ADDFRIEND_REQUEST:
				try {
					friendrequest = new JSONArray((String) msg.obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.w("TAG", "服务器发来的数据出错");
				}
				flushRequestList(friendrequest);
				break;
			default:
				Log.w("TAG", "其他");
				break;
			}

		};
	};

	public Addresspage() {
		// TODO Auto-generated constructor stub

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		ViewGroup newfriends;
		ViewGroup friendfooter;
		
		localbroad = LocalBroadcastManager.getInstance(getActivity());
		infalter = getActivity().getLayoutInflater();
		newfriends = (ViewGroup) infalter.inflate(R.layout.friendlist_head,
				null);
		friendfooter = (ViewGroup) infalter.inflate(R.layout.friendlist_bottom,
				null);
		view = infalter.inflate(R.layout.tab_address, null, true);
		friendlist = (ListView) view.findViewById(R.id.friends);

		request_number = (TextView) newfriends
				.findViewById(R.id.request_number);
		friendlistadapter = new FriendlistAdapter(getActivity(),
				friendarraylist);
		friendlist.addHeaderView(newfriends);
		friendlist.addFooterView(friendfooter);
		friendlist.setAdapter(friendlistadapter);

		WechatSocket sock = WechatSocket.getInstance();
		sock.getAllfriend(handler);
		sock.query_addfriendrequest(handler);

		((TextView) friendfooter.findViewById(R.id.lll))
				.setText(friendarraylist.size() + "位联系人");

		friendlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getActivity(),
						ActivityFriendMessage.class);
				FriendDescriptor friend = friendlistadapter.get(arg2 - 1);
				intent.putExtra("friend", friend);
				((BaseActivity) getActivity()).bootActivity(intent);
			}
		});

		newfriends.findViewById(R.id.news).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(getActivity(),
								ActivityNewfriends.class);
						intent.putExtra("request", new SerializableObject(
								addrequestlist));
						getParent().bootActivity(intent);
					}
				});

		if (friendlistbcr == null) {

			friendlistbcr = new FriendListBroadcastReceiver();
			IntentFilter filter = new IntentFilter();
			
			filter.addAction(getResources().getString(R.string.addnotify_broadcast));
			filter.addAction(getResources().getString(R.string.addfriend_broadcast));
			filter.addAction(getResources().getString(R.string.refusenotify_broadcast));
			filter.addAction(getResources().getString(R.string.localbrard_deletefriend));
															    
			localbroad.registerReceiver(friendlistbcr, filter);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (friendlistbcr == null)
			localbroad.unregisterReceiver(friendlistbcr);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	class FriendListBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			
			if (!isAdded())
				return;
			
			String action = arg1.getAction();
			
			if (action.equals(getString(R.string.localbrard_deletefriend))){
				//删除好友的本地广播
				String phone = arg1.getExtras().getString("phone");
				WechatSocket.getInstance().deleteFriend(phone);
				friendlistadapter.del(arg1.getExtras().getString("phone"));
				friendlistadapter.commit();						
			}else {
				//进入到这里的，都是通过网络推送的广播
				
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
				
				if (action.equals(getString(R.string.addfriend_broadcast))){
					// 好友添加请求
					handle_newrequest(friend);
				}else if (action.equals(getString(R.string.addnotify_broadcast))){
					// 对方答应添加好友
					remove_request(friend);
					friendlistadapter.add(friend);
					friendlistadapter.commit();					
				}else if (action.equals(getString(R.string.refusenotify_broadcast))){
					// 对方拒绝添加好友
					remove_request(friend);
				}
			}
		}
	}

	/*
	 * 使用 JSON 数据更新好友 List 并刷新好友列表画面
	 */
	public void flushFriendList(JSONArray arr) {

		if (arr == null) {

			Log.w("TAG", "ARR-NULL");
			return;
		}

		friendarraylist.clear();

		for (int i = 0; i < arr.length(); i++) {
			/* 更新好友列表 */
			JSONObject obj = null;
			try {
				obj = arr.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.w("TAG", "" + i);

			FriendDescriptor friend = new FriendDescriptor();
			try {
				friend.setname(obj.getString("name"));
				friend.setphone(obj.getString("phone"));
				friend.setip(obj.getString("ip"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 得到拼音
			String pinyin = ctp.getSelling(friend.getname());
			// 得到首字母
			friend.sethead(pinyin.substring(0, 1).toUpperCase());

			if (friend.gethead().matches("[A-Z]")) {
				// 英文字母，则把他转换为大写
				friend.sethead(friend.gethead().toUpperCase());
			} else {
				// 特殊字符全部转化成#
				friend.sethead("#");
			}

			// 准备存放在本地的头像文件
			File file = new File(getActivity().getFilesDir() + "/"
					+ friend.getphone() + ".jpg");
			if (file.exists() == false) {
				// 如果本地头像文件不存在，则从网络上读取
				Log.w("FILE_EXISTS=FALSE", getActivity().getFilesDir() + "/"
						+ friend.getphone());
				friend.setimage(((BitmapDrawable) getResources().getDrawable(
						R.drawable.ic_launcher)).getBitmap());
			} else {
				// 本地头像存在，把他读出来。如果读取失败，那么他会返回默认头像

				Bitmap image = getParent().getImageFromLocal(
						getParent().getPhone());
				if (image != null)
					friend.setimage(image);
			}
			// friend.setimage(WechatSocket.getInstance().getImage(getActivity(),
			// friend.getphone()));
			friendarraylist.add(friend);
		}
		// ..
		// 然后对 ttt 里的数据进行排序，排序的方法存放在 pinyinComparator 内
		Collections.sort(friendarraylist, new PinyinComparator());
		friendlistadapter.notifyDataSetChanged();
	}

	/*
	 * 使用 JSON 数据更新好友请求 List 并刷新画面
	 */
	public void flushRequestList(JSONArray arr) {

		int i;

		if (arr == null)
			return;
		addrequestlist.clear();

		for (i = 0; i < arr.length(); i++) {
			/* 更新好友列表 */
			JSONObject obj = null;
			try {
				obj = arr.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			FriendDescriptor friend = new FriendDescriptor();
			try {
				friend.setname(obj.getString("srcname"));
				friend.setphone(obj.getString("phone"));
				friend.setprivate(obj.getString("private_data"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			handle_newrequest(friend);
		}
	}

	/*
	 * 插入新的好友请求
	 */
	void handle_newrequest(FriendDescriptor friend) {

		if (friend == null)
			return;

		addrequestlist.add(friend);

		if (addrequestlist.size() == 0) {
			// 如果没有好友请求，就隐藏他
			request_number.setVisibility(View.GONE);
		} else {
			// 有好友请求，不隐藏了。
			request_number.setVisibility(View.VISIBLE);
			request_number.setText("" + addrequestlist.size());
		}
	}

	/*
	 * 删除一个好友请求
	 */
	void remove_request(FriendDescriptor friend) {
		
		int i;
		for (i = 0; i < addrequestlist.size(); i++) {

			FriendDescriptor ifriend = (FriendDescriptor) addrequestlist.get(i);
			if (ifriend.getphone().equals(friend.getphone()))
				addrequestlist.remove(i);
		}
		
		request_number.setText(""+addrequestlist.size());
		if (addrequestlist.size() == 0) {

			request_number.setVisibility(View.GONE);
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

			// 得到拼音
			String pinyin = ctp.getSelling(friend.getname());
			// 得到首字母
			friend.sethead(pinyin.substring(0, 1).toUpperCase());
			
			// Get user-image
			friend.setimage(getParent().getImageFromLocal(friend.getphone()));
			Log.w("ADD-IMAGE",friend.getphone());
			
			if (friend.gethead().matches("[A-Z]")) {
				// 英文字母，则把他转换为大写
				friend.sethead(friend.gethead().toUpperCase());
			} else {
				// 特殊字符全部转化成#
				friend.sethead("#");
			}

			myfriendlist.add(friend);
		}
		
		public void del(String phone){
			
			for (int i = 0;i < myfriendlist.size();i++){
				
				FriendDescriptor friend = myfriendlist.get(i);
				if (friend.getphone().equals(phone)){
					myfriendlist.remove(i);
					break;
				}
			}
		}
		
		/*
		 * 提交所有添加和删除
		 */
		public void commit() {

			Collections.sort(myfriendlist, new PinyinComparator());
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

			Log.w("TAG", "getView" + friend.getphone());

			if (convertView == null) {

				holder = new ViewHolder();

				convertView = (ViewGroup) inflater.inflate(
						R.layout.friendlist_item, null);

				// 缓存View内的子控件
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.catalog = (TextView) convertView
						.findViewById(R.id.catalog);
				holder.touxiang = (ImageView) convertView
						.findViewById(R.id.user_image);
				
				
				
				convertView.setTag(holder);

			} else {

				// 既然缓存的View已经存在，我们再取出他的缓存TextView
				holder = (ViewHolder) convertView.getTag();
			}
			
			Log.w("get-IMAGE",friend.getphone());
			holder.name.setText(friend.getname());
			holder.touxiang.setImageBitmap(friend.getImage());

			int i;
			for (i = 0; i < myfriendlist.size(); i++) {
				/* 遍历好友列表，查看当前好友的名字的首字母是不是列表里第一个出现的。 */
				FriendDescriptor cur = myfriendlist.get(i);
				if (TextUtils.equals(cur.gethead(), friend.gethead()))
					break;
			}

			if (i == position) {
				/* 是第一个出现的，显示字母 */

				holder.catalog.setVisibility(View.VISIBLE);
				holder.catalog.setText(friend.gethead());
			} else {
				/*
				 * 当我们插入一条新项的时候，他也会给出一个别的项的缓存来给我们使用
				 * 但这个缓存可能是一个带字母的，所以我们除了在上面修改他内容外，还要隐藏掉他
				 */
				holder.catalog.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	class ViewHolder {
		ImageView touxiang;
		TextView catalog;
		TextView name;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.w("TAG", "onCreateView");
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}
}
