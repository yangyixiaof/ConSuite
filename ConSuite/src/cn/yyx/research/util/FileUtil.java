package cn.yyx.research.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class FileUtil {

	public static void AppendToFile(String filepath, List<String> contents) {
		File f = new File(filepath);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			Iterator<String> itr = contents.iterator();
			while (itr.hasNext()) {
				String oneline = itr.next();
				bw.write(oneline);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void ReadFromStreamAndWriteToFile(InputStream is, String filename) {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			File dst = new File(filename);
			if (dst.exists())
			{
				dst.delete();
				dst.createNewFile();
			}
			in = new BufferedInputStream(is);
			out = new BufferedOutputStream(new FileOutputStream(dst));

			byte[] b = new byte[1024];
			int num_bytes = 0;
			while ((num_bytes = in.read(b)) >= 0) {
				out.write(b, 0, num_bytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
			{
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null)
			{
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (!file.exists()) {
			return flag;
		} else {
			if (file.isFile()) {
				return DeleteFile(sPath);
			} else {
				return DeleteDirectory(sPath);
			}
		}
	}

	public static boolean DeleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static boolean DeleteDirectory(String sPath) {
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = DeleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = DeleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

}
