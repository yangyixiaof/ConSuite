package cn.yyx.research.integrate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import cn.yyx.research.util.SystemStreamUtil;

public class DisplayInfo implements Runnable {
	
	public static final int MaxCount = 25;
	
	protected InputStream is = null;
	protected PrintStream ps = null;
	
	public DisplayInfo(PrintStream out) {
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
				HandleInformation(oneline);
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
		// System.out.println("Thread " + ps.getClass() + " Over!");
		SystemStreamUtil.Flush();
	}
	
	public void setIs(InputStream is) {
		this.is = is;
	}
	
	public void HandleInformation(String oneline)
	{
		// do nothing.
	}
	
}
