package net.mobctrl.normal.apk;

import android.app.Activity;
import android.content.res.Resources;

/**
 * @Author Zheng Haibo (mochuan)
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: BaseActivity.java, v 0.1 2016年1月8日 下午5:32:37 mochuan.zhb Exp $
 * @Description
 */
public class BaseActivity extends Activity {
	
	@Override
	public Resources getResources() {
		return getApplication().getResources();
	}
}
