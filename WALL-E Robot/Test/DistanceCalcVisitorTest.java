import comp303.assignment6.robot.*;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import comp303.assignment6.robot.ActionInterface;
import comp303.assignment6.robot.CompactObject;
import comp303.assignment6.robot.Direction;
import comp303.assignment6.robot.EmptyCompactor;
import comp303.assignment6.robot.GrabObject;
import comp303.assignment6.robot.MoveForward;
import comp303.assignment6.robot.Program;
import comp303.assignment6.robot.ReleaseObject;
import comp303.assignment6.robot.Rotate90;
import comp303.assignment6.robot.DistanceCalcVisitor;
import comp303.assignment6.robot.ForceBatteryRechargeDecorator;


class DistanceCalcVisitorTest {

	@Test
	void test() {
		DistanceCalcVisitor visitor = new DistanceCalcVisitor();
		double distance = 0;
		TestRobot myRobot = new TestRobot();
		
		ActionInterface decoratedMvFwd = new ForceBatteryRechargeDecorator (new MoveForward(4));
		
		Action compactObject = new CompactObject();
		Action emptyCompactor = new EmptyCompactor();
		Action grabObject = new GrabObject();
		Action moveForward = new MoveForward(10.0);
		Action releaseObject = new ReleaseObject();
		Action rotate90 = new Rotate90(Direction.LEFT);
		
		
		ComplexAction customAction = new ComplexAction("myCustomAction");
		customAction.addAction(grabObject);
		customAction.addAction(releaseObject);
		customAction.addAction(moveForward);
		customAction.addAction(decoratedMvFwd);
		
		customAction.executeProtocolAction(myRobot);
		
		customAction.accept(visitor);
		
		
		distance = visitor.distanceTravelled();
		assertTrue(distance == 14.0);
		System.out.println("Distance travelled " + distance );
		
		
		moveForward.executeProtocolAction(myRobot);
		decoratedMvFwd.execute(myRobot);
				
		moveForward.accept(visitor);
		decoratedMvFwd.accept(visitor);		
		compactObject.accept(visitor);
		emptyCompactor.accept(visitor);
		grabObject.accept(visitor);
		releaseObject.accept(visitor);
		rotate90.accept(visitor);
		
		distance = visitor.distanceTravelled();
		
		assertTrue(distance == 28.0);
		System.out.println("Distance travelled " + distance );
		
		Program program = new Program("myProgram");
		
		program.addAction(rotate90);
		program.addAction(moveForward);
		program.addAction(grabObject);
		program.addAction(compactObject);
		program.addAction(decoratedMvFwd);
		program.addAction(releaseObject);
		program.addAction(moveForward);
		program.addAction(grabObject);
		program.addAction(compactObject);
		program.addAction(emptyCompactor);
		
		program.accept(visitor);
		
		distance = visitor.distanceTravelled();
		
		assertTrue(distance == 52.0);
		visitor.resetDistance();
		distance = visitor.distanceTravelled();
		assertTrue(distance == 0.0);
		System.out.println("Distance travelled " + distance );
	}

}
