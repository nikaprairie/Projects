import comp303.assignment6.robot.*;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class CompactCalcVisitorTest {

	@Test
	void test() {
		CompactCalcVisitor visitor = new CompactCalcVisitor();
		int numObjects = 0;
		TestRobot myRobot = new TestRobot();
		
		ActionInterface decoratedMvFwd = new ForceBatteryRechargeDecorator (new MoveForward(4));
		
		Action compactObject = new CompactObject();
		ActionInterface decoratedCompact = new ForceBatteryRechargeDecorator (new CompactObject());
		
		Action emptyCompactor = new EmptyCompactor();
		Action grabObject = new GrabObject();
		Action moveForward = new MoveForward(10.0);
		Action releaseObject = new ReleaseObject();
		Action rotate90 = new Rotate90(Direction.LEFT);
		
		
		ComplexAction customAction = new ComplexAction("myCustomAction");
		customAction.addAction(moveForward);
		customAction.addAction(grabObject);
		customAction.addAction(decoratedCompact);
		customAction.addAction(moveForward);
		customAction.addAction(grabObject);
		customAction.addAction(compactObject);
		customAction.addAction(grabObject);
		customAction.addAction(releaseObject);
		customAction.addAction(moveForward);
		customAction.addAction(decoratedMvFwd);
		
		customAction.executeProtocolAction(myRobot);
		
		customAction.accept(visitor);
		
		numObjects = visitor.numberOfCompactedObjects();
		assertTrue(numObjects == 2);
		System.out.println("Number of objects compacted " + numObjects );
		
		
		
		moveForward.executeProtocolAction(myRobot);
		decoratedMvFwd.execute(myRobot);
		
		
		moveForward.accept(visitor);
		decoratedMvFwd.accept(visitor);
		decoratedCompact.accept(visitor);
		compactObject.accept(visitor);
		emptyCompactor.accept(visitor);
		grabObject.accept(visitor);
		releaseObject.accept(visitor);
		rotate90.accept(visitor);
		
		numObjects = visitor.numberOfCompactedObjects();
		
		
		assertTrue(numObjects == 4);
		System.out.println("Number of objects compacted " + numObjects );
		
		
		Program program = new Program("myProgram");
		
		program.addAction(rotate90);
		program.addAction(moveForward);
		program.addAction(decoratedMvFwd);
		program.addAction(grabObject);
		program.addAction(decoratedCompact);
		program.addAction(releaseObject);
		program.addAction(moveForward);
		program.addAction(grabObject);
		program.addAction(compactObject);
		program.addAction(emptyCompactor);
		
		program.accept(visitor);

		
		numObjects = visitor.numberOfCompactedObjects();
		
		assertTrue(numObjects == 6);
		visitor.resetCompactedObjects();
		numObjects = visitor.numberOfCompactedObjects();
		assertTrue(numObjects == 0);
		System.out.println("Number of objects compacted " + numObjects );

	}

}
