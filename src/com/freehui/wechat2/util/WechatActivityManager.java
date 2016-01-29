package com.freehui.wechat2.util;

import java.util.ArrayList;

import com.freehui.wechat2.baseclass.BaseActivity;

/*
 * 每打开一个 Activity，程序就会在这里记录下他。
 * */
public class WechatActivityManager {
	
	public static ArrayList<BaseActivity> activitylist = new ArrayList<BaseActivity>();
	
	/*
	 * 添加一个 Acitivty
	 */
	public static void addActivity(BaseActivity activity){
		
		activitylist.add(activity);
	}
	/*
	 * 删除一个 Acitivty
	 * */
	public static void delActivity(BaseActivity activity){
		
		activitylist.remove(activity);
	}
	
	/*
	 * 得到最顶层的 Activity
	 */
	public static BaseActivity getTopActivity(){

		return (BaseActivity)(activitylist.get(activitylist.size()-1));
	}
	
	/*
	 * 关闭打开的所有 Acitivty
	 * */
	public static void finishAll(){
		
		int i;
		int size = activitylist.size();
		for (i = 0;i < size;i++){
		
			if (activitylist.get(i) != null)
				if (((BaseActivity)(activitylist.get(i))).readlock() == false)
					((BaseActivity)(activitylist.get(i))).finish();
		}
		activitylist.clear();
	}
}
