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

/** ================= **/
/** Destination Rules **/
/** ================= **/

// Gets the most recent notification and determines the relative position (only X,Y coordinates for UGV)
destination(AZ,EL,RANGE,TIME) :-
    position(X_REF,Y_REF,Z_REF,_) &
    velocity(BEARING,_,_,_) &
    ((notifyThreat(X_DEST, Y_DEST, Z_DEST, _,TIME,_)) &
    not (notifyThreat(_,_,_,_,NEWER,_) & (TIME < NEWER))) &
    savi.UxVInternalActions.GetRelativePosition(X_DEST,Y_DEST,0,X_REF,Y_REF,0,BEARING,AZ,EL,RANGE).


// For all available (recent) destinations, we want to choose the closest one
closestDestination(AZ, EL, RANGE, TIME) :-
    destination(AZ,EL,RANGE,TIME) &
    not (destination(_,_,CLOSER,_) &
    	(CLOSER < RANGE)).

destLeft(AZ,EL,RANGE) :-
	turnAngle(ANGLE) &
	pi(PI) &
	((PI < AZ) &
	(AZ < ((2 * PI) - ANGLE))).

destRight(AZ,EL,RANGE) :-
	turnAngle(ANGLE) &
    	pi(PI) &
    	(ANGLE < AZ) &
    	(AZ <= PI).

destAhead(AZ,EL,RANGE) :-
    (not destLeft(AZ,EL,RANGE) & not destRight(AZ,EL,RANGE)).

noDestination :-
    not(destination(_,_,_,_)).
	
/** ============= **/
/** Initial Goals **/
/** ============= **/

//!sendTelemetry.
//!findTarget.		// Find a target
//!faceTarget.		// Turn to face a target head on
//!watchTarget.		// Face a target and keep facing it recursively
//!followTarget.		// Follow a target

/** =================================================== **/
/** Deal with telemetry request							**/
/** =================================================== **/
+!sendTelemetry
	:	position(X,Y,Z,TP) & velocity(BEARING,PITCH,SPEED,TV)
	<-	.broadcast(tell, notifyPosition(X,Y,Z,TP,ugv));
		.broadcast(tell, notifyVelocity(BEARING,PITCH,SPEED,TV,ugv)).
+!sendTelemetry.

/** =================================================== **/
/** Removing old notifyThreat beliefs from other agents **/
/** =================================================== **/

// Clears all notifyThreat messages (useful if we are following a visible target)
+!clearAllThreatNotifications
    :   notifyThreat(_,_,_,_,_,_)[source(S)]
    <- -notifyThreat(_,_,_,_,_,_)[source(S)];
        !clearAllThreatNotifications.
+!clearAllThreatNotifications.

// Clears all old notifyThreat messages (ensures our belief base is up-to-date)
+!clearOldThreatNotifications
    :   destination(_,_,_,TIME) &
        notifyThreat(_,_,_,_,OLD,_)[source(S)] &
        OLD < TIME
    <- -notifyThreat(_,_,_,_,OLD,_)[source(S)];
        !clearOldThreatNotifications.
+!clearOldThreatNotifications .

/** ======================================================== **/
/** ======================================================== **/

// Broadcast the absolute positions of any visible threats
+!broadcastVisibleThreats
    :   threat(AZ,EL,RANGE,RADIUS,TIME,TYPE) &
        position(X_REF, Y_REF, Z_REF, _) &
        velocity(BEARING, _,_,_)
    <-  savi.UxVInternalActions.GetAbsolutePosition(X_TARGET,Y_TARGET,Z_TARGET,X_REF,Y_REF,Z_REF,BEARING,AZ,EL,RANGE);
       	.broadcast(tell, notifyThreat(X_TARGET,Y_TARGET,Z_TARGET,RADIUS,TIME,TYPE)).

+!broadcastVisibleThreats.


/** ========== **/
/** Main Plans **/
/** ========== **/

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
		
// Follow a target that is ahead but not close (ignoring any threat notifications)
+!followTarget
	:	targetFar
	<-	!clearAllThreatNotifications; // Clear all threat notifications since we are chasing a visible target
	    !broadcastVisibleThreats;
	    !avoidObstacle;
	    !move;
		!faceTarget;
		!followTarget.
		
// Follow a target that is close (stop moving, don't want to pass it)
// OR Can't see a target, stop find one. (ignoring any threat notifications since we have a target)
+!followTarget
	:	targetClose | (noTarget & noDestination)
	<-	!clearAllThreatNotifications; // Clear all threat notifications since we are chasing a visible target
	    !broadcastVisibleThreats;
	    !avoidObstacle;
	    !stopMoving;
		!faceTarget;
		!followTarget.

// We can't see any targets so find the closest destination (using global threat notifications from other sources)
+!followTarget
    :   noTarget &
        closestDestination(AZ,EL,RANGE,TIME)
    <-  !clearOldThreatNotifications; // Clear old threat notifications, since they are no longer necessary
        !avoidObstacle;
        !goToDestination(AZ,EL,RANGE); // Go to the current destination (AKA threats from other agents)
        !followTarget.

/** ====================================== **/
/** Sub-goals/plans for obstacle avoidance **/
/** ====================================== **/

// Drop all intentions and turn if we see a tree
// Ensure that our belief base doesn't get littered with notifyThreat while we do this by clearing notifications.
// Recursively avoid obstacle until we can't see anymore trees
+!avoidObstacle
    :   tree(AZ,EL,RANGE,RADIUS,TIME,TYPE) &
        proximityThreshold(T) &
        RANGE < (T + RADIUS)
    <- .drop_all_intentions;
       !stopMoving;
       turn(right);
       !clearAllThreatNotifications; // We need to keep these clear since we've dropped all other intentions. Surely there is a better way to handle this?
       !avoidObstacle.

+!avoidObstacle.


/** =========================================== **/
/** Sub-goals for navigating to the destination **/
/** =========================================== **/

// Turn right if destination is on the right
+!goToDestination(AZ,EL,RANGE)
    : destRight(AZ,EL,RANGE)
    <- turn(right).

// Turn left if destination is on the left
+!goToDestination(AZ,EL,RANGE)
    : destLeft(AZ,EL,RANGE)
    <- turn(left).

// Move if destination is ahead of us
+!goToDestination(AZ,EL,RANGE)
    :   destAhead(AZ,EL,RANGE) &
        proximityThreshold(T) &
        R > T
    <-  !move.

// Stop moving if we have reached the destination
// Clear all current threat notifications and wait for new data (or search for threats)
+!goToDestination(AZ,EL,RANGE)
    : destAhead(AZ,EL,RANGE) &
        proximityThreshold(T) &
        R < T
    <-  !stopMoving;
        // Clear threats if we reach the destination
        !clearAllThreatNotifications.

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

// Address possibility of being asked to patrol (no plans)
+!patrol.
