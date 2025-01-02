package comp303.assignment6.robot;

import comp303.assignment6.robot.Robot.GripperState;

public class CompactObject extends Action implements ActionInterface{

	//sets the name of the compactObject action to "CompactObject" as a default value and calls the super
	public CompactObject() {
		super("CompactObject");
	}
	
	//specific routine to execute the CompactObject Action
	public boolean execute(Robot pRobot) {
		
		if(pRobot.getGripperState() == GripperState.HOLDING_OBJECT && pRobot.getCompactorLevel() < 10) {
			
			pRobot.compact();
			return true;
			
		}
		
		return false;
	}
	
	//accepts a ActionVisitor for CompactObject, keep track of compactedObejcts
	public void accept (ActionVisitor pVisitor) {
		pVisitor.visitCompactObject(this);
	}
	
}
