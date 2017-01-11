package cn.yyx.research.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class FileUtil {
	
	public static void AppendToFile(String filepath, List<String> contents)
	{
		File f = new File(filepath);
		if (!f.exists())
		{
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			Iterator<String> itr = contents.iterator();
			while (itr.hasNext())
			{
				String oneline = itr.next();
				bw.write(oneline);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
