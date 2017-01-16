package cn.yyx.research.integrate;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.yyx.research.slice.Slicer;
import cn.yyx.research.util.CommandUtil;
import cn.yyx.research.util.EnvironmentUtil;
import cn.yyx.research.util.FileIterator;
import cn.yyx.research.util.FileUtil;
import cn.yyx.research.util.ResourceUtil;
import cn.yyx.research.util.SystemStreamUtil;

public class ConcatMain {

	private String Java7_Home = null;
	private String Java8_Home = null;

	private String task_type = null; // {race-analysis, deadlock-analysis,
										// atomfuzzer-analysis,
										// predictest-analysis}
	
	private boolean projectCP_exists = false;
	private int projectCP_idx = -1;
	private String required_jars = "";

	public static final String Compiled_Classpath = "classes";

	private ArrayList<String> refined_args = new ArrayList<String>();

	public ConcatMain(String[] args) {
//		for (String arg : args)
//		{
//			System.err.println("arg:" + arg);
//		}
//		System.exit(1);
		String pathsep = System.getProperty("path.separator");
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
			} else if (one_arg.startsWith("-Djar_dir=")) {
				String dir = one_arg.substring("-Djar_dir=".length());
				String here = new File("here").getAbsolutePath();
				here = here.substring(0, here.length()-"here".length());
				String full_dir = new File(dir).getAbsolutePath();
				boolean relative_path = false;
				if (full_dir.startsWith(here))
				{
					relative_path = true;
				}
				FileIterator fi = new FileIterator(dir, ".*\\.jar$");
				Iterator<File> fitr = fi.EachFileIterator();
				while (fitr.hasNext())
				{
					File f = fitr.next();
					String fpath = f.getAbsolutePath();
					if (relative_path)
					{
						fpath = fpath.substring(here.length());
					}
					fpath = fpath.replace('\\', '/');
					required_jars += (pathsep + fpath);
				}
			} else {
				if (one_arg.startsWith("-projectCP"))
				{
					projectCP_exists = true;
					projectCP_idx = refined_args.size() + 1;
				}
				refined_args.add(one_arg);
			}
		}
		if (task_type == null) {
			task_type = "race-analysis";
		}
		if (Java7_Home == null && Java8_Home != null) {
			Java7_Home = System.getenv("JAVA_HOME").replace('\\', '/');
		} else if (Java7_Home != null && Java8_Home == null) {
			Java8_Home = System.getenv("JAVA_HOME").replace('\\', '/');
		} else if (Java7_Home == null && Java8_Home == null) {
			System.err.println(
					"Error! we need only both java7 path and java8 path set through -Djava7= or -Djava8=.");
			System.exit(1);
		}
	}

	public String[] GetRefinedArgs() {
		if (!required_jars.equals(""))
		{
			if (projectCP_exists) {
				refined_args.set(projectCP_idx, refined_args.get(projectCP_idx) + required_jars);
			} else {
				refined_args.add("-projectCP");
				refined_args.add(required_jars.substring(";".length()));
			}
		}
		String[] rarr = new String[refined_args.size()];
		rarr = refined_args.toArray(rarr);
		return rarr;
	}

	public void RunOneProcess(String cmd, boolean use8, DisplayInfo out, DisplayInfo err) {
		try {
			List<String> commands = new LinkedList<String>();
			if (EnvironmentUtil.IsWindows()) {
				commands.add("cmd");
				commands.add("/c");
				String[] cmds = cmd.split(" ");
				for (int i=0;i<cmds.length;i++) {
					commands.add(cmds[i]);
				}
			} else {
				commands.add("sh");
				commands.add("-c");
				commands.add(cmd);
			}
			ProcessBuilder pb = new ProcessBuilder(commands); // "java", "-jar",
															// "Test3.jar"
			// pb.directory(new File("F:\\dist"));
			Map<String, String> map = pb.environment();
			
			if ((use8 && Java8_Home != null)) {
				EnvironmentUtil.HandleProcessEnvironment(map, Java8_Home);
			}
			if (!use8 && Java7_Home != null) {
				EnvironmentUtil.HandleProcessEnvironment(map, Java7_Home);
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
		ResourceUtil.InitialEnvironment();
		
		ConcatMain cm = new ConcatMain(args);
		String[] ref_args = cm.GetRefinedArgs();
		String task_type = cm.Task_type();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ref_args.length; i++) {
			sb.append(" " + ref_args[i]);
		}
		String cmd = "java -jar " + ResourceUtil.Evosuite_Master + " -Dassertions=false" + sb.toString();
		cm.RunOneProcess(cmd, true, new DisplayInfo(System.out), new DisplayInfo(System.err));

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
				+ ResourceUtil.Evosuite_Runtime;
		FileIterator fi1 = new FileIterator(Slicer.consuitedir, ".+(\\.java)$");
		Iterator<File> fitr1 = fi1.EachFileIterator();
		while (fitr1.hasNext()) {
			File f = fitr1.next();
			
//			cmd = "javac -version";
//			cm.RunOneProcess(cmd, false, new DisplayInfo(System.out), new DisplayInfo(System.err));
//			cmd = "java -version";
//			cm.RunOneProcess(cmd, false, new DisplayInfo(System.out), new DisplayInfo(System.err));
			
			cmd = "javac " + f.getAbsolutePath() + " -d classes -cp " + classpath;
			cm.RunOneProcess(cmd, false, new DisplayInfo(System.out), new DisplayInfo(System.err));
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
		
		classpath += (pathsep + ResourceUtil.Calfuzzer + pathsep + Compiled_Classpath);
		String classpath_ant = classpath.replace(';', ':');
		
		String one_class = null;
		Map<String, Integer> final_result_count = new TreeMap<String, Integer>();
		String parent_path = new File(Compiled_Classpath).getAbsolutePath().replace('\\', '/') + "/";
		FileIterator fi2 = new FileIterator(Compiled_Classpath, ".+(TestCase([0-9]+)\\.class)$");
		Iterator<File> fitr2 = fi2.EachFileIterator();
		while (fitr2.hasNext()) {
			File f = fitr2.next();
			String f_abosulate_path = f.getAbsolutePath().replace('\\', '/');
			String temp_full_name = f_abosulate_path.substring(parent_path.length());
			String full_name = temp_full_name.substring(0, temp_full_name.length() - ".class".length()).replace('/',
					'.');
			
			String temp_one_class = full_name.substring(0, full_name.lastIndexOf("_TestCase"));
			if (one_class == null) {
				one_class = temp_one_class;
			} else {
				if (!one_class.equals(temp_one_class))
				{
					PrintResultMap(final_result_count, one_class);
				}
			}
			
			cmd = ant_cmd + " -f " + ResourceUtil.Ant_Run + " calfuzzer_run -Dtest_class=" + full_name + " -Dtask_type=" + task_type
					+ " -Dclass_path=" + classpath_ant;

			DisplayInfoAndConsumeCalfuzzerResult out = new DisplayInfoAndConsumeCalfuzzerResult(System.out);
			DisplayInfoAndConsumeCalfuzzerResult err = new DisplayInfoAndConsumeCalfuzzerResult(System.err);
			cm.RunOneProcess(cmd, false, out, err);
			ArrayList<String> out_result = out.GetRaces();
			ArrayList<String> err_result = err.GetRaces();
			Map<String, Integer> result_count = new TreeMap<String, Integer>();
			FillResultMap(out_result, result_count);
			FillResultMap(err_result, result_count);
			FillFinalResultMap(final_result_count, result_count);
			
//			List<String> test_list = new LinkedList<String>();
//			test_list.add("============== " + "Detect race in " + full_name + " ==============");
//			test_list.addAll(out_result);
//			test_list.addAll(err_result);
//			FileUtil.AppendToFile("compare_result.1k", test_list);
			
			System.out.println("Successfully " + task_type + " in:" + full_name + ".");
		}
		if (!final_result_count.isEmpty())
		{
			if (one_class == null)
			{
				System.err.println("What the fuck! one_class is null and result_count is not null?");
				System.exit(1);
			}
			PrintResultMap(final_result_count, one_class);
		}
		SystemStreamUtil.Flush();
		cmd = ant_cmd + " -f " + ResourceUtil.Ant_Run + " clean_here";
		cm.RunOneProcess(cmd, false, new DisplayInfo(System.out), new DisplayInfo(System.err));
		if (classes.exists()) {
			FileUtil.DeleteFolder(classes.getAbsolutePath());
		}
		
		ResourceUtil.ClearEnvironment();
	}
	
	private static void PrintResultMap(Map<String, Integer> result_count, String one_class)
	{
		List<String> result = new LinkedList<String>();
		result.add("============== " + "Detect race in " + one_class + " ==============");
		Set<String> rkeys = result_count.keySet();
		Iterator<String> ritr = rkeys.iterator();
		while (ritr.hasNext())
		{
			String rkey = ritr.next();
			int count = result_count.get(rkey);
			for (int i=0;i<count;i++)
			{
				result.add(rkey);
			}
		}
		result.add(System.getProperty("line.separator"));
		FileUtil.AppendToFile("calfuzzer_result.1k", result);
		result_count.clear();
		result.clear();
	}
	
	private static void FillFinalResultMap(Map<String, Integer> final_result_count, Map<String, Integer> result_count)
	{
		if (final_result_count.isEmpty()) {
			final_result_count.putAll(result_count);
		} else {
			Set<String> rkeys = result_count.keySet();
			Iterator<String> ritr = rkeys.iterator();
			while (ritr.hasNext())
			{
				String key = ritr.next();
				int count = result_count.get(key);
				Integer final_count = final_result_count.get(key);
				if (final_count == null) {
					final_count = count;
				} else {
					if (final_count < count)
					{
						final_count = count;
					}
				}
				final_result_count.put(key, final_count);
			}
		}
	}
	
	private static void FillResultMap(List<String> result, Map<String, Integer> result_count)
	{
		Iterator<String> ritr = result.iterator();
		while (ritr.hasNext())
		{
			String one = ritr.next();
			Integer rs = result_count.get(one);
			if (rs == null)
			{
				rs = 0;
			}
			rs++;
			result_count.put(one, rs);
		}
	}

	public String Task_type() {
		return task_type;
	}

	public String GetRequiredJars() {
		return required_jars;
	}
	
}
