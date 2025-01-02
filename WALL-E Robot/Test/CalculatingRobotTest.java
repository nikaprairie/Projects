import comp303.assignment6.robot.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CalculatingRobotTest {

	@Test
	void test() {
	
		CalculatingRobot myRobot = new CalculatingRobot();
		
		ActionInterface decoratedMvFwd = new ForceBatteryRechargeDecorator (new MoveForward(4));
		
		Action compactObject = new CompactObject();
		Action emptyCompactor = new EmptyCompactor();
		Action grabObject = new GrabObject();
		Action moveForward = new MoveForward(10.0);
		Action rotate90 = new Rotate90(Direction.LEFT);
		Action releaseObject2 = new ReleaseObject();
		Action grabObject2 = new GrabObject();
		
		ComplexAction customAction = new ComplexAction("myCustomAction");
		customAction.addAction(grabObject);
		customAction.addAction(releaseObject2);
		customAction.addAction(rotate90);
		customAction.addAction(grabObject2);
		customAction.addAction(compactObject);
		customAction.addAction(moveForward);
		customAction.addAction(emptyCompactor);
		customAction.addAction(decoratedMvFwd);
		customAction.executeProtocolAction(myRobot);
		moveForward.executeProtocolAction(myRobot);
		grabObject.executeProtocolAction(myRobot);
		compactObject.executeProtocolAction(myRobot);
		myRobot.closeGripper();
		double distance = myRobot.getTotalDistanceTravelled();
		int compactedItems = myRobot.getTotalCompactedItems();
		
		assertTrue(distance == 24.0);
		assertTrue(compactedItems == 2);
	}

}
