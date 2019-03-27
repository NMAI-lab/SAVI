/*
 * UAV Agent Behaviour
 * @author	Michael Vezina
 * @date	26 March 2019
 */

// Set initial beliefs and test goals
pi(3.14159265359).			// Set the constant for PI
proximityThreshold(30.0).	// When the agent is closer than this threashold, no need to get closer

/* Rules */


// Initial goals
// TODO: Patrol general area vs just going to one destination
!patrol.    // Patrol the map

+!patrol
    :   not destination(_, _, _)
    <-  .print("No Dest");
        turn(right);
        +destination(1, 2, 3);
        !patrol.

+threat(AZ,EL,RANGE,RADIUS,TIME,TYPE)
    : true
    <- .print("Threat Found: ", RANGE).


+!patrol
    :   destination(X, Y, Z) &
        not threat(_,_,_,_,_,_)
    <-  .print("Destination no threat");
        .print("dest: ", X);
        !move;
        !patrol.

+!patrol
    :   destination(X_DEST, Y_DEST, Z_DEST) &
        threat(AZ,EL,RANGE,RADIUS,TIME,TYPE) &
        position(X_REF,Y_REF,Z_REF,_) &
        velocity(BEARING,_,_,_)
    <-  savi.UxVInternalActions.GetRelativePosition(X_DEST,Y_DEST,Z_DEST,X_REF,Y_REF,Z_REF,BEARING,AZ,EL,RANGE);
        .print("Destination threat: ", X_REF);
        turn(dest);
        !move;
        !patrol.

// See a target to the right
destinationRight :-
    (target(threat,TYPE,AZ,EL,RANGE,RADIUS) &
    turnAngle(ANGLE) &
    pi(PI) &
    (ANGLE < AZ) &
    (AZ <= PI)).

// See a target to the left
destinationLeft :-
    (target(threat,TYPE,AZ,EL,RANGE,RADIUS) &
    turnAngle(ANGLE) &
    pi(PI) &
    ((PI < AZ) &
    (AZ < ((2 * PI) - ANGLE)))).


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
		
