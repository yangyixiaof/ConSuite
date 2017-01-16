package cn.yyx.research.util;

import java.util.Map;

public class EnvironmentUtil {
	
	public static boolean IsWindows()
	{
		return System.getProperty("os.name").toLowerCase().indexOf("windows")>=0;
	}
	
	public static void HandleProcessEnvironment(Map<String, String> map, String JAVA_HOME)
	{
		String replaced = map.get("JAVA_HOME").replace('\\', '/');
		map.put("JAVA_HOME", map.get("JAVA_HOME").replace('\\', '/').replace(replaced, JAVA_HOME));
		map.put("PATH", map.get("PATH").replace('\\', '/').replace(replaced, JAVA_HOME));
		map.put("CLASSPATH", map.get("CLASSPATH").replace('\\', '/').replace(replaced, JAVA_HOME));
	}
	
}
