package cn.yyx.research.integrate;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.yyx.research.slice.Slicer;
import cn.yyx.research.util.CommandUtil;
import cn.yyx.research.util.EnvironmentUtil;
import cn.yyx.research.util.FileIterator;
import cn.yyx.research.util.FileUtil;
import cn.yyx.research.util.SystemStreamUtil;

public class ConcatMain {

	private String Java7_Home = null;
	private String Java8_Home = null;

	private String task_type = null; // {race-analysis, deadlock-analysis,
										// atomfuzzer-analysis,
										// predictest-analysis}

	public static final String Compiled_Classpath = "classes";

	private ArrayList<String> refined_args = new ArrayList<String>();

	public ConcatMain(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String one_arg = args[i].trim();
			if (one_arg.startsWith("-Djava")) {
				if (one_arg.startsWith("-Djava7")) {
					Java7_Home = one_arg.substring("-Djava7=".length()).replace('\\', '/');
				}
				if (one_arg.startsWith("-Djava8")) {
					Java8_Home = one_arg.substring("-Djava8=".length()).replace('\\', '/');
				}
			} else if (one_arg.startsWith("-Dtask=")) {
				task_type = one_arg.substring("-Dtask=".length());
			} else {
				refined_args.add(one_arg);
			}
		}
		if (Task_type() == null) {
			task_type = "race-analysis";
		}
		if (Java7_Home == null && Java8_Home != null) {
			Java7_Home = System.getenv("JAVA_HOME").replace('\\', '/');
		} else if (Java7_Home != null && Java8_Home == null) {
			Java8_Home = System.getenv("JAVA_HOME").replace('\\', '/');
		} else if (Java7_Home == null && Java8_Home == null) {
			System.err.println(
					"Error! we need only both java7 path and java8 path set through -Djava7= or -Djava8=.");
		}
	}

	public String[] GetRefinedArgs() {
		// TODO
		System.out.println(refined_args);
		String[] rarr = new String[refined_args.size()];
		rarr = refined_args.toArray(rarr);
		return rarr;
	}

	public void RunOneProcess(String[] cmd, boolean use8, DisplayInfo out, DisplayInfo err) {
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd); // "java", "-jar",
															// "Test3.jar"
			// pb.directory(new File("F:\\dist"));
			Map<String, String> map = pb.environment();

			if ((use8 && Java8_Home != null)) {
				map.put("JAVA_HOME", Java8_Home);
			}
			if (!use8 && Java7_Home != null) {
				map.put("JAVA_HOME", Java7_Home);
			}

			Process process = pb.start();
			InputStream is = process.getInputStream();
			out.setIs(is);
			InputStream es = process.getErrorStream();
			err.setIs(es);
			Thread t1 = new Thread(out);
			t1.start();
			Thread t2 = new Thread(err);
			t2.start();
			process.waitFor();
			t1.join();
			t2.join();
			Thread.sleep(1000);
			SystemStreamUtil.Flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ConcatMain cm = new ConcatMain(args);
		String[] ref_args = cm.GetRefinedArgs();
		String task_type = cm.Task_type();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ref_args.length; i++) {
			sb.append(" " + ref_args[i]);
		}
		String cmd = "java -jar evosuite-master-1.0.4-SNAPSHOT.jar -Dassertions=false" + sb.toString();
		cm.RunOneProcess(cmd.split(" "), true, new DisplayInfo(System.out), new DisplayInfo(System.err));

		Slicer s = new Slicer("evosuite-tests");
		s.SliceSuffixedTestInDirectory("_ESTest");
		SystemStreamUtil.Flush();

		// ============ start compiling! ============
		System.out.println("============ start compiling! ============");
		
		File classes = new File(Compiled_Classpath);
		if (classes.exists()) {
			FileUtil.DeleteFolder(classes.getAbsolutePath());
		}
		classes.mkdir();
		String projectcp = CommandUtil.FindProjectClassPath(ref_args);
		String pathsep = System.getProperty("path.separator");
		String classpath = "." + (projectcp == null ? "" : (pathsep + projectcp)) + pathsep
				+ "evosuite-standalone-runtime-1.0.4-SNAPSHOT";
		FileIterator fi1 = new FileIterator(Slicer.consuitedir, ".+(\\.java)$");
		Iterator<File> fitr1 = fi1.EachFileIterator();
		while (fitr1.hasNext()) {
			File f = fitr1.next();
			cmd = "javac " + f.getAbsolutePath() + " -d classes -cp " + classpath;
			cm.RunOneProcess(cmd.split(" "), false, new DisplayInfo(System.out), new DisplayInfo(System.err));
			System.out.println("Successfully compile the java file:" + f.getAbsolutePath() + ".");
		}
		SystemStreamUtil.Flush();
		
		// ============ start detecting bugs! ============
		System.out.println("============ start detecting bugs! ============");
		
		String ant_cmd = "ant";
		if (EnvironmentUtil.IsWindows())
		{
			ant_cmd = "ant.bat";
		}
		
		classpath += (pathsep + "calfuzzer.jar" + pathsep + Compiled_Classpath);
		String parent_path = new File("haha").getAbsolutePath().replace('\\', '/') + "/" + Compiled_Classpath + "/";
		FileIterator fi2 = new FileIterator(Compiled_Classpath, ".+(TestCase([0-9]+)\\.class)$");
		Iterator<File> fitr2 = fi2.EachFileIterator();
		while (fitr2.hasNext()) {
			File f = fitr2.next();
			String f_abosulate_path = f.getAbsolutePath().replace('\\', '/');
			String temp_full_name = f_abosulate_path.substring(parent_path.length());
			String full_name = temp_full_name.substring(0, temp_full_name.length() - ".class".length()).replace('/',
					'.');
			cmd = ant_cmd + " -f run.xml calfuzzer_run -Dtest_class=" + full_name + " -Dtask_type=" + task_type
					+ " -Dclass_path=" + classpath;

			DisplayInfoAndConsumeCalfuzzerResult out = new DisplayInfoAndConsumeCalfuzzerResult(System.out);
			DisplayInfoAndConsumeCalfuzzerResult err = new DisplayInfoAndConsumeCalfuzzerResult(System.err);
			cm.RunOneProcess(cmd.split(" "), false, out, err);
			List<String> result = new LinkedList<String>();
			result.add("========== " + full_name + " data_race" + " ==========");
			result.addAll(out.GetRaces());
			result.addAll(err.GetRaces());
			result.add(System.getProperty("line.separator"));

			FileUtil.AppendToFile("calfuzzer_result.1k", result);

			System.out.println("Successfully " + task_type + " in:" + full_name + ".");

			cmd = ant_cmd + " -f run.xml clean";
			cm.RunOneProcess(cmd.split(" "), false, new DisplayInfo(System.out), new DisplayInfo(System.err));
		}
		SystemStreamUtil.Flush();
		if (classes.exists()) {
			FileUtil.DeleteFolder(classes.getAbsolutePath());
		}
	}

	public String Task_type() {
		return task_type;
	}

}
