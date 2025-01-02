package comp303.assignment6.robot;

import java.util.Random;

public class TestRobot implements Robot {
	
	public final Random random = new Random();
	public int charge = 11;
	public GripperState gripperState = GripperState.EMPTY;
	public ArmState armState = ArmState.RETRACTED;
	public int compactedItems = 0;
	

	@Override
	public void turnRobot(double pDegrees) {
		assert armState == ArmState.RETRACTED;
		System.out.println("Turn");
	}

	@Override
	public void moveRobot(double pDistance) {
		assert armState == ArmState.RETRACTED;
		System.out.println("Forward");
	}

	@Override
	public void openGripper() {
		assert gripperState != GripperState.OPEN && armState == ArmState.RETRACTED;
		System.out.println("Open gripper");
		gripperState = GripperState.OPEN;
	}

	@Override
	public void closeGripper() {
		assert gripperState == GripperState.OPEN;
		System.out.println("Close gripper");
		switch (armState) {
		case EXTENDED:
			gripperState = GripperState.HOLDING_OBJECT;
			break;
		case RETRACTED:
			gripperState = GripperState.EMPTY;
			break;
		default:
			assert false;
			break;
		}
	}

	@Override
	public GripperState getGripperState() {
		return gripperState;
	}

	@Override
	public void extendArm() {
		assert armState == ArmState.RETRACTED;
		System.out.println("Extend arm");
		armState = ArmState.EXTENDED;
	}

	@Override
	public void retractArm() {
		assert armState == ArmState.EXTENDED;
		System.out.println("Retract arm");
		armState = ArmState.RETRACTED;
	}

	@Override
	public ArmState getArmState() {
		return armState;
	}

	@Override
	public void compact() {
		assert compactedItems < 10 && gripperState == GripperState.HOLDING_OBJECT;
		System.out.println("Compact");
		compactedItems++;
		gripperState = GripperState.OPEN;
	}

	@Override
	public void emptyCompactor() {
		assert compactedItems > 0;
		System.out.println("Empty compactor");
		compactedItems = 0;
	}

	@Override
	public int getCompactorLevel() {
		return compactedItems;
	}

	@Override
	public void rechargeBattery() {
		System.out.println("Recharge");
		charge = 11;
	}

	@Override
	public void updateBatteryLevel() {
		charge -= 3;
	}

	@Override
	public int getBatteryCharge() {
		return charge;
	}

}
