package comp303.assignment6.robot;

import comp303.assignment6.robot.Robot.ArmState;

public class Rotate90 extends Action implements ActionInterface{
	
	private Direction aDirection;
	
	//sets the name of the rotate90 action to "Rotate90" as a default value and calls the super
	public Rotate90(Direction pDirection) {
		super("Rotate90");
		aDirection = pDirection;
	}
	
	//specific routine to execute the Rotate90 Action
	public boolean execute(Robot pRobot) {
		
		if(pRobot.getArmState() == ArmState.EXTENDED) {
			pRobot.retractArm();
		}
		if(aDirection == Direction.LEFT) {
			pRobot.turnRobot(-90);
		}else {
			pRobot.turnRobot(90);
		}
		return true;
	}
	
	//accepts a ActionVisitor for Rotate90
	public void accept (ActionVisitor pVisitor) {
		pVisitor.visitRotate90(this);
	}
}


