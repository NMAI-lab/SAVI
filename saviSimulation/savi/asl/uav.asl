/*
 * UAV Agent Behaviour
 * @author	Michael Vezina
 * @date	26 March 2019
 */

// Set initial beliefs and test goals
pi(3.14159265359).			// Set the constant for PI
proximityThreshold(30.0).	// When the agent is closer than this threashold, no need to get closer
mapSize(900, 700).
cruisingAltitude(500).		// Set desired cruising altitude for the UAV
altitudeThreshold(100).		// Set desired cruising altitude for the UAV

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
	
// Destination is close
destinationClose :-
	relativeDestination(_,_,RANGE) &
	proximityThreshold(T) &
	(RANGE < T).

// Destination is far
destinationFar :-
	relativeDestination(_,_,RANGE) &
	proximityThreshold(T) &
	(RANGE > T).
	
// Altitude is too low
altitudeTooLow :-
	position(_,_,Z,_) & 
	cruisingAltitude(A) &
	altitudeThreshold(T) &
	(Z < (A - T)).
	
// Altitude is too high
altitudeTooHigh :-
	position(_,_,Z,_) & 
	cruisingAltitude(A) &
	altitudeThreshold(T) &
	(Z > (A + T)).

// Altitude is within margin
altitudeCorrect :-
	not (altitudeTooLow | altitudeTooHigh).
		
// Initial goals
//!sendTelemetry.
!patrol.    // Patrol the map

// Deal with telemetry request/
+!sendTelemetry
	:	position(X,Y,Z,TP) & velocity(BEARING,PITCH,SPEED,TV)
	<-	.broadcast(tell, notifyPosition(X,Y,Z,TP));
		.broadcast(tell, notifyVelocity(BEARING,PITCH,SPEED,TV)).
+!sendTelemetry.

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
    :   destinationFar
    <-  !move.

// Stop moving if we arrive at the destination
+!goToDestination
    : destinationClose
    <-  !stopMoving;
        -destination(_,_,_).
        //.wait(500).


// Start moving if not moving
+!move
	:	altitudeTooLow | altitudeTooHigh
	<-	!adjustAltitude;
		!move.

+!move
	: 	velocity(_,_,SPEED,_) &
		SPEED == 0.0 &
		altitudeCorrect
	<-	thrust(on).
+!move.

// Stop if moving
+!stopMoving
	: 	velocity(_,_,SPEED,_) &
		SPEED \== 0.0
	<-	thrust(off).
+!stopMoving.


// Get to the right altitude
+!adjustAltitude
	:	altitudeTooLow
	<-	thrust(up);
		!adjustAltitude.
		
+!adjustAltitude
	:	altitudeTooHigh
	<-	thrust(down);
		!adjustAltitude.
		
+!adjustAltitude
	:	altitudeCorrect
	<-	hover.
	
// Address possibility of being asked to followTarget (no plans)
+!followTarget.
