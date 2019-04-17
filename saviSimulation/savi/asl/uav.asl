/*
 * UAV Agent Behaviour
 * @author	Michael Vezina
 * @date	26 March 2019
 */

// Set initial beliefs and test goals
pi(3.14159265359).			// Set the constant for PI
proximityThreshold(30.0).	// When the agent is closer than this threashold, no need to get closer
mapSize(900, 700).

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
!patrol.    // Patrol the map

// Remove any beliefs broadcast by other agents so they don't litter the belief base!
+notifyThreat(_,_,_,_,_,_)[source(_)]
    : true
    <- -notifyThreat(_,_,_,_,_,_)[source(_)].

// Broadcasts any visible threats
+!broadcastVisibleThreats
    :   threat(AZ,EL,RANGE,RADIUS,TIME,TYPE) &
        position(X_REF, Y_REF, Z_REF, _) &
        velocity(BEARING, _,_,_)
    <-  savi.UxVInternalActions.GetAbsolutePosition(X_TARGET,Y_TARGET,Z_TARGET,X_REF,Y_REF,Z_REF,BEARING,AZ,EL,RANGE);
       	.broadcast(tell, notifyThreat(X_TARGET,Y_TARGET,Z_TARGET,RADIUS,TIME,TYPE)).
+!broadcastVisibleThreats.

// Randomly generate next coordinate
nextDest(X, Y) :-
    mapSize(MX, MY) & .random(RX) & .random(RY) & X = (RX*MX) & Y = (RY*MY).


// Generate a random destination if one does not exist.
+!patrol
    :   not destination(_, _, _) &
        nextDest(X, Y)
    <-  +destination(X, Y, 0);
        !patrol.

// If a destination exists, we want to generate a relative destination so that we know how to go to the destination
+!patrol
    :   destination(X_DEST, Y_DEST, Z_DEST) &
        position(X_REF,Y_REF,Z_REF,_) &
        velocity(BEARING,_,_,_)
    <-  !broadcastVisibleThreats;
        savi.UxVInternalActions.GetRelativePosition(X_DEST,Y_DEST,0,X_REF,Y_REF,0,BEARING,AZ,EL,RANGE);
        -relativeDestination(_,_,_);
        +relativeDestination(AZ, EL, RANGE);
        !goToDestination;
        !patrol.

// Turns right if the destination is to our right
+!goToDestination
    : destRight
    <-  turn(right).


// Turns left if destination is to our left
+!goToDestination
    : destLeft
    <-  turn(left).

// Move if the destination is ahead of us (and far)
+!goToDestination
    :   destAhead(R) &
        proximityThreshold(T) &
        R > T
    <-  !move.

// Stop moving if we arrive at the destination
+!goToDestination
    : true
    <-  !stopMoving;
        -destination(_,_,_);
        .wait(500).


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