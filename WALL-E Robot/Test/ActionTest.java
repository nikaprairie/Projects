



import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import comp303.assignment6.robot.*;
import comp303.assignment6.robot.Robot.ArmState;
import comp303.assignment6.robot.Robot.GripperState;

import java.io.FileOutputStream;
import java.lang.reflect.Field;

class ActionTest {

	@Test
	 public void testGetActionName() {
		
		Action compactObject = new CompactObject();
		Action emptyCompactor = new EmptyCompactor();
		Action grabObject = new GrabObject();
		Action moveForward = new MoveForward(10.0);
		Action releaseObject = new ReleaseObject();
		Action rotate90 = new Rotate90(Direction.LEFT);
		
		assertEquals(compactObject.getActionName(), "CompactObject");
		assertEquals(emptyCompactor.getActionName(), "EmptyCompactor");
		assertEquals(grabObject.getActionName(), "GrabObject");
		assertEquals(moveForward.getActionName(), "MoveForward");
		assertEquals(releaseObject.getActionName(), "ReleaseObject");
		assertEquals(rotate90.getActionName(), "Rotate90");
		
		
	}

	@Test
	void testSetActionName() {
		
		Action compactObject = new CompactObject();
		Action emptyCompactor = new EmptyCompactor();
		Action grabObject = new GrabObject();
		Action moveForward = new MoveForward(10.0);
		Action releaseObject = new ReleaseObject();
		Action rotate90 = new Rotate90(Direction.LEFT);
		
		compactObject.setActionName("RobotCompactObject");
		emptyCompactor.setActionName("RobotEmptyCompactor");
		grabObject.setActionName("RobotGrabObject");
		moveForward.setActionName("RobotMoveForward");
		releaseObject.setActionName("RobotReleaseObject");
		rotate90.setActionName("RobotRotate90");
		
		assertEquals(compactObject.getActionName(), "RobotCompactObject");
		assertEquals(emptyCompactor.getActionName(), "RobotEmptyCompactor");
		assertEquals(grabObject.getActionName(), "RobotGrabObject");
		assertEquals(moveForward.getActionName(), "RobotMoveForward");
		assertEquals(releaseObject.getActionName(), "RobotReleaseObject");
		assertEquals(rotate90.getActionName(), "RobotRotate90");
		
		
	}

