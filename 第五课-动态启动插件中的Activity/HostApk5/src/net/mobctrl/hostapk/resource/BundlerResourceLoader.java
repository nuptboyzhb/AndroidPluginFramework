package net.mobctrl.hostapk.resource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import net.mobctrl.hostapk.utils.AssetsManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

/**
 * @Author Zheng Haibo
 * @Mail mochuan.zhb@alibaba-inc.com
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: LoaderResManager.java, v 0.1 2015年12月11日 下午7:58:59 mochuan.zhb
 *          Exp $
 * @Description 动态加载资源的管理器
 */
public class BundlerResourceLoader {

	private static final String TAG = "BundlerResourceLoader";

	/**
	 * 创建AssetManager
	 * 
	 * @param apkPaths
	 * @return
	 */
	public static AssetManager createAssetManager(List<String> apkPaths) {
		if (apkPaths == null || apkPaths.size() == 0) {
			return null;
		}
		try {
			AssetManager assetManager = AssetManager.class.newInstance();
			return modifyAssetManager(assetManager, apkPaths);
		} catch (Throwable th) {
			System.out.println("debug:createAssetManager :" + th.getMessage());
			th.printStackTrace();
		}
		return null;
	}

	/**
	 * 修改AssetManager
	 * 
	 * @param assetManager
	 * @param apkPaths
	 * @return
	 */
	private static AssetManager modifyAssetManager(AssetManager assetManager,
			List<String> apkPaths) {
		if (apkPaths == null || apkPaths.size() == 0) {
			return null;
		}
		try {
			for (String apkPath : apkPaths) {
				try {
					AssetManager.class.getDeclaredMethod("addAssetPath",
							String.class).invoke(assetManager, apkPath);
				} catch (Throwable th) {
					System.out.println("debug:createAssetManager :"
							+ th.getMessage());
					th.printStackTrace();
				}
			}
			return assetManager;
		} catch (Throwable th) {
			System.out.println("debug:createAssetManager :" + th.getMessage());
			th.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取整个App的资源管理器中的资源
	 * 
	 * @param context
	 * @param apkPath
	 * @return
	 */
	public static Resources getAppResource(Context context) {
		System.out.println("debug:getAppResource ...");
		AssetsManager.copyAllAssetsApk(context);
		// 获取dex文件列表
		File dexDir = context.getDir(AssetsManager.APK_DIR,
				Context.MODE_PRIVATE);
		File[] szFiles = dexDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(AssetsManager.FILE_FILTER);
			}
		});
		if (szFiles == null || szFiles.length == 0) {
			return context.getResources();
		}
		System.out.println("debug:getAppResource szFiles = "+szFiles.length);
		List<String> apkPaths = new ArrayList<String>();
		for (File f : szFiles) {
			Log.i(TAG, "load file:" + f.getName());
			apkPaths.add(f.getAbsolutePath());
			System.out.println("debug:apkPath = " + f.getAbsolutePath());
		}
		AssetManager assetManager = modifyAssetManager(context.getAssets(),
				apkPaths);
		AppResource resources = new AppResource(
				assetManager, context.getResources().getDisplayMetrics(),
				context.getResources().getConfiguration());
		return resources;
	}

}
