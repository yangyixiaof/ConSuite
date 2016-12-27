package cn.yyx.research.integrate;

import java.io.InputStream;

import cn.yyx.research.slice.Slicer;

public class ConcatMain {

	public static void main(String[] args) {
		// org.evosuite.EvoSuite.main(args);
		
		try {
			Runtime runtime = Runtime.getRuntime();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < args.length; i++) {
				sb.append(" " + args[i]);
			}
			Process process = runtime.exec("java -jar evosuite-master-1.0.4-SNAPSHOT.jar -Dassertions=false" + sb.toString());
			InputStream es = process.getErrorStream();
			InputStream is = process.getInputStream();
			new Thread(new DisplayInfo(is, System.out)).start();
			new Thread(new DisplayInfo(es, System.err)).start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Slicer s = new Slicer("evosuite-tests");
		s.SliceSuffixedTestInDirectory("_ESTest");
	}

}
