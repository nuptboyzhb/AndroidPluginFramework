package com.taobao.trip;


/**
 * @Author Zheng Haibo (mochuan)
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: BuildMain.java, v 0.1 2016年1月7日 下午9:40:06 mochuan.zhb Exp $
 * @Description
 */
public class BuildMain {

	private static final String PROJECT_PATH = "C:\\Users\\mochuan.zhb\\newworkspace\\BundleApk5";

	private static final int PACKAGE_ID = 5;

	public static void main(String[] args) {
		new BuildApkUtils(PROJECT_PATH, PACKAGE_ID).buildUnsingedApk();
	}

}
