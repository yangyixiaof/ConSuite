package cn.yyx.research.util;

import java.io.File;

public class TestUtil {
	
	public static void main(String[] args) {
		System.out.println(System.getenv("JAVA_HOME"));
		System.out.println(new File("haha").getAbsolutePath().replace('\\', '/'));
	}
	
}
