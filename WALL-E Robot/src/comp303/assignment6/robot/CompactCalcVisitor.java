package comp303.assignment6.robot;

public class CompactCalcVisitor implements ActionVisitor {

	//calculates the total compacted objects without actually executing the CompactObject action
	private int aTotalCompactedObjects = 0;
	
	public void resetCompactedObjects() {
		aTotalCompactedObjects = 0;
	}
	
	@Override
	public void visitMoveForward(MoveForward pMove) {
		// no update to number of compacted objects		
	}

	@Override
	public  void visitRotate90(Rotate90 pRotate) {
		// no update to number of compacted objects	
	}

	@Override
	public  void visitRelease(ReleaseObject pRelObj) {
		// no update to number of compacted objects
	}

	@Override
	public  void visitGrabObject(GrabObject pGrabObj) {
		// no update to number of compacted objects	
	}

	@Override
	public  void visitCompactObject(CompactObject pCompactObj) {
		// increment number of compacted objects
		aTotalCompactedObjects ++;
	}

	@Override
	public  void visitEmptyCompactor(EmptyCompactor pEmptyCompactor) {
		// no update to number of compacted objects		
	}

	@Override
	public  void visitCustomAction(ComplexAction pCustomAction) {
		// will get resolved in accept
	}
	
	public void visitActionDecorator(ActionDecorator pDecorator) {
		// no update to number of compacted objects
	}
	public void visitProgram(Program pProgram) {
		// will get resolved in accept
	}
	
	//returns the number of compacted objects
	public int numberOfCompactedObjects()
	{
		return aTotalCompactedObjects;
	}

}
