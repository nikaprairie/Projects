

import org.junit.jupiter.api.Test;

import comp303.assignment6.robot.Logger;

class LoggerTest {

	@Test
	void test() {
		Logger myLogger = Logger.getInstance();
		
		myLogger.changeOutput(null);
		myLogger.outputLog();
		myLogger.addLog(null);

	}

}
