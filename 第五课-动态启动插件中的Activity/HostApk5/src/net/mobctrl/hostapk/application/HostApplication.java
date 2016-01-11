package net.mobctrl.hostapk.application;

import net.mobctrl.hostapk.classloader.AssetsMultiDexLoader;
import net.mobctrl.hostapk.resource.BundlerResourceLoader;
import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * @Author Zheng Haibo (mochuan)
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: HostApplication.java, v 0.1 2016年1月7日 上午11:20:53 mochuan.zhb
 *          Exp $
 * @Description HostApplication
 */
public class HostApplication extends Application {

	private Resources mAppResources = null;
	private Resources mOldResources = null;

	@Override
	public void onCreate() {
		super.onCreate();
		mOldResources = super.getResources();
		AssetsMultiDexLoader.install(this);// 加载assets中的apk
		System.out.println("debug:HostApplication onCreate");
		installResource();
	}

	@Override
	public Resources getResources() {
		if(mAppResources == null){
			return mOldResources;
		}
		return this.mAppResources;
	}

	private void installResource() {
		if (mAppResources == null) {
			mAppResources = BundlerResourceLoader.getAppResource(this);// 加载assets中的资源对象
		}
	}

	@Override
	public AssetManager getAssets() {
		if (this.mAppResources == null) {
			return super.getAssets();
		}
		return this.mAppResources.getAssets();
	}

}