	@Test
	void testExecuteProtocolAction() {
		Logger myLogger = Logger.getInstance();
		TestRobot myRobot = new TestRobot();
		FileOutputStream outputFile;
		try {
			outputFile = new FileOutputStream("logoutput.txt");
				
		} 
		catch (Exception e) {
			fail("Unexpected exception");
			return;
		}

		
		Action compactObject = new CompactObject();
		Action emptyCompactor = new EmptyCompactor();
		Action grabObject = new GrabObject();
		Action moveForward = new MoveForward(10.0);
		Action releaseObject = new ReleaseObject();
		Action rotate90Left = new Rotate90(Direction.LEFT);
		Action rotate90Right = new Rotate90(Direction.RIGHT);
		
		grabObject.executeProtocolAction(myRobot);//battery is at 11 before this action is executed 8 after
		int batteryDiff = 3;
		int battery = 11 - batteryDiff;
		assertEquals(myRobot.getBatteryCharge(), battery);
		releaseObject.executeProtocolAction(myRobot); // battery level at 5 now
		assertTrue(!compactObject.executeProtocolAction(myRobot)); // battery level at 2 now
		moveForward.executeProtocolAction(myRobot); // should increase battery  back
		assertEquals(myRobot.charge, 8);
		
		
		myRobot.extendArm();
		moveForward.executeProtocolAction(myRobot);
		assertTrue(myRobot.getArmState().equals(ArmState.RETRACTED));
		
		myRobot.extendArm();
		myRobot.closeGripper();
		releaseObject.executeProtocolAction(myRobot);
		assertTrue(myRobot.getArmState().equals(ArmState.RETRACTED));
		assertTrue(myRobot.getGripperState().equals(GripperState.OPEN));
		myRobot.extendArm();
		grabObject.executeProtocolAction(myRobot);
		releaseObject.executeProtocolAction(myRobot);
		assertTrue(myRobot.getArmState().equals(ArmState.RETRACTED));
		assertTrue(myRobot.getGripperState().equals(GripperState.OPEN));
		
		grabObject.executeProtocolAction(myRobot);
		grabObject.executeProtocolAction(myRobot);
		releaseObject.executeProtocolAction(myRobot);
		myRobot.extendArm();
		grabObject.executeProtocolAction(myRobot);
		myRobot.openGripper();
		grabObject.executeProtocolAction(myRobot);
		assertTrue(myRobot.getArmState().equals(ArmState.RETRACTED));
		assertTrue(myRobot.getGripperState().equals(GripperState.HOLDING_OBJECT));
		/// New
		myRobot.armState = ArmState.EXTENDED;
		myRobot.gripperState = GripperState.EMPTY;
		grabObject.executeProtocolAction(myRobot);
		assertTrue(myRobot.getArmState().equals(ArmState.RETRACTED));
		assertTrue(myRobot.getGripperState().equals(GripperState.HOLDING_OBJECT));
				
		//Full Coverage on CompactObject.java
		myRobot.gripperState = GripperState.HOLDING_OBJECT;
		myRobot.compactedItems = 5;
		compactObject.executeProtocolAction(myRobot);
		assertTrue(myRobot.getGripperState().equals(GripperState.OPEN));
		
		myRobot.gripperState = GripperState.HOLDING_OBJECT;
		myRobot.compactedItems = 11;
		compactObject.executeProtocolAction(myRobot);
		assertTrue(myRobot.getGripperState().equals(GripperState.HOLDING_OBJECT));
		
		myRobot.gripperState = GripperState.EMPTY;
		myRobot.compactedItems = 11;
		compactObject.executeProtocolAction(myRobot);
		assertTrue(!myRobot.getGripperState().equals(GripperState.HOLDING_OBJECT));
		
		myRobot.gripperState = GripperState.EMPTY;
		myRobot.compactedItems = 7;
		compactObject.executeProtocolAction(myRobot);
		assertTrue(!myRobot.getGripperState().equals(GripperState.HOLDING_OBJECT));
		
		
		//Full Coverage on EmptyCompactor.java
		myRobot.compactedItems = 0;
		myRobot.armState = ArmState.EXTENDED;
		emptyCompactor.executeProtocolAction(myRobot);
		assertTrue(myRobot.getArmState().equals(ArmState.EXTENDED));
		
		myRobot.compactedItems = 4;
		myRobot.armState = ArmState.RETRACTED;
		emptyCompactor.executeProtocolAction(myRobot);
		assertTrue(myRobot.getArmState().equals(ArmState.RETRACTED));
		
		//Full Coverage on Rotate90.java
		myRobot.armState = ArmState.EXTENDED;
		rotate90Left.executeProtocolAction(myRobot);
		assertTrue(myRobot.getArmState().equals(ArmState.RETRACTED));
		
		myRobot.armState = ArmState.RETRACTED;
		rotate90Right.executeProtocolAction(myRobot);
		assertTrue(myRobot.getArmState().equals(ArmState.RETRACTED));
		
		//Full coverage on ActionDecorator.java and ForceBatteryRechargeDecorator.java
		ForceBatteryRechargeDecorator forceCharge = new ForceBatteryRechargeDecorator(rotate90Right);
		forceCharge.execute(myRobot);
		assertTrue(myRobot.charge == 11);
		
		//Full Coverage on CustomAction.java
		myRobot.compactedItems = 0;
		ComplexAction customAction = new ComplexAction("customAction");
		customAction.addAction(grabObject);
		customAction.addAction(compactObject);
		customAction.removeAction(null);
		customAction.addAction(null);
	
	//	customAction.removeAction(null);
		customAction.removeAction(releaseObject);
		customAction.addAction(releaseObject);
		customAction.removeAction(releaseObject);
		customAction.executeProtocolAction(myRobot);
		
		assertEquals(myRobot.compactedItems, 1);
		
		
		myRobot.armState = ArmState.RETRACTED;
		myRobot.gripperState = GripperState.EMPTY;
		ComplexAction testActions = new ComplexAction("test");
		testActions.addAction(rotate90Right);
		testActions.addAction(releaseObject);
		testActions.addAction(emptyCompactor);
		testActions.addAction(emptyCompactor);
		testActions.addAction(emptyCompactor);
		
		testActions.executeProtocolAction(myRobot);
				
		
		myLogger.outputLog();
		myLogger.changeOutput(outputFile);
		myLogger.outputLog();
		
	}

	@Test
	void testWallEWithActions() {
		//Test Action with WallE easily by setting the private members with reflection
		try {
	//Object myRobot = new WallE();
		WallE myRobot = new WallE();
		WallE newRobot = new WallE();
		Action compactObject = new CompactObject();
		Action emptyCompactor = new EmptyCompactor();
		Action grabObject = new GrabObject();
		Action moveForward = new MoveForward(10.0);
		Action releaseObject = new ReleaseObject();
		Action rotate90 = new Rotate90(Direction.LEFT);
		Field gripperState = myRobot.getClass().getDeclaredField("gripperState");
		Field armState = myRobot.getClass().getDeclaredField("armState");
		Field charge = myRobot.getClass().getDeclaredField("charge");
		//Field[] fields = myRobot.getClass().getDeclaredFields();
		//List<String> actualFiledNames = getFieldNames(fields);
		gripperState.setAccessible(true);
		armState.setAccessible(true);
		charge.setAccessible(true);
		ComplexAction customAction = new ComplexAction("myCustomAction");
		customAction.addAction(moveForward);
		customAction.addAction(rotate90);
		customAction.addAction(grabObject);
		customAction.addAction(compactObject);
		customAction.addAction(emptyCompactor);
		customAction.executeProtocolAction(newRobot);
		gripperState.set(newRobot, GripperState.HOLDING_OBJECT);
		releaseObject.executeProtocolAction(newRobot);
		gripperState.set(newRobot, GripperState.HOLDING_OBJECT);
		//System.out.println(newRobot.getGripperState());
		assertTrue(newRobot.getGripperState().equals(GripperState.HOLDING_OBJECT));
		newRobot.rechargeBattery();
		armState.set(newRobot, ArmState.RETRACTED);
		gripperState.set(newRobot, GripperState.OPEN);
		newRobot.closeGripper();
		assertTrue(newRobot.getGripperState().equals(GripperState.EMPTY));
		
		} catch (Exception E ) {
			System.out.println("It failed!!!");
		}
	}

}
