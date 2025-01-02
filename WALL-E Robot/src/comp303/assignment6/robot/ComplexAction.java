package comp303.assignment6.robot;

import java.util.LinkedList;

public class ComplexAction extends Action {

	//Keeps track of Actions added to the custom action, which may composed of many different ActionInterface
	private LinkedList<ActionInterface> aActionSequence = new LinkedList<ActionInterface>();

	//Sets the customAction name 
	public ComplexAction(String pName) {
		super(pName);
	}
	
	// Adds a basic action to a custom action, if a null action is added the method returns false
	public boolean addAction(ActionInterface pAction) {
		if (pAction == null) {
			return false;
		}
		aActionSequence.add(pAction);
		return true;
	}

	// Removes a basic action to a custom action, if a null action or an action that isn't present is removed the method returns false
	public boolean removeAction(ActionInterface pAction) {
		if (pAction == null) {
			return false;
		}
		return aActionSequence.remove(pAction);

	}

	//Goes through and executes with proper protocols each ActionInterface item in the CustomAction's LinkedList of ActionInterfaces
	public boolean execute(Robot pRobot) {
		for(ActionInterface action: aActionSequence) {
			if (!action.executeProtocolAction(pRobot)) {
				return false;
			}
		}
		return true;
	}
	
	//accepts a ActionVisitor type on each ActionInterface in the LinkedList
	public void accept (ActionVisitor pVisitor) {	
		pVisitor.visitCustomAction(this);
		for(ActionInterface action: aActionSequence) {
			action.accept(pVisitor);
		}
	}



}
