package net.mobctrl.hostapk.classloader;

import dalvik.system.DexClassLoader;

/**
 * @Author Zheng Haibo
 * @Mail mochuan.zhb@alibaba-inc.com
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: BundleDexClassLoader.java, v 0.1 2015年12月11日 下午7:12:49 mochuan.zhb Exp $
 * @Description bundle的类加载器
 */
public class BundleDexClassLoader extends DexClassLoader {

	public BundleDexClassLoader(String dexPath, String optimizedDirectory,
			String libraryPath, ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
	}

}
