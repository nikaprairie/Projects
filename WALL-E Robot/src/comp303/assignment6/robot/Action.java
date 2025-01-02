package comp303.assignment6.robot;

public abstract class Action implements ActionInterface {

	private String aActionName; // Each Action has a specific Action Name that way multiple actions of the same type can be distinct

	//Constructor for Action, sets the aActionName
	public Action(String pName) {
		aActionName = pName;
	}

	//gets aActionName String since the field is private
	public String getActionName() {
		return aActionName;
	}

	//set aActionName if the user wishes to change the fieldName
	public void setActionName(String pName) {
		aActionName = pName;
	}

	//Execute protocol which checks the battery and updates if necessary then logs the action, 
	//calls subroutine execute on the appropriate action with the Robot given
	public boolean executeProtocolAction(Robot pRobot) {
		Logger aLogger = Logger.getInstance();

		if(pRobot.getBatteryCharge() <= 5) {
			pRobot.rechargeBattery();
		}
		boolean execute = this.execute(pRobot);
		if (execute) {

			aLogger.addLog(new ActionEntry( this.aActionName, pRobot.getBatteryCharge()));

			pRobot.updateBatteryLevel();
		}
		return execute;

	}
	
	//abstract method that each BasicAction inherits and implements based on what that Basic Action does
	public abstract boolean execute(Robot pRobot);
	
	//abstract method that each BasicAction inherits and implements 
	public abstract void accept (ActionVisitor pVisitor);
	

}
