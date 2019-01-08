/*
 * Simple agent behaviour for the SAVI project
 * @author	Patrick Gavigan
 * @date	18 December 2018
 */

/* Set initial beliefs */
noTarget.
velocity(0,0).
//pi(3.14159265359).
turnAngle(3.14159265359/16).
margin(0.2).

/* Rules */
// See a target to the right
targetRight(T) :-	(target(T,DIR,DIST) &
					(DIR > ANGLE) &
					turnAngle(ANGLE)).

// See a target to the left
targettLeft(T) :-	(target(T,DIR,DIST) &
					(DIR < -ANGLE) &
					turnAngle(ANGLE)).

// See a target ahead				
targetAhead(T) :-	(target(T,DIR,DIST) &
					(-ANGLE <= DIR) &
					(DIR <= ANGLE) &
					turnAngle(ANGLE)).

// X is the absolute value of Y					
abs(X,Y) :- 	((X > 0 | X == 0) &
				((X == Y) | (X == -Y))).
				
// Check if X is positive or 0 
positive(X) :-	X > 0 | X == 0.
				
// Check if the similarity of X and Y are within a margin of error.
withinMargin(X, Y) :-	margin(MARGIN) &
						(positive(X - Y) & (X - Y < MARGIN * Y)) |
						(positive(Y - X) & (Y - X < MARGIN * Y)).

/* Initial goals */
//!seeTarget.			// Find a target
//!observeTarget.		// Keep a target visible (recursive seeTarget)
//!faceTarget.			// Turn to face a target head on
//!watchTarget.			// Face a target and keep facing it recursively
!followTarget.			// Follow a target

/* Plans */

/* Update beliefes of observing target */
+threat(DIR,DIST)
	:	noTarget
	<- 	-noTarget;
		+target(threat,DIR,DIST).

/* Update target beliefs due to relative movement */
+threat(DIR,DIST)
	:	(not threat(OLD_DIR,OLD_DIST)) &
		target(threat,OLD_DIR,OLD_DIST) &
		(withinMargin(DIR,OLD_DIR) | withinMargin(DIST,OLD_DIST))
	<- 	-target(threat,OLD_DIR,old_DIST);
		+target(threat,DIR,DIST).
		
/* Update belief of not seeing target */
-threat(_,_)
	: 	target(threat,OLD_DIR,OLD_DIST) &
		(not threat(OLD_DIR,OLD_DIST))
	<-	-target(threat,OLD_DIR,OLD_DIST);
		+noTarget.

/* Manage beliefs associated with agent velocity */
+velocity(HEADDING,SPEED)
	:	speedData(NEW_HEADDING,NEW_SPEED) &
		(HEADDING \== NEW_HEADDING | SPEED \== NEW_SPEED)
	<-	-velocity(HEADDING,SPEED);
		+velocity(NEW_HEADDING,NEW_SPEED).
		
/* Plan for trying to see target */
+!seeTarget
	:	noTarget
	<-	turn(left);
		!seeTarget.

/* See a target, seeTarget achieved */
+!seeTarget
	:	(not noTarget) | target(_,_,_).

/* Stop the agent if it is moving */
+!seeTarget
	:	noTarget &
		velocity(_,SPEED) &
		SPEED \== 0
	<-	thrust(off);
		!seeTarget.
	
/* Implementation of observeTarget (recursive seeTarget) */
+!observeTarget
	:	true
	<-	!seeTarget.

/* Plan for facing the target if none is seen */
+!faceTarget
	:	noTarget
	<-	!seeTarget;
		!faceTarget.

/* Face a target to the right */
+!faceTarget
	:	targetRight(T)
	<-	turn(right);
		!faceTarget.
		
/* Face a target to the right */
+!faceTarget
	:	targetLeft(T)
	<-	turn(left);
		!faceTarget.
		
/* Face a target, goal achieved */
+!faceTarget:	targetAhead(T).

/* watchTarget - recursive faceTarget */
+!watchTarget
	:	true
	<-	!watchTarget.

/* Follow a target - case where target not ahead */	
+!followTarget
	:	(not targetAhead(_,_,_))
	<-	!faceTarget;
		!followTarget.

/* Follow a target that is ahead */
+!followTarget
	:	targetAhead(_,_,_) &
		velocity(_,SPEED) &
		SPEED == 0
	<-	thrust(on);
		!followTarget.

