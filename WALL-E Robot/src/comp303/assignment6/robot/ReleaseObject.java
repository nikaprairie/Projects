package comp303.assignment6.robot;

import comp303.assignment6.robot.Robot.ArmState;
import comp303.assignment6.robot.Robot.GripperState;

public class ReleaseObject extends Action  implements ActionInterface{
	
	//sets the name of the releaseObject action to "ReleaseObejct" as a default value and calls the super
	public ReleaseObject() {
		super("ReleaseObject");
	}
	
	//specific routine to execute the ReleaseObject Action
	public boolean execute(Robot pRobot) {
		
		if(pRobot.getArmState() == ArmState.EXTENDED) {
			pRobot.retractArm();
		}
		if(pRobot.getGripperState() != GripperState.OPEN) {
			pRobot.openGripper();
		}
		return true;
		
		
	}
	
	//accepts a ActionVisitor for ReleaseObject
	public void accept (ActionVisitor pVisitor) {
		pVisitor.visitRelease(this);
	}

}
