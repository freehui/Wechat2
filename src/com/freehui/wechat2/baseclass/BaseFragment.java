package com.freehui.wechat2.baseclass;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
	
	public BaseActivity getParent(){
		
		return (BaseActivity)getActivity();
	}
}
