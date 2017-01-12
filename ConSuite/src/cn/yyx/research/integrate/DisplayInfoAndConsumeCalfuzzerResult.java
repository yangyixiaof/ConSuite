package cn.yyx.research.integrate;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class DisplayInfoAndConsumeCalfuzzerResult extends DisplayInfo {
	
	boolean start_recore = false;
	private ArrayList<String> race_list = new ArrayList<String>();
	
	public DisplayInfoAndConsumeCalfuzzerResult(PrintStream out) {
		super(out);
	}

	public void setIs(InputStream is) {
		this.is = is;
	}
	
	@Override
	public void HandleInformation(String oneline) {
		if (start_recore)
		{
			oneline = oneline.trim();
			if ((oneline.startsWith("[java] Data race between") && (!oneline.startsWith("[java] Data race between sum: "))) || (oneline.startsWith("[java] Lock race between") && (!oneline.startsWith("[java] Lock race between sum: ")))) {
				race_list.add(oneline);
			}
		}
		if (oneline.equals("analysis-once:"))
		{
			start_recore = true;
		}
		if (oneline.startsWith("[stopwatch] [timer:"))
		{
			start_recore = false;
		}
	}

	public ArrayList<String> GetRaces() {
		return race_list;
	}
	
}
