package cn.yyx.research.util;

public class CommandUtil {
	
	public static String FindProjectClassPath(String[] args)
	{
		for (int i=0;i<args.length;i++)
		{
			String one_arg = args[i];
			if (one_arg.equals("-projectCP"))
			{
				String cp = args[i+1];
				return cp;
			}
		}
		return null;
	}
	
}
