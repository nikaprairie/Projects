package comp303.assignment6.robot;

public class DistanceCalcVisitor implements ActionVisitor {

	private double aTotalDistance = 0.0;
	
	public void resetDistance() {
		aTotalDistance = 0.0;
	}
	
	@Override
	public void visitMoveForward(MoveForward pMove) {
		// increment aTotalDistance
		aTotalDistance += pMove.getDistance();
		
	}

	@Override
	public  void visitRotate90(Rotate90 pRotate) {
		// no update to distance	
	}

	@Override
	public  void visitRelease(ReleaseObject pRelObj) {
		// no update to distance	
	}

	@Override
	public  void visitGrabObject(GrabObject pGrabObj) {
		// no update to distance	
	}

	@Override
	public  void visitCompactObject(CompactObject pCompactObj) {
		// no update to distance		
	}

	@Override
	public  void visitEmptyCompactor(EmptyCompactor pEmptyCompactor) {
		// no update to distance
	}

	@Override
	public  void visitCustomAction(ComplexAction pCustomAction) {
		// will get resolved in accept
	}
	
	public void visitActionDecorator(ActionDecorator pDecorator) {
		// no update to distance
	}
	public void visitProgram(Program pProgram) {
		// will get resolved in accept
	}
	
	
	public double distanceTravelled()
	{
		return aTotalDistance;
	}

}
