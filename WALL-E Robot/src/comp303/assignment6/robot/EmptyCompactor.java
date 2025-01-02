package comp303.assignment6.robot;

public class EmptyCompactor extends Action implements ActionInterface {
	
	//sets the name of the emptyCompactor action to "EmptyCompactor" as a default value and calls the super
	public EmptyCompactor() {
		super("EmptyCompactor");
	}
	
	//specific routine to execute the EmptyCompactor Action
	public boolean execute(Robot pRobot) {
		
		if(pRobot.getCompactorLevel() > 0) {
			
			pRobot.emptyCompactor();
			return true;
		}
		
		return false;
	}
	
	//accepts an ActionVisitor 
	public void accept (ActionVisitor pVisitor) {
		pVisitor.visitEmptyCompactor(this);
	}
}
