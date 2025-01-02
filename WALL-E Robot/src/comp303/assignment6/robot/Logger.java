package comp303.assignment6.robot;

import java.util.LinkedList;
import java.io.OutputStream;

public class Logger {

	private static Logger logger_instance = null;
	private static LinkedList<LogEntry> aActionLog = new LinkedList<LogEntry>();
	private static OutputStream aOutput = System.out;

// constructor is private, no need to define one

	public static Logger getInstance() {
		//Logger is a singleton, will always return the same instance, and will set it at first call
		if (logger_instance == null) {
			logger_instance = new Logger();
		}
		return logger_instance;
	}

	public void changeOutput(OutputStream pOutput) {
		aOutput = pOutput;
	}

	public String outputLog() {

		String output = "";

		try {
			// Output the logs to the outputstream
			// by default this is stdout, but can be overriden
			// using changeOutput
			for (LogEntry aEntry : aActionLog) {
				aOutput.write(aEntry.getLogString().getBytes());
				aOutput.write('\n');
			}
		} catch (Exception e) {
			System.out.println("Unable to write to the output stream!\n");
		}

		return output;

	}

	//Adds log entry to the logger
	public void addLog(LogEntry pLog) {

		if (pLog == null) {
			return;
		} else {

			aActionLog.add(pLog);

		}
		return;
	}

}
