/*
 * UAV Agent Behaviour
 * @author	Michael Vezina
 * @date	26 March 2019
 */

// Set initial beliefs and test goals
pi(3.14159265359).			// Set the constant for PI
proximityThreshold(30.0).	// When the agent is closer than this threashold, no need to get closer

/* Rules */
// Define the turn angle
turnAngle(A) :-
	((A = PI/16) & pi(PI)).

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

// Initial goals
// TODO: Patrol general area vs just going to one destination
!patrol.    // Patrol the map

+!patrol
    :   not destination(_, _, _)
    <-  .print("No Dest");
        +destination(205, 222, 3);
        !patrol.

+!patrol
    :   destination(X_DEST, Y_DEST, Z_DEST) &
        position(X_REF,Y_REF,Z_REF,_) &
        velocity(BEARING,_,_,_)
    <-  savi.UxVInternalActions.GetRelativePosition(X_DEST,Y_DEST,0,X_REF,Y_REF,0,BEARING,AZ,EL,RANGE);
        .print("Destination Exists: ", AZ, " ", EL, " ", RANGE);
        -relativeDestination(_,_,_);
        +relativeDestination(AZ, EL, RANGE);
        !turnToDest.

+!turnToDest
    : destRight
    <- .print("Right!");
        turn(right);
        !patrol.

+!turnToDest
    : destLeft
    <- .print("Left!");
        turn(left);
        !patrol.

+!turnToDest
    : destAhead(R)
    & R > 30.0
    <- .print("RANGE: ", R);
        thrust(on);
        !patrol.
+!turnToDest
    : true
    <- thrust(off);
       !patrol.


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
