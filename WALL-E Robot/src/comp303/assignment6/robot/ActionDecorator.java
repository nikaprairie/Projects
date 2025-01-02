package comp303.assignment6.robot;

//import comp303.assignment6.robot.ActionEntry;

public abstract class ActionDecorator implements ActionInterface {
	
	protected ActionInterface aDecoratedAction; //the Action which is to be decorated, this can be any basic or customAtion

	
	//Takes in an Action which is to be decorated
	public ActionDecorator(ActionInterface pDecoratedAction) {
		aDecoratedAction = pDecoratedAction;
	}
	
	//Accept Visitor on ActionDecorator
	public void accept (ActionVisitor pVisitor) {
		
		pVisitor.visitActionDecorator(this);
		aDecoratedAction.accept(pVisitor);
	}

	//Abstract method execute for each BasicAction
	public abstract boolean execute(Robot pRobot); 
	
	//Execute the protocol so it checks the battery, re-charge if necessary, executes the action then updates the battery
	public boolean executeProtocolAction(Robot pRobot) {
		Logger aLogger = Logger.getInstance(); 

		if(pRobot.getBatteryCharge() <= 5) {
			pRobot.rechargeBattery();
		}
		boolean execute = this.execute(pRobot);
		if (execute) {

			aLogger.addLog(new ActionEntry( this.getActionName(), pRobot.getBatteryCharge()));// when executing the action log it
			

			pRobot.updateBatteryLevel();
		}
		return execute;

	}
	
	public String getActionName() {
		return aDecoratedAction.getActionName(); //return the name of the Action being decorated
	}
	
	

}
