#Android加载插件apk中的Resource资源

##简介
如何加载未安装apk中的资源文件呢？我们从android.content.res.AssetManager.java的源码中发现，它有一个私有方法addAssetPath，只需要将apk的路径作为参数传入，我们就可以获得对应的AssetsManager对象，然后我们就可以使用AssetsManager对象，创建一个Resources对象，然后就可以从Resource对象中访问apk中的资源了。总结如下：<br>

- 1.新建一个AssetManager对象
- 2.通过反射调用addAssetPath方法
- 3.以AssetsManager对象为参数，创建Resources对象即可。

代码如下：<br>
```java
package net.mobctrl.hostapk;

import java.io.File;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: LoaderResManager.java, v 0.1 2015年12月11日 下午7:58:59 mochuan.zhb
 *          Exp $
 * @Description 动态加载资源的管理器
 */
public class BundlerResourceLoader {

	private static AssetManager createAssetManager(String apkPath) {
		try {
			AssetManager assetManager = AssetManager.class.newInstance();
			try {
				AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(
						assetManager, apkPath);
			} catch (Throwable th) {
				System.out.println("debug:createAssetManager :"+th.getMessage());
				th.printStackTrace();
			}
			return assetManager;
		} catch (Throwable th) {
			System.out.println("debug:createAssetManager :"+th.getMessage());
			th.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取Bundle中的资源
	 * @param context
	 * @param apkPath
	 * @return
	 */
	public static Resources getBundleResource(Context context){
		AssetsManager.copyAllAssetsApk(context);
		File dir = context.getDir(AssetsManager.APK_DIR, Context.MODE_PRIVATE);
		String apkPath = dir.getAbsolutePath()+"/BundleApk.apk";
		System.out.println("debug:apkPath = "+apkPath+",exists="+(new File(apkPath).exists()));
		AssetManager assetManager = createAssetManager(apkPath);
	    return new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
	}

}

```

##DEMO
注意：我们使用Resources对象，获取资源时，传递的ID必须是离线apk中R文件对应的资源的ID。如果使用getIdentifier方法，第一个参数是资源名称，第二个参数是资源类型，第三个参数是离线apk的包名，切记第三个参数。<br>

```java
Resources resources = BundlerResourceLoader.getBundleResource(getApplicationContext());
		imageView = (ImageView)findViewById(R.id.image_view_iv);
		if(resources != null){
			String str = resources.getString(resources.getIdentifier("test_str", "string", "net.mobctrl.normal.apk"));
			String strById = resources.getString(0x7f050001);//注意，id参照Bundle apk中的R文件
			System.out.println("debug:"+str);
			Toast.makeText(getApplicationContext(),strById, Toast.LENGTH_SHORT).show();
		
			Drawable drawable = resources.getDrawable(0x7f020000);//注意，id参照Bundle apk中的R文件
			imageView.setImageDrawable(drawable);
		}
```

上述代码是加载离线apk中的字符串和Drawable资源，那么layout资源呢？

##问题引入
我们使用LayoutInflate对象，一般使用方法如下：<br>
```java
View view = LayoutInflater.from(context).inflate(R.layout.main_fragment, null);
```
其中，R.layout.main_fragment我们可以通过上述方法获取其ID，那么关键的一步就是如何生成一个context？直接传入当前的context是不行的。
解决方案有2个：<br>
- 1.创建一个自己的ContextImpl，Override其方法。<br>
- 2.通过反射，直接替换当前context的mResources私有成员变量。<br>
当然，我们是使用第二种方案：<br>
```java
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
			field.set(context, mBundleResources);
			System.out.println("debug:repalceResources succ");
		} catch (Exception e) {
			System.out.println("debug:repalceResources error");
			e.printStackTrace();
		}
	}

```
<br>
我们在Activity的attachBaseContext方法中，对Context的mResources进行替换，这样，我们就可以加载离线apk中的布局了。<br>

##资源文件的打包过程

如果想要做到插件化，需要了解Android资源文件的打包过程，这样可以为每一个插件进行编号，然后按照规则生成R文件。例如，以携程DynamicAPK为例，它将插件的R文件按照如下规则：<br>

- 1.R文件为int型，前8位代表插件的Id，其中两个特殊的Id：Host是0x7f，android系统自带的是以0x01开头.
- 2.紧跟着的8位是区分资源类型的，比如layout，id,string,dimen等
- 3.后面16位是资源的编号

按照上述规则生成对应的插件apk。然后在运行时，我们可以写一个ResourceManager类，它继承自Resource对象，然后所有的Activity，都将其context的mResource成员变量修改为ResourceManager类，然后Override其方法，然后在加载资源时，根据不同的id的前缀，查找对应插件的Resource即可。也就是说，用一个类做分发。


##源码

https://github.com/nuptboyzhb/AndroidPluginFramework

##Android插件化相关资料

- 1.Android动态加载基础 ClassLoader工作机制 http://segmentfault.com/a/1190000004062880<br>
- 2.Android动态加载黑科技 动态创建Activity模式 http://segmentfault.com/a/1190000004077469<br>
- 3.Android插件化框架Github总结 https://github.com/liaohuqiu/android-dynamic-load-awesome<br>
- 4.携程动态加载框架源码 https://github.com/CtripMobile/DynamicAPK<br>
- 5.携程Android App插件化和动态加载实践 http://www.infoq.com/cn/articles/ctrip-android-dynamic-loading<br>
- 6.dex分包变形记 http://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=401345907&idx=1http://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=401345907&idx=1&sn=debdddf25950aaaa10f575472629b557<br>
- 7.各大热补丁方案分析和比较 http://blog.zhaiyifan.cn/2015/11/20/HotPatchCompare/<br>
- 8.Android App 线上热修复方案 http://lirenlong.github.io/hotfix/<br>
- 9.Android 热补丁动态修复框架小结 http://blog.csdn.net/lmj623565791/article/details/49883661<br>
- 10.Android热更新实现原理 http://blog.csdn.net/lzyzsd/article/details/49843581<br>
- 11.【新技能get】让App像Web一样发布新版本 http://bugly.qq.com/blog/?p=781<br>
- 12.Android动态加载技术 系列索引 http://segmentfault.com/a/1190000004086213<br>
- 13.Android对第三方类库运行时加载 http://blog.csdn.net/dzg1977/article/details/41683173<br>
- 14.关于Android如何动态加载res http://nobodycare.me/2014/11/07/about-loading-res-from-apk-directly/<br>
- 15.Android应用程序资源的编译和打包过程分析 http://blog.csdn.net/luoshengyang/article/details/8744683<br>
- 16.Android应用程序资源管理器（Asset Manager）的创建过程分析 http://blog.csdn.net/luoshengyang/article/details/8791064<br>
- 17.Android 自动编译、打包生成apk文件 1 - 命令行方式 http://blog.csdn.net/androiddevelop/article/details/10948639<br>