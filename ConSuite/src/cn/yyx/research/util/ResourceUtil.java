package cn.yyx.research.util;

import java.io.File;
import java.io.InputStream;

public class ResourceUtil {
	
	public static void InitialEnvironment()
	{
		InputStream is = null;
		is = ResourceUtil.class.getResourceAsStream("/resources/evosuite-master-1.0.4-SNAPSHOT.jar");
		FileUtil.ReadFromStreamAndWriteToFile(is, "evosuite-master-1.0.4-SNAPSHOT.jar");
		is = ResourceUtil.class.getResourceAsStream("/resources/calfuzzer.jar");
		FileUtil.ReadFromStreamAndWriteToFile(is, "calfuzzer.jar");
		is = ResourceUtil.class.getResourceAsStream("/resources/evosuite-standalone-runtime-1.0.4-SNAPSHOT.jar");
		FileUtil.ReadFromStreamAndWriteToFile(is, "evosuite-standalone-runtime-1.0.4-SNAPSHOT.jar");
		is = ResourceUtil.class.getResourceAsStream("/resources/run.xml");
		FileUtil.ReadFromStreamAndWriteToFile(is, "run.xml");
	}
	
	public static void ClearEnvironment()
	{
		File f = null;
		f = new File("evosuite-master-1.0.4-SNAPSHOT.jar");
		if (f != null && f.exists())
		{
			f.delete();
		}
		f = new File("calfuzzer.jar");
		if (f != null && f.exists())
		{
			f.delete();
		}
		f = new File("evosuite-standalone-runtime-1.0.4-SNAPSHOT.jar");
		if (f != null && f.exists())
		{
			f.delete();
		}
		f = new File("run.xml");
		if (f != null && f.exists())
		{
			f.delete();
		}
	}
	
}
