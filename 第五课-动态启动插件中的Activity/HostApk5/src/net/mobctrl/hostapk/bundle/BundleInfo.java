package net.mobctrl.hostapk.bundle;

import java.io.Serializable;

/**
 * @Author Zheng Haibo (mochuan)
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: BundleInfo.java, v 0.1 2016年1月7日 上午11:38:10 mochuan.zhb Exp $
 * @Description 每个Bundle的信息
 */
public class BundleInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String packageName;//包名
	
	private String apkPath;//apk路径
	
	private int packageId;//包ID

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getApkPath() {
		return apkPath;
	}

	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}

}
