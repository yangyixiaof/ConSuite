package cn.yyx.research.util;

import java.io.File;
import java.io.InputStream;

public class ResourceUtil {
	
	public static final String Evosuite_Master = ".evosuite-master-1.0.4-SNAPSHOT.jar";
	public static final String Calfuzzer = ".calfuzzer.jar";
	public static final String Evosuite_Runtime = ".evosuite-standalone-runtime-1.0.4-SNAPSHOT.jar";
	public static final String Ant_Run = ".run.xml";
	
	private static String TrimDot(String filename)
	{
		if (filename.startsWith("."))
		{
			filename = filename.substring(1);
		}
		return filename;
	}
	
	public static void InitialEnvironment()
	{
		InputStream is = null;
		is = ResourceUtil.class.getResourceAsStream("/resources/" + TrimDot(Evosuite_Master));
		FileUtil.ReadFromStreamAndWriteToFile(is, Evosuite_Master);
		is = ResourceUtil.class.getResourceAsStream("/resources/" + TrimDot(Calfuzzer));
		FileUtil.ReadFromStreamAndWriteToFile(is, Calfuzzer);
		is = ResourceUtil.class.getResourceAsStream("/resources/" + TrimDot(Evosuite_Runtime));
		FileUtil.ReadFromStreamAndWriteToFile(is, Evosuite_Runtime);
		is = ResourceUtil.class.getResourceAsStream("/resources/" + TrimDot(Ant_Run));
		FileUtil.ReadFromStreamAndWriteToFile(is, Ant_Run);
	}
	
	public static void ClearEnvironment()
	{
		File f = null;
		f = new File(Evosuite_Master);
		if (f != null && f.exists())
		{
			f.delete();
		}
		f = new File(Calfuzzer);
		if (f != null && f.exists())
		{
			f.delete();
		}
		f = new File(Evosuite_Runtime);
		if (f != null && f.exists())
		{
			f.delete();
		}
		f = new File(Ant_Run);
		if (f != null && f.exists())
		{
			f.delete();
		}
	}
	
}
