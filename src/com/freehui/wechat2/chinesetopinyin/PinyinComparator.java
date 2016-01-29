package com.freehui.wechat2.chinesetopinyin;

import java.util.Comparator;

import com.freehui.wechat2.baseclass.FriendDescriptor;

public class PinyinComparator implements Comparator<FriendDescriptor> {

	public int compare(FriendDescriptor o1, FriendDescriptor o2) {
		if (o1.gethead().equals("@")
				|| o2.gethead().equals("#")) {
			return -1;
		} else if (o1.gethead().equals("#")
				|| o2.gethead().equals("@")) {
			return 1;
		} else {
			return o1.gethead().compareTo(o2.gethead());
		}
	}
}