package comp303.assignment6.robot;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileOutputStream;

public class Driver {
	
	public static void main(String[] args) {
			
		//create basic actions
		Action compactObject = new CompactObject();
		Action emptyCompactor = new EmptyCompactor();
		Action grabObject = new GrabObject();
		Action moveForward = new MoveForward(10.0);
		Action releaseObject = new ReleaseObject();
		Action rotate90Left = new Rotate90(Direction.LEFT);
		Action rotate90Right = new Rotate90(Direction.RIGHT);
		
		//create complex action and add basic actions
		ComplexAction complexAction = new ComplexAction("complex_action");
		complexAction.addAction(moveForward);
		complexAction.addAction(grabObject);
		complexAction.addAction(compactObject);
		complexAction.addAction(emptyCompactor);
		complexAction.addAction(rotate90Right);
		complexAction.addAction(rotate90Left);
		complexAction.addAction(grabObject);
		complexAction.addAction(releaseObject);
		
		//create a new complex action and add it to the other complex action
		ComplexAction complexMoveForward = new ComplexAction("complex_moveforward");
		complexMoveForward.addAction(moveForward);
		complexMoveForward.addAction(moveForward);
		complexAction.addAction(complexMoveForward);
		
		//create a program and add actions, one complex and one basic in this case
		Program program = new Program("myProgram");
		program.addAction(complexAction);
		program.addAction(rotate90Right);
		
		//run the program and create a WallE robot to run the program on
		WallE wallE = new WallE();
		
		program.run(wallE);
		
		
		
		FileOutputStream outputFile;
		
		//how to use the visitor to calculate:
		//      - the distance traveled 
		//      - number of compacted objects
		DistanceCalcVisitor distanceVisitor = new DistanceCalcVisitor();
		double distance = 0; //set distance to 0
		
		
		CompactCalcVisitor compactVisitor = new CompactCalcVisitor();
		int numObjs  = 0; //set number of compacted objects to 0
		
		
		
		TestRobot myRobot = new TestRobot(); // testRobot created to help with testing code and 
		
		
		//Create Actions as basic actions or a decorated action with a forced battery re-charge
		ActionInterface myMvFwd = new MoveForward(10);
		ActionInterface decoratedMvFwd = new ForceBatteryRechargeDecorator (new MoveForward(4));
		ActionInterface grab = new GrabObject();
		ActionInterface release = new ReleaseObject();
		ActionInterface empty = new EmptyCompactor();
		ActionInterface compact = new CompactObject();
		ActionInterface turn = new Rotate90(Direction.RIGHT);
		
		
		
		myMvFwd.executeProtocolAction(myRobot);
		decoratedMvFwd.executeProtocolAction(myRobot);
		grab.executeProtocolAction(myRobot);
		release.executeProtocolAction(myRobot);
		compact.executeProtocolAction(myRobot);
		empty.executeProtocolAction(myRobot);
		turn.executeProtocolAction(myRobot);
		
		//action myMvFwd accepts a visitor
		myMvFwd.accept(distanceVisitor);
		decoratedMvFwd.accept(distanceVisitor); 
		distance = distanceVisitor.distanceTravelled();	//distance is updated with the total distance traveled by that program so far	
		System.out.println("Distance travelled " + distance );
		distanceVisitor.resetDistance();
		
		ComplexAction customAction = new ComplexAction("customAction");
		customAction.addAction(myMvFwd);
		customAction.addAction(decoratedMvFwd);
		customAction.addAction(grab);
		customAction.addAction(release);
		customAction.addAction(empty);
		customAction.addAction(turn);
		customAction.addAction(complexAction);
		
		
		customAction.accept(distanceVisitor);
		distance = distanceVisitor.distanceTravelled();		
		System.out.println("Distance travelled 1 " + distance );
	
		
		//Another way to get total distance and total items compacted is through my Calculating robot
		//which simulates a Robot and how a robot would treat pre-conditons better than a visitor type
		
		
		
		
		
		
		
		distanceVisitor.resetDistance();
		

		
		Program myProgram = new Program("myProgram");
		
		Action compactObject1 = new CompactObject();
		Action emptyCompactor1 = new EmptyCompactor();
		Action grabObject1 = new GrabObject();
		Action moveForward1 = new MoveForward(10.0);
		Action releaseObject1 = new ReleaseObject();
		Action rotate90_1 = new Rotate90(Direction.LEFT);
		
		myProgram.addAction(rotate90_1);
		myProgram.addAction(grabObject1);
		myProgram.addAction(releaseObject1);
		myProgram.addAction(moveForward1);
		myProgram.addAction(grabObject1);
		myProgram.addAction(compactObject1);
		myProgram.addAction(emptyCompactor1);
		myProgram.removeAction(rotate90_1);
		myProgram.removeAction(emptyCompactor1);
		myProgram.addAction(customAction);
		myProgram.addAction(customAction);
		
		
		myProgram.accept(distanceVisitor);
		distance = distanceVisitor.distanceTravelled();		
		System.out.println("Distance travelled " + distance );
		
		CalculatingRobot myCalcRobot = new CalculatingRobot();
		myProgram.run(myCalcRobot);
		double dist = myCalcRobot.getTotalDistanceTravelled();
		System.out.println("Distance travelled 2: " + dist); //dist and distance are different values
		// distance calculated by visitor patter provides a distance 
		// that robot would travel if all commands were executed 
		// however, depending of the action set provided by the client, 
		// not all actions would be executed if they violate pre/post conditions
		// second method of calculating distance is tracking state and calculating 
		// distance until the point in program when it exists (which includes premature exit)
		

		//2nd method provides a more accurate representation of distance traveled by a program 
		
		
		
		
		myProgram.accept(compactVisitor);
		numObjs = compactVisitor.numberOfCompactedObjects();		
		System.out.println("Number of compacted objects for this program " + numObjs );
		
		//WallE newRobot = new WallE();
		CalculatingRobot newRobot2 = new CalculatingRobot();
		//here both visitor and calculating Robot provide the same results
		DistanceCalcVisitor calcVisitor = new DistanceCalcVisitor ();
		Action move = new MoveForward(2.0);
		move.accept(calcVisitor);
		double distance1 = calcVisitor.distanceTravelled();
		move.executeProtocolAction(newRobot2);
		double distance2 = newRobot2.getTotalDistanceTravelled();
		System.out.println("Both are equal, " + distance1 + " " + distance2);
		//Same goes for items compacted
		
		GrabObject newGrab = new GrabObject();
		CompactObject newCompact = new CompactObject();
		
		CompactCalcVisitor vis = new CompactCalcVisitor();
		ComplexAction newCustom = new ComplexAction("new");
		newCustom.addAction(newGrab);
		newCustom.addAction(newCompact);
		newCustom.accept(vis);
		int num1 = vis.numberOfCompactedObjects();
		newCustom.executeProtocolAction(newRobot2);
		int num2 = newRobot2.getTotalCompactedItems();
		System.out.println("Both are equal, " + num1 + " " + num2);
		
		
		Logger.getInstance().outputLog();
		try {
			outputFile = new FileOutputStream("logoutput.txt");
			Logger.getInstance().changeOutput(outputFile);	
			Logger.getInstance().outputLog();
		} 
		catch (Exception e) {
			fail("Unexpected exception");
			return;
		}
	}

}
