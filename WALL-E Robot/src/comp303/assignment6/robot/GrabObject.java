package comp303.assignment6.robot;
//import java.sql.Timestamp;
import comp303.assignment6.robot.Robot.ArmState;


public class GrabObject extends Action implements ActionInterface{

	//sets the name of the grabObject action to "GrabObject" as a default value and calls the super
	public GrabObject() {
		super("GrabObject");
	}

	//specific routine to execute the GrabObject Action
	public boolean execute(Robot pRobot) {
		switch (pRobot.getGripperState()) {
		case HOLDING_OBJECT:
			return false;
		case EMPTY:
			if(pRobot.getArmState() == ArmState.EXTENDED) {
				pRobot.retractArm();
			}
			pRobot.openGripper();

			pRobot.extendArm();
			pRobot.closeGripper();
			pRobot.retractArm();
			break;
		case OPEN:
			if (pRobot.getArmState() == ArmState.RETRACTED) {
				pRobot.extendArm();
			}
			pRobot.closeGripper();
			pRobot.retractArm();
			break;
		default: 
			return false;

		}
		return true;
	}

	//accepts a ActionVisitor for GrabObject
	public void accept (ActionVisitor pVisitor) {
		pVisitor.visitGrabObject(this);
	}




}
