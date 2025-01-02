import org.junit.platform.runner.JUnitPlatform;

import org.junit.runner.RunWith;
import org.junit.platform.suite.api.SelectClasses;


@RunWith(JUnitPlatform.class)
@SelectClasses({ActionTest.class, 
	LoggerTest.class, 
	DistanceCalcVisitorTest.class, 
	CompactCalcVisitorTest.class, 
	CalculatingRobotTest.class,
	ProgramTest.class})

public class AllTests {

}
