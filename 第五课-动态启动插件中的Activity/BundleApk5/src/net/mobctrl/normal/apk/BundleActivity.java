package net.mobctrl.normal.apk;

import android.os.Bundle;
import android.view.View;

/**
 * @Author Zheng Haibo (mochuan)
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: BundleActivity.java, v 0.1 2016年1月8日 下午1:21:46 mochuan.zhb Exp $
 * @Description
 */
public class BundleActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bundle_layout);
		findViewById(R.id.text_view).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
			}
		});
	}
	
	
	
}