#Android插件化（二）：使用DexClassLoader动态加载assets中的apk#

##简介
上一篇博客讲到，我们可以使用MultiDex.java加载离线的apk文件。需要注意的是，apk中的类是加载到当前的PathClassLoader当中的，如果apk文件过多，可能会出现ANR的情况。那么，我们能不能使用DexClassLoader加载apk呢？当然是可以的！首先看一下[Doc文档](http://developer.android.com/intl/zh-cn/reference/dalvik/system/DexClassLoader.html).

```html
A class loader that loads classes from .jar and .apk files containing a classes.dex entry. This can be used to execute code not installed as part of an application.
```

也就是说，DexClassLoader可以加载一个含有classes.dex文件的压缩包，既可以是jar也可以是apk。那么加载一个离线的apk文件需要注意哪些呢？<br>

- 1.DexClassLoader的构造方法：<br>
DexClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent)

- 2.私有目录<br>
This class loader requires an application-private, writable directory to cache optimized classes. 

了解到上述两点，我们就可以根据DexClassLoader所需要的参数，动态加载assets中的apk了。

##源码<br>

### BundleClassLoaderManager<br>
该类主要是负责管理这些DexClassLoader的，首先，我们定义了一个叫做BundleDexClassLoader的类，它继承自DexClassLoader，用于加载离线的apk文件。每一个apk文件对应一个BundleDexClassLoader,而BundleClassLoaderManager则保存了一个List<BundleDexClassLoader>,在加载的时候，用于查找类。具体代码如下：
<br>
```java
package net.mobctrl.hostapk;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

/**
 * @Author Zheng Haibo
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
		for (File f : szFiles) {
			System.out.println("debug:load file:" + f.getName());
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

```

注意点：<br>

- 1.install方法<br>


- 2.loadClass方法<br>

### DEMO运行


### 改进方案





