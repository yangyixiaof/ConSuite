package cn.yyx.research.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class TestUtil {
	
	public static void main(String[] args) {
		System.out.println(System.getenv("JAVA_HOME"));
		System.out.println(new File("haha").getAbsolutePath().replace('\\', '/'));
		System.out.println("abc.java".matches(".+(\\.java)$"));
		System.out.println("abc.class".matches(".+(TestCase([0-9]+)\\.class)$"));
		System.out.println("TestCase12.class".matches(".+(TestCase([0-9]+)\\.class)$"));
		System.out.println("TestCase12$2.class".matches(".+(TestCase([0-9]+)\\.class)$"));
		System.out.println("HaHaTestCase12.class".matches(".+(TestCase([0-9]+)\\.class)$"));
		
//		File heihei = new File(".kj");
//		List<String> res = new LinkedList<String>();
//		res.add("iu");
//		res.add("op");
//		FileUtil.AppendToFile(heihei.getAbsolutePath(), res);
		
		try {
			String str = "add your string content";
			InputStream inputStream = new ByteArrayInputStream(str.getBytes());
			FileUtil.ReadFromStreamAndWriteToFile(inputStream, "D:/ddd.ddd");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
