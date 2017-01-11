package cn.yyx.research.util;

public class EnvironmentUtil {
	
	public static boolean IsWindows()
	{
		return System.getProperty("os.name").toLowerCase().indexOf("windows")>=0;
	}
	
}
