package net.mobctrl.normal.apk;

import android.content.Context;
import android.widget.Toast;

/**
 * @Author Zheng Haibo
 * @Mail mochuan.zhb@alibaba-inc.com
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: Utils.java, v 0.1 2015年12月10日 下午2:25:39 mochuan.zhb Exp $
 * @Description
 */
public class Utils {
	
	/**
	 * 计算 a+b
	 * 
	 * @param context
	 * @param a
	 * @param b
	 * @param name
	 */
	public int printSum(Context context,int a,int b,String name){
		int sum = a + b;
		Toast.makeText(context, name+":"+sum, Toast.LENGTH_SHORT).show();
		return sum;
	}
	
	public void printFileName(Context context,String name){
		new FileUtils().print(context,name);
	}

}
