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

// Remove any beliefs broadcast by other agents so they don't litter the belief base!
+notifyThreat(_,_,_,_,_,_)[source(_)]
    : true
    <- -notifyThreat(_,_,_,_,_,_)[source(_)].

+!broadcastVisibleThreats
    :   threat(AZ,EL,RANGE,RADIUS,TIME,TYPE) &
        position(X_REF, Y_REF, Z_REF, _) &
        velocity(BEARING, _,_,_)
    <-  savi.UxVInternalActions.GetAbsolutePosition(X_TARGET,Y_TARGET,Z_TARGET,X_REF,Y_REF,Z_REF,BEARING,AZ,EL,RANGE);
       	.broadcast(tell, notifyThreat(X_TARGET,Y_TARGET,Z_TARGET,RADIUS,TIME,TYPE)).

+!broadcastVisibleThreats.

// Hard coded random generator between 100 and 800 (X value of destination)
nextRand(X) :-
    .random(R) & X = (R*700)+100.


+!patrol
    :   not destination(_, _, _) &
        nextRand(X)
    <-  +destination(X, 222, 3);
        !patrol.


+!patrol
    :   destination(X_DEST, Y_DEST, Z_DEST) &
        position(X_REF,Y_REF,Z_REF,_) &
        velocity(BEARING,_,_,_)
    <-  !broadcastVisibleThreats;
        savi.UxVInternalActions.GetRelativePosition(X_DEST,Y_DEST,0,X_REF,Y_REF,0,BEARING,AZ,EL,RANGE);
        -relativeDestination(_,_,_);
        +relativeDestination(AZ, EL, RANGE);
        !turnToDest.

+!turnToDest
    : destRight
    <-  turn(right);
        !patrol.

+!turnToDest
    : destLeft
    <-  turn(left);
        !patrol.

+!turnToDest
    :   destAhead(R) &
        proximityThreshold(T) &
        R > T
    <-  thrust(on);
        !patrol.

+!turnToDest
    : true
    <-  thrust(off);
        -destination(_,_,_);
        .wait(500);
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