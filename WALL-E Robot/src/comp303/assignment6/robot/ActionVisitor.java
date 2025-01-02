package comp303.assignment6.robot;

public interface ActionVisitor {
	
	//visitor interface for Actions
	
	void visitMoveForward(MoveForward pMove);
	void visitRotate90(Rotate90 pRotate);
	void visitRelease(ReleaseObject pRelObj);
	void visitGrabObject(GrabObject pGrabObj);
	void visitCompactObject(CompactObject pCompactObj);
	void visitEmptyCompactor(EmptyCompactor pEmptyCompactor);
	void visitCustomAction(ComplexAction pCustomAction);
	void visitActionDecorator(ActionDecorator pDecorator);
	void visitProgram(Program pProgram);
	
}
