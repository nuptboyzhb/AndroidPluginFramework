package net.mobctrl.hostapk.resource;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * @Author Zheng Haibo (mochuan)
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: ManagerResource.java, v 0.1 2016年1月7日 下午5:19:10 mochuan.zhb Exp $
 * @Description 整个AppResource的托管
 */
public class AppResource extends Resources {
	
	public AppResource(AssetManager assets, DisplayMetrics metrics,
			Configuration config) {
		super(assets, metrics, config);
	}
}
