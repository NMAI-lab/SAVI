/*
 * Simple agent behaviour for the SAVI project
 * @author	Patrick Gavigan
 * @date		25 Jan 2019
 */

// Set initial beliefs and test goals
pi(3.14159265359).	// Set the constant for PI

/* Rules */
// Define the turn angle

turnAngle(A) :-
	((A = PI/16) & pi(PI)).

// There is no target
noTarget :-
	not(threat(DIR,DIST)).

// Target is the closest threat
target(threat,DIR,DIST) :-
	((threat(DIR,DIST)) &
	not (threat(_,CLOSER) &
	(CLOSER < DIST))).

// See a target to the right
targetRight :-
	(target(threat,DIR,DIST) &
	turnAngle(ANGLE) &
	pi(PI) &
	(ANGLE < DIR) & 
	(DIR <= PI)).

// See a target to the left
targetLeft :-
	(target(threat,DIR,DIST) &
	turnAngle(ANGLE) &
	pi(PI) &
	((PI < DIR) &
	(DIR < ((2 * PI) - ANGLE)))).

// See a target ahead				
targetAhead :-
	(target(T,DIR,DIST) &
	(not targetLeft(T)) &
	(not targetRight(T))).
	
// Initial goals
//!findTarget.		// Find a target
//!observeTarget.	// Keep a target visible (recursive seeTarget)
//!faceTarget.		// Turn to face a target head on
//!watchTarget.		// Face a target and keep facing it recursively
!followTarget.		// Follow a target

/* Plans to achieve goals */

// Plan for trying to find a target
+!findTarget	:	noTarget
				<-	turn(left);
					.broadcast(tell,turning(left)).
+!findTarget.

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
		.broadcast(tell,turning(right)).
		
// Face a target to the left
+!faceTarget
	:	targetLeft
	<-	turn(left);
		.broadcast(tell,turning(left)).
		
// Face a target, goal achieved
+!faceTarget
	:	targetAhead
	<-	.broadcast(tell,targetAhead).

// watchTarget - recursive faceTarget
+!watchTarget
	:	true
	<-	!faceTarget;
		!watchTarget.
		
// Follow a target that is ahead
+!followTarget
	:	(not noTarget)
	<-	!faceTarget;
		!move;
		!followTarget.
		
// Can't see a target, stop find one.
+!followTarget
	:	noTarget
	<-	!stopMoving;
		!faceTarget;
		!followTarget.

// Start moving if not moving
+!move
	: 	velocity(_,SPEED) &
		SPEED == 0
	<-	thrust(on);
		.broadcast(tell,moving).
+!move.

// Stop if moving
+!stopMoving
	: 	velocity(_,SPEED) &
		SPEED \== 0
	<-	thrust(off);
		.broadcast(tell,stopping).
+!stopMoving.
		
