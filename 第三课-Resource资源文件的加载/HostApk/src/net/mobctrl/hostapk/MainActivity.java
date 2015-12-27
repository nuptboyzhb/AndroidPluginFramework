package net.mobctrl.hostapk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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

			}
		});
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
	}

}
