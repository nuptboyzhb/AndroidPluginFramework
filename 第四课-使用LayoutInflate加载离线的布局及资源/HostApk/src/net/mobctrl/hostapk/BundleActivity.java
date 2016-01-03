package net.mobctrl.hostapk;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @Author Zheng Haibo
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: BundleActivity.java, v 0.1 2015年12月16日 下午3:49:00 mochuan.zhb Exp $
 * @Description
 */
public class BundleActivity extends Activity{
	
	private Resources mBundleResources;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("debug:BundleActivity onCreate...");
		//设置Bundle Layout中的ID：bundle_layout=0x7f030001;
		int bundleLayoutId = 0x7f030001;
		View bundleView  = LayoutInflater.from(this).inflate(bundleLayoutId, null);
		setContentView(bundleView);
	}
	
	@Override
	protected void attachBaseContext(Context context) {
		replaceContextResources(context);
		super.attachBaseContext(context);
	}
	
	/**
	 * 使用反射的方式，使用Bundle的Resource对象，替换Context的mResources对象
	 * @param context
	 */
	public void replaceContextResources(Context context){
		try {
			Field field = context.getClass().getDeclaredField("mResources");
			field.setAccessible(true);
			if (null == mBundleResources) {
				mBundleResources = BundlerResourceLoader.bundleResMap.get("bundle_apk");
			}
			field.set(context, mBundleResources);
			System.out.println("debug:repalceResources succ");
		} catch (Exception e) {
			System.out.println("debug:repalceResources error");
			e.printStackTrace();
		}
	}

}
