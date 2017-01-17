package cn.yyx.research.util;

import java.util.Map;

public class EnvironmentUtil {
	
	public static boolean IsWindows()
	{
		return System.getProperty("os.name").toLowerCase().indexOf("windows")>=0;
	}
	
	public static void HandleProcessEnvironment(Map<String, String> map, String JAVA_HOME_REP)
	{
		try {
			String replaced = map.get("JAVA_HOME").replace('\\', '/');
			String replace = JAVA_HOME_REP.replace('\\', '/');
			
			if (map.get("JAVA_HOME") == null || map.get("JAVA_HOME").equals(""))
			{
				System.err.println("No java home?");
				System.exit(1);
			}
			map.put("JAVA_HOME", map.get("JAVA_HOME").replace('\\', '/').replace(replaced, replace));
			
			String path = "Path";
			if (map.get("PATH") != null && !map.get("PATH").equals("")) {
				path = "PATH";
			}
			if (map.get(path) != null)
			{
				map.put(path, map.get(path).replace('\\', '/').replace(replaced, replace));
			}
			if (map.get("CLASSPATH") != null)
			{
				map.put("CLASSPATH", map.get("CLASSPATH").replace('\\', '/').replace(replaced, replace));
			}
		} catch (Exception e) {
			System.err.println(map.get("Path"));
			System.err.println(map.get("PATH"));
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}
