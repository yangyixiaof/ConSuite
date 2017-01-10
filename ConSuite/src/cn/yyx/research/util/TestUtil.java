package cn.yyx.research.util;

import java.io.File;

public class TestUtil {
	
	public static void main(String[] args) {
		System.out.println(new File("haha").getAbsolutePath().replace('\\', '/'));
	}
	
}
