package net.mobctrl.hostapk;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";

	private TextView invokeTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AssetsMultiDexLoader.install(getApplicationContext());
		invokeTv = (TextView) findViewById(R.id.invoke_tv);
		invokeTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				loadApk();
			}
		});
		loadClass();
		
	}
	
	private void loadClass(){
		try{
			Class<?> clazz = Class.forName("net.mobctrl.normal.apk.FileUtils");
			
			Constructor<?> constructor = clazz.getConstructor();
			Object bundleUtils = constructor.newInstance();

			Method printSumMethod = clazz.getMethod("print", Context.class,String.class);
			printSumMethod.setAccessible(true);
			printSumMethod.invoke(bundleUtils,
					getApplicationContext(), "Hello");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		
		super.attachBaseContext(newBase);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void loadApk() {
		try {
			Class<?> clazz = getClassLoader()
					.loadClass("net.mobctrl.normal.apk.Utils");
			Constructor<?> constructor = clazz.getConstructor();
			Object bundleUtils = constructor.newInstance();

			Method printSumMethod = clazz.getMethod("printSum", Context.class,
					int.class, int.class, String.class);
			printSumMethod.setAccessible(true);
			Integer sum = (Integer)printSumMethod.invoke(bundleUtils,
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

}
