package log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.System;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * A simple little logger, if debug is set to true the code will start printing information about the game in the console.
 * Debug can be tolled using the static access method debug.
 * @author s164166
 */
public class Log {

	public static boolean debug = false;
	
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
			String output ="[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "][" + input + "][" + methodName + "] " + message;
			System.out.println(output);
			writeToDebugFile(output);
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
			String output ="[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "][" + input + "][" + methodName + "] " + message;
			System.err.println(output);
			writeToDebugFile(output);
		}
	}
	
	public static void writeToDebugFile(String input)
	{
		try(FileWriter fw = new FileWriter("debugFile.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(input);
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}

	}
}
