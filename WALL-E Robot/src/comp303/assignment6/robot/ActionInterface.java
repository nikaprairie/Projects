package comp303.assignment6.robot;

public interface ActionInterface {
	
	//Each Action will implement ActionInterface and implement the execute appropriately 
	public boolean execute(Robot pRobot);
	
	//Ability to accept a visitor
	public void accept (ActionVisitor pVisitor);
	
	//Execute a Basic Action or Custom Action, each implements this ActionInterface
	public boolean executeProtocolAction(Robot pRobot);
	
	//Returns the name of the Action
	public String getActionName();

}
