/*
 * Simple agent behaviour for the SAVI project
 * @author	Patrick Gavigan
 * @date	25 February 2019
 */

// Set initial beliefs and test goals
pi(3.14159265359).			// Set the constant for PI
proximityThreshold(30.0).	// When the agent is closer than this threashold, no need to get closer
targetLastRight.

/* Rules */

// Define the turn angle
turnAngle(A) :-
	((A = PI/16) & pi(PI)).

// There is no target
noTarget :-
	not(target(_,_,_,_,_,_)).

// Target is the closest threat
target(threat,TYPE,AZ,EL,RANGE,RADIUS) :-
	((threat(AZ,EL,RANGE,RADIUS,TIME,TYPE)) &
	not (threat(_,_,CLOSER,_,_,_) &
	(CLOSER < RANGE))).

// See a target to the right
targetRight :-
	(target(threat,TYPE,AZ,EL,RANGE,RADIUS) &
	turnAngle(ANGLE) &
	pi(PI) &
	(ANGLE < AZ) & 
	(AZ <= PI)).

// See a target to the left
targetLeft :-
	(target(threat,TYPE,AZ,EL,RANGE,RADIUS) &
	turnAngle(ANGLE) &
	pi(PI) &
	((PI < AZ) &
	(AZ < ((2 * PI) - ANGLE)))).

// See a target ahead				
targetAhead :-
	((not noTarget) &
	(not targetLeft) &
	(not targetRight)).
	
// Target is close
targetClose :-
	target(_,_,_,_,RANGE,_) &
	proximityThreshold(CLOSE) &
	(RANGE < CLOSE).

// Target is far
targetFar :-
	target(_,_,_,_,RANGE,_) &
	proximityThreshold(CLOSE) &
	(RANGE > CLOSE).
	
// Initial goals
//!findTarget.		// Find a target
//!faceTarget.		// Turn to face a target head on
//!watchTarget.		// Face a target and keep facing it recursively
!followDestTarget.		// Follow a target
//+destination(250, 250, 250).


+!followDestTarget
    : true
    <- +destination(500,500,0);
        !followTarget.

/* Plans to achieve goals */

// Plan for trying to find a target
+!findTarget
	:	noTarget & targetLastRight
	<-	turn(right).
		
// Plan for trying to find a target
+!findTarget
	:	noTarget & targetLastLeft
	<-	turn(left).
		
// Plan for facing the target if none is seen
+!faceTarget
	:	noTarget
	<-	!findTarget.

// Face a target to the right.
+!faceTarget
	:	targetRight
	<-	turn(right);
		+targetLastRight;
		-targetLastLeft.
		
// Face a target to the left
+!faceTarget
	:	targetLeft
	<-	turn(left);
		+targetLastLeft;
		-targetLastRight.
		
// Face a target, goal achieved
+!faceTarget
	:	targetAhead.

// watchTarget - recursive faceTarget
+!watchTarget
	:	true
	<-	!faceTarget;
		!watchTarget;
		.print("tell,turning(left)").
		
// Follow a target that is ahead but not close
+!followTarget
	:	targetFar
	<-	!move;
		!faceTarget;
		!followTarget.
		
// Follow a target that is close (stop moving, don't want to pass it)
// OR Can't see a target, stop find one.
+!followTarget
	:	targetClose | (noTarget & not destination(_,_,_))
	<-	!stopMoving;
		!faceTarget;
		!followTarget.

+!followTarget
    :   noTarget &
        destination(X_DEST, Y_DEST, Z_DEST)
        <-  .print("No targets found. Using destination.");
            -relativeDestination(_,_,_);
            !goToDest;
            !followTarget.

+!goToDest
    :   destination(X_DEST, Y_DEST, Z_DEST) &
        not relativeDestination(_,_,_) &
        position(X_REF,Y_REF,Z_REF,_) &
        velocity(BEARING,_,_,_)
    <-  .print("Determining Relative Destination");
        // Only navigate X, Y position for now, Z=0
        savi.UxVInternalActions.GetRelativePosition(X_DEST,Y_DEST,0,X_REF,Y_REF,0,BEARING,AZ,EL,RANGE);
        +relativeDestination(AZ, EL, RANGE);
        !goToDest.

+!goToDest
    : destRight
    <- .print("Destination is Right!");
        turn(right).

+!goToDest
    : destLeft
    <- .print("Destination is Left!");
        turn(left).

+!goToDest
    :   destAhead(R)
        & R > 30.0
    <-  .print("RANGE: ", R);
        thrust(on).

+!goToDest
    : destAhead(R)
      & R < 30.0
    <-  thrust(off);
        -destination(_,_,_).

// Start moving if not moving
+!move
	: 	velocity(_,_,SPEED,_) &
		SPEED == 0.0
	<-	thrust(on).
+!move.

// Stop if moving
+!stopMoving
	: 	velocity(_,_,SPEED,_) &
		SPEED \== 0.0
	<-	thrust(off).
+!stopMoving.
		
/** Dest **/
destLeft :-
	(relativeDestination(AZ,EL,RANGE) &
	turnAngle(ANGLE) &
	pi(PI) &
	((PI < AZ) &
	(AZ < ((2 * PI) - ANGLE)))).

destRight :-
	(relativeDestination(AZ,EL,RANGE) &
	turnAngle(ANGLE) &
    	pi(PI) &
    	(ANGLE < AZ) &
    	(AZ <= PI)).

destAhead(R) :-
    (not destLeft & not destRight & relativeDestination(AZ, EL, R)).

+!patrol
    :   destination(X_DEST, Y_DEST, Z_DEST) &
        position(X_REF,Y_REF,Z_REF,_) &
        velocity(BEARING,_,_,_)
    <-  savi.UxVInternalActions.GetRelativePosition(X_DEST,Y_DEST,0,X_REF,Y_REF,0,BEARING,AZ,EL,RANGE);
        .print("Destination Exists: ", AZ, " ", EL, " ", RANGE);
        -relativeDestination(_,_,_);
        +relativeDestination(AZ, EL, RANGE);
        !turnToDest.

