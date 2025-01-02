package comp303.assignment6.robot;
//import java.util.ArrayList;

public class Program {
	
	//Private customAction which can be composed of many ActionInterfaces, 
	//essentially a list of any sort of action that could be executed by a program given a Robot
	private ComplexAction aComplexAction;
	
	//Program constructor, takes String for program name
	public Program(String pName) {
		aComplexAction = new ComplexAction(pName);
		
	}
	
	//Adds a basic action or custom action to the CustomAction, which holds all the actions the program has
	public boolean addAction(ActionInterface pAction) {
		return aComplexAction.addAction(pAction);
	}
	// Return the name of the program
	public String getName() {
		return aComplexAction.getActionName();
	}
	
	//Removes a basic action or custom action from the CustomAction, which holds all the actions the program has
	public boolean removeAction(ActionInterface pAction) {
		return aComplexAction.removeAction(pAction);
	}
	
	//running the program executes all the actions which that program holds
	public void run(Robot pRobot) {
		aComplexAction.executeProtocolAction(pRobot);
	}
	
	//Accepts a ActionVisitor
	public void accept(ActionVisitor pVisitor) {
		pVisitor.visitProgram(this);
		aComplexAction.accept(pVisitor);
	}
	

	

}
