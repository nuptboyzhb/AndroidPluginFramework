package com.taobao.trip;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @Author Zheng Haibo (mochuan)
 * @Company Alibaba Group
 * @PersonalWebsite http://www.mobctrl.net
 * @version $Id: BuildUtils.java, v 0.1 2016年1月8日 下午8:47:42 mochuan.zhb Exp $
 * @Description
 */
public class BuildApkUtils {

	private static final String ANDROID_JAR_PATH = "D:\\android_sdk_for_studio\\platforms\\android-22\\android.jar";

	private static final String AAPT_PATH = "D:\\mochuan.zhb\\android-sdks\\build-tools\\22.0.1\\aapt_alitrip.exe";

	private static final String DX_PATH = "D:\\android_sdk_for_studio\\build-tools\\22.0.1\\dx.bat";

	private static final String SDK_LIB_JAR_PATH = "D:\\android_sdk_for_studio\\tools\\lib\\sdklib.jar";

	private static final String batchDir = System.getProperty("user.dir")
			+ "\\batch\\";

	private String projectDir;
	private int packageId = 127;

	public BuildApkUtils() {

	}

	public BuildApkUtils(String projectDir) {
		this.projectDir = projectDir;
	}

	public BuildApkUtils(String projectDir, int packageId) {
		this.projectDir = projectDir;
		this.packageId = packageId;
	}

	public void buildUnsingedApk() {
		clearDir(batchDir);
		clearDir(projectDir + "\\bin");
		generateR(projectDir, packageId);
		compileJavaFiles(projectDir);
		buildDexFile(projectDir);
		complieResources(projectDir, packageId);
		buildUnsignedApk(projectDir, "unsigned.apk");
		mergeExeBatchFiles();
	}

	/**
	 * 第一步：产生R文件
	 * 
	 * @param projectDir
	 * @param packageId
	 */
	private static void generateR(String projectDir, int packageId) {
		StringBuffer command = new StringBuffer();
		command.append(AAPT_PATH).append(" package -f -m -J ")
				.append(projectDir).append("\\gen ").append("-S ")
				.append(projectDir).append("\\res ").append("-M ")
				.append(projectDir).append("\\AndroidManifest.xml ")
				.append(" -A ").append(projectDir).append("\\assets ")
				.append("-I ").append(ANDROID_JAR_PATH)
				.append(" --non-constant-id -x --package-id ")
				.append(packageId);
		buildExeBatchFiles(command.toString(), "1.bat");
	}

	/**
	 * 编译java文件
	 * 
	 * @param projectDir
	 */
	private static void compileJavaFiles(String projectDir) {
		StringBuffer command = new StringBuffer();
		command.append("javac -target 1.5 -bootclasspath ")
				.append(ANDROID_JAR_PATH).append(" -d ").append(projectDir)
				.append("\\bin ");
		List<String> javaFilePaths = new ArrayList<String>();
		findJavaFiles(projectDir + "\\src", javaFilePaths);
		findJavaFiles(projectDir + "\\gen", javaFilePaths);
		for (String javaPath : javaFilePaths) {
			command.append(javaPath).append(" ");
		}
		command.append("-classpath ").append(projectDir)
				.append("\\libs\\.*jar");
		buildExeBatchFiles(command.toString(), "2.bat");
	}

	/**
	 * 创建dex文件
	 * 
	 * @param projectDir
	 */
	private static void buildDexFile(String projectDir) {
		StringBuffer command = new StringBuffer();
		command.append(DX_PATH).append(" --dex --output=").append(projectDir)
				.append("\\bin\\classes.dex").append(" ").append(projectDir)
				.append("\\bin");
		buildExeBatchFiles(command.toString(), "3.bat");
	}

	/**
	 * 编译资源文件
	 * 
	 * @param projectDir
	 */
	private static void complieResources(String projectDir, int packageId) {
		StringBuffer command = new StringBuffer();
		command.append(AAPT_PATH).append(" package -f -M ").append(projectDir)
				.append("\\AndroidManifest.xml ").append("-S ")
				.append(projectDir).append("\\res ").append("-I ")
				.append(ANDROID_JAR_PATH).append(" -A ").append(projectDir)
				.append("\\assets ").append(" -F ").append(projectDir)
				.append("\\bin\\resources.ap_")
				.append(" --non-constant-id -x --package-id ")
				.append(packageId);
		buildExeBatchFiles(command.toString(), "4.bat");
	}

	/**
	 * 生成未签名的apk
	 * 
	 * @param projectDir
	 * @param apkName
	 */
	private static void buildUnsignedApk(String projectDir, String apkName) {
		StringBuffer command = new StringBuffer();
		command.append("java -cp ").append(SDK_LIB_JAR_PATH)
				.append(" com.android.sdklib.build.ApkBuilderMain ")
				.append(projectDir).append("\\bin\\").append(apkName)
				.append(" -v -u -z ").append(projectDir)
				.append("\\bin\\resources.ap_").append(" -f ")
				.append(projectDir).append("\\bin\\classes.dex")
				.append(" -rf ").append(projectDir).append("\\src");
		buildExeBatchFiles(command.toString(), "5.bat");
	}

	/**
	 * 递归查找
	 * 
	 * @param projectDir
	 * @param javaFilePaths
	 */
	private static void findJavaFiles(String projectDir,
			List<String> javaFilePaths) {
		File file = new File(projectDir);
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		for (File f : files) {
			if (f.isDirectory()) {
				findJavaFiles(f.getAbsolutePath(), javaFilePaths);
			} else {
				if (f.getAbsolutePath().endsWith(".java")) {
					javaFilePaths.add(f.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * 清理目录
	 * 
	 * @param projectDir
	 */
	private static void clearDir(String projectDir) {
		File file = new File(projectDir);
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		for (File f : files) {
			if (f.isDirectory()) {
				clearDir(f.getAbsolutePath());
			} else {
				f.delete();
			}
		}
	}

	/**
	 * 创建批处理文件
	 * 
	 * @param command
	 * @param file
	 */
	private static void buildExeBatchFiles(String command, String fileName) {
		System.out.println(command);
		if (!new File(batchDir).exists()) {
			new File(batchDir).mkdirs();
		}
		String filePath = batchDir + fileName;
		try {
			writeFile(filePath, command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void mergeExeBatchFiles() {
		File file = new File(batchDir);
		System.out.println("debug:current path = " + file.getAbsolutePath());
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File file1, File file2) {
				if (file1.lastModified() > file2.lastModified()) {
					return 1;
				} else if (file1.lastModified() < file2.lastModified()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		StringBuffer command = new StringBuffer();
		for (File f : files) {
			command.append("call ").append(f.getAbsolutePath()).append("\n");
		}
		try {
			String filePath = batchDir + "build.bat";
			writeFile(filePath, command.toString());
			Runtime.getRuntime().exec("cmd /c start " + filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写文件
	 * 
	 * @param filePath
	 * @param sets
	 * @throws IOException
	 */
	private static void writeFile(String filePath, String content)
			throws IOException {
		FileWriter fw = new FileWriter(filePath);
		PrintWriter out = new PrintWriter(fw);
		out.write(content);
		out.println();
		fw.close();
		out.close();
	}

}
