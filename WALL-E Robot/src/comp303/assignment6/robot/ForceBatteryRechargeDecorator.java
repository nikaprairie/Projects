package comp303.assignment6.robot;

public class ForceBatteryRechargeDecorator extends ActionDecorator {
	

	//constructor for the ForceBatteryRechargeDecorator which takes an ActionInterface which will have the force battery applied onto it
	public ForceBatteryRechargeDecorator(ActionInterface pDecoratedAction) {
		super(pDecoratedAction);
	}
	
	//recharges the battery then executes the action which has been passed to the decorator, will always return true because it is being forced
	public boolean execute(Robot pRobot) {
		pRobot.rechargeBattery();
		aDecoratedAction.execute(pRobot);
		return true;
	}

}
