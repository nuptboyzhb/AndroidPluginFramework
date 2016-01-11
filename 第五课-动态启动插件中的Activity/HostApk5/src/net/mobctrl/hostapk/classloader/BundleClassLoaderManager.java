package net.mobctrl.hostapk.classloader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import net.mobctrl.hostapk.utils.AssetsManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

/**
 * @Author Zheng Haibo
 * @Mail mochuan.zhb@alibaba-inc.com
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: BundleClassLoaderManager.java, v 0.1 2015年12月11日 下午7:30:59
 *          mochuan.zhb Exp $
 * @Description
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class BundleClassLoaderManager {

	public static List<BundleDexClassLoader> bundleDexClassLoaderList = new ArrayList<BundleDexClassLoader>();

	/**
	 * 加载Assets里的apk文件
	 * @param context
	 */
	public static void install(Context context) {
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
		if(szFiles == null || szFiles.length == 0){
			return;
		}
		for (File f : szFiles) {
			System.out.println("debug:load file:" + f.getName());
			//加载apk，生成对应的ClassLoader
			BundleDexClassLoader bundleDexClassLoader = new BundleDexClassLoader(
					f.getAbsolutePath(), dexDir.getAbsolutePath(), null,
					context.getClassLoader());
			bundleDexClassLoaderList.add(bundleDexClassLoader);
		}
	}
	
	/**
	 * 查找类
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(Context context,String className) throws ClassNotFoundException {
		try {
			Class<?> clazz = context.getClassLoader().loadClass(className);
			if (clazz != null) {
				System.out.println("debug: class find in main classLoader");
				return clazz;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (BundleDexClassLoader bundleDexClassLoader : bundleDexClassLoaderList) {
			try {
				Class<?> clazz = bundleDexClassLoader.loadClass(className);
				if (clazz != null) {
					System.out.println("debug: class find in bundle classLoader");
					return clazz;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new ClassCastException(className + " not found exception");
	}
}
