package cn.yyx.research.util;

import java.io.File;

public class TestUtil {
	
	public static void main(String[] args) {
		System.out.println(System.getenv("JAVA_HOME"));
		System.out.println(new File("haha").getAbsolutePath().replace('\\', '/'));
		System.out.println("abc.java".matches(".+(\\.java)$"));
		System.out.println("abc.class".matches(".+(TestCase([0-9]+)\\.class)$"));
		System.out.println("TestCase12.class".matches(".+(TestCase([0-9]+)\\.class)$"));
		System.out.println("TestCase12$2.class".matches(".+(TestCase([0-9]+)\\.class)$"));
		System.out.println("HaHaTestCase12.class".matches(".+(TestCase([0-9]+)\\.class)$"));
	}
	
}
