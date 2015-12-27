package net.mobctrl.hostapk;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";

	private TextView invokeTv;
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BundleClassLoaderManager.install(getApplicationContext());
		invokeTv = (TextView) findViewById(R.id.invoke_tv);
		invokeTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this,BundleActivity.class));
			}
		});
		BundlerResourceLoader.getBundleResource(getApplicationContext());
		imageView = (ImageView)findViewById(R.id.image_view_iv);
		
	}
	
	

}
