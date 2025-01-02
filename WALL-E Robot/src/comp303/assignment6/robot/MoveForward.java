package comp303.assignment6.robot;

import comp303.assignment6.robot.Robot.ArmState;

public class MoveForward extends Action implements ActionInterface {
	
	private final double aDistance;
	
	//sets the name of the MoveForward action to "MoveForward" as a default value and calls the super
	public MoveForward(double pDistance) {
		super("MoveForward");
		aDistance = pDistance;
	}
	
	//specific routine to execute the MoveForward Action
	public boolean execute(Robot pRobot) {
		
		if(pRobot.getArmState() == ArmState.EXTENDED) {
			pRobot.retractArm();
		}
		pRobot.moveRobot(aDistance);
		return true;
	}
	
	//returns the distance which the robot is to move forward
	public double getDistance()
	{
		return aDistance;
	}
	
	//accepts a ActionVisitor for MoveForward, keep track of distance moved
	public void accept (ActionVisitor pVisitor) {
		pVisitor.visitMoveForward(this);
	}

}
