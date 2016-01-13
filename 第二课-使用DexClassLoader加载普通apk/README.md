#Android插件化（二）：使用DexClassLoader动态加载assets中的apk#
<br>
##Author:莫川
<br>
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
install方法主要是将assets中的apk全部拷贝到私有目录，然后再遍历私有目录，使用BundleDexClassLoader加载apk文件，然后将这些BundleDexClassLoader保存到数组中。

- 2.loadClass方法<br>
该方法先从当前的ClassLoader中查找需要的类，如果找不到，在从List<BundleDexClassLoader>中遍历查找。

### DEMO运行
在MainActivity中，我们可以通过如下方式，调用apk类中的方法：<br>
```java
      private void loadApk() {
		try {
			Class<?> clazz = BundleClassLoaderManager.loadClass(getApplicationContext(),
					"net.mobctrl.normal.apk.Utils");
			Constructor<?> constructor = clazz.getConstructor();
			Object bundleUtils = constructor.newInstance();

			Method printSumMethod = clazz.getMethod("printSum", Context.class,
					int.class, int.class, String.class);
			printSumMethod.setAccessible(true);
			Integer sum = (Integer) printSumMethod.invoke(bundleUtils,
					getApplicationContext(), 10, 20, "计算结果");
			System.out.println("debug:sum = " + sum);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
```

与MultiDex不同时，我们是通过BundleClassLoaderManager来加载类的，而不是当前的ClassLoader。

### 改进方案
正如BundleClassLoaderManager中的loadClass方法，其实我们创建一个ClassLoader对象，通过重写当前ClassLoader的findClass方法即可，然后在Override的findClass方法中，首先从当前ClassLoader中查找类，然后再从BundleDexClassLoader中遍历查找，这样既可以在Host项目中调用Bundle中的类，也能够在Bundle中调用Host中的类。

```java

       mClassLoader = new ClassLoader(super.getClassLoader()) {

			@Override
			protected Class<?> findClass(String className)
					throws ClassNotFoundException {
				Class clazz = BundleClassLoaderManager.loadClass(context,className);
				if (clazz == null) {
					throw new ClassNotFoundException(className);
				}
				return clazz;
			}
		};

```

##总结
上一篇博客和这一篇博客将的都是类的加载。如果所需要加载的类都是工具类，不需要加载资源等，那么上面的方案都没啥问题。但是如果加载的类是Fragment或者Activity等UI，需要引用资源文件，这又改如何处理呢？

下一篇博文：Android资源的离线加载。


##源码

https://github.com/nuptboyzhb/AndroidPluginFramework

##参考
1.DexClassLoader源码
2.DexClassLoader用法