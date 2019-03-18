package log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.System;

/**
 * A simple little logger, if debug is set to true the code will start printing information about the game in the console.
 * Debug can be tolled using the static access method debug.
 * @author s164166
 */
public class Log {

	public static boolean debug = true;
	
	public static void debug(boolean debugValue)
	{
		debug = debugValue;
	}
	
	public static void log(String message)
	{
		if (debug)
		{
			String input = CallingClass.INSTANCE.getCallingClasses()[2].getName(); 
			String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			input = input.substring(input.indexOf(".")+1);
			System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "][" + input + "][" + methodName + "] " + message);
		}
	}
	
	public static void important(String message)
	{
		if (debug)
		{
			//System.out.println("[" + new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").format(new Date()) + "]: " + message);
			String input = CallingClass.INSTANCE.getCallingClasses()[2].getName();
			String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			input = input.substring(input.indexOf(".")+1);
			System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "][" + input + "][" + methodName + "] " + message);
		}
	}
}
