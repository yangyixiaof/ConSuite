package cn.yyx.research.integrate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class DisplayInfo implements Runnable{
	
	public static final int MaxCount = 25;
	
	InputStream is = null;
	PrintStream ps = null;
	
	public DisplayInfo(InputStream is, PrintStream out) {
		this.is = is;
		this.ps = out;
	}

	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String oneline = null;
		try {
			int count = 0;
			while ((oneline = br.readLine()) != null)
			{
				oneline = oneline.trim();
				if (oneline.startsWith("[Progress:")) {
					count++;
					if (count > MaxCount)
					{
						ps.println(oneline);
						count = 0;
					}
				} else {
					ps.println(oneline);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Thread " + ps.getClass() + " Over!");
		System.out.flush();
		System.err.flush();
	}
	
}
