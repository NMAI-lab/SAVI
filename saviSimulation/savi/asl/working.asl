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
//!observeTarget.	// Keep a target visible (recursive seeTarget)
//!faceTarget.		// Turn to face a target head on
//!watchTarget.		// Face a target and keep facing it recursively
!followTarget.		// Follow a target

/* Plans to achieve goals */

// Plan for trying to find a target
+!findTarget
	:	noTarget & targetLastRight
	<-	turn(right).
		
// Plan for trying to find a target
+!findTarget
	:	noTarget & targetLastLeft
	<-	turn(left).

// See a target. Broadcast it.
+!findTarget
	:	target(threat,TYPE,AZ,EL,RANGE) & 
		position(X_REF,Y_REF,Z_REF,_) &
		velocity(BEARING,_,_,_)
	<-	savi.UxVInternalActions.GetAbsolutePosition(X_TARGET,Y_TARGET,Z_TARGET,X_REF,Y_REF,Z_REF,BEARING,AZ,EL,RANGE);
		.broadcast(tell,threatSeen(X_TARGET,Y_TARGET,Z_TARGET)).

// Default plan for observing target - force recursion.
+!observeTarget
	: 	true
	<- 	!findTarget;
		!observeTarget.
		
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
		!watchTarget.
		
// Follow a target that is ahead but not close
+!followTarget
	:	targetFar
	<-	!move;
		!faceTarget;
		!followTarget.
		
// Follow a target that is close (stop moving, don't want to pass it)
// OR Can't see a target, stop find one.
+!followTarget
	:	noTarget | targetClose
	<-	!stopMoving;
		!faceTarget;
		!followTarget.

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
		
