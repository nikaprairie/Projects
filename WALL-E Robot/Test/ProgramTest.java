import comp303.assignment6.robot.*;

import comp303.assignment6.robot.Robot.ArmState;
import comp303.assignment6.robot.Robot.GripperState;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


import comp303.assignment6.robot.CompactObject;
import comp303.assignment6.robot.Direction;
import comp303.assignment6.robot.EmptyCompactor;
import comp303.assignment6.robot.GrabObject;
import comp303.assignment6.robot.MoveForward;
import comp303.assignment6.robot.Program;
import comp303.assignment6.robot.ReleaseObject;
import comp303.assignment6.robot.Rotate90;

class ProgramTest {

	@Test
	void test() {
		
		TestRobot myRobot = new TestRobot();
		Program program = new Program("myProgram");
		
		Action compactObject = new CompactObject();
		Action emptyCompactor = new EmptyCompactor();
		Action grabObject = new GrabObject();
		Action moveForward = new MoveForward(10.0);
		Action releaseObject = new ReleaseObject();
		Action rotate90 = new Rotate90(Direction.LEFT);
		
		program.addAction(rotate90);
		program.addAction(grabObject);
		program.addAction(releaseObject);
		program.addAction(moveForward);
		program.addAction(grabObject);
		program.addAction(compactObject);
		program.addAction(emptyCompactor);
		program.removeAction(rotate90);
		program.removeAction(emptyCompactor);
		
		program.run(myRobot);
		System.out.println(program.getName());
		
		assertTrue(myRobot.compactedItems == 1);
		assertTrue(myRobot.gripperState.equals(GripperState.OPEN));
		assertTrue(myRobot.armState.equals(ArmState.RETRACTED));
		
	}

}
