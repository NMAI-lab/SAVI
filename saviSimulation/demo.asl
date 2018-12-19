/*
 * Simple agent behaviour for the SAVI project
 * @author	Patrick Gavigan
 * @date	18 December 2018
 */

/* Set initial beliefs */
noThreat.
velocity(0,0).
//pi(3.14159265359).
turnAngle(3.14159265359/16).

/* Rules */
// See a threat to the right
threatRight(T) :-	(threat(T,DIR,DIST) &
					(DIR > ANGLE) &
					turnAngle(ANGLE)).

// See a threat to the left
threatLeft(T) :-	(threat(T,DIR,DIST) &
					(DIR < -ANGLE) &
					turnAngle(ANGLE)).

// See a threat ahead				
threatAhead(T) :-	(threat(T,DIR,DIST) &
					(-ANGLE <= DIR) &
					(DIR <= ANGLE) &
					turnAngle(ANGLE)).

/* Initial goals */
//!seeThreat.			// Find a threat
//!observeThreat		// Keep a threat visible (recursive seeThreat)
//!faceThreat.			// Turn to face a threat head on
//!watchThreat			// Face a threat and keep facing it recursively
!followThreat.			// Follow a threat

/* Plans */

/* Update beliefes of observing threat aircraft */
+aircraft(DIR,DIST)
	:	noThreat
	<- 	-noThreat;
		+threat(aircraft,DIR,DIST).

/* Update threat beliefs due to relative movement */
+aircraft(DIR,DIST)
	:	(not aircraft(OLD_DIR,OLD_DIST)) &
		threat(aircraft,OLD_DIR,OLD_DIST) &
		((DIR \== OLD_DIR) | (DIST \== OLD_DIST))
	<- 	-threat(aircraft,OLD_DIR,old_DIST);
		+threat(aircraft,DIR,DIST).
		
/* Update belief of not seeing threat aircraft */
-aircraft(_,_)
	: 	threat(aircraft,OLD_DIR,OLD_DIST) &
		(not aircraft(OLD_DIR,OLD_DIST))
	<-	-threat(aircraft,OLD_DIR,OLD_DIST);
		+noThreat.

/* Manage beliefs associated with agent velocity */
+velocity(HEADDING,SPEED)
	:	speedData(NEW_HEADDING,NEW_SPEED) &
		(HEADDING \== NEW_HEADDING | SPEED \== NEW_SPEED)
	<-	-velocity(HEADDING,SPEED);
		+velocity(NEW_HEADDING,NEW_SPEED).
		
/* Plan for trying to see threat */
+!seeThreat
	:	noThreat
	<-	turn(left);
		!seeThreat.

/* See a threat, seeThreat achieved */
+!seeThreat
	:	(not noThreat) | threat(_,_,_).

/* Stop the agent if it is moving */
+!seeThreat
	:	noThreat &
		velocity(_,SPEED) &
		SPEED \== 0
	<-	thrust(off);
		!seeThreat.
	
/* Implementation of observeThreat (recursive seeThreat) */
+!observeThreat
	:	true
	<-	!seeThreat.

/* Plan for facing the threat if none is seen */
+!faceThreat
	:	noThreat
	<-	!seeThreat;
		!faceThreat.

/* Face a threat to the right */
+!faceThreat
	:	threatRight(T)
	<-	turn(right);
		!faceThreat.
		
/* Face a threat to the right */
+!faceThreat
	:	threatLeft(T)
	<-	turn(left);
		!faceThreat.
		
/* Face a threat, goal achieved */
+!faceThreat:	threatAhead(T).

/* watchThreat - recursive faceThreat */
+!watchThreat
	:	true
	<-	!watchThreat.

/* Follow a threat - case where threat not ahead */	
+!followThreat
	:	(not threatAhead(_,_,_))
	<-	!faceThreat;
		!followThreat.

/* Follow a threat that is ahead */
+!followThreat
	:	threatAhead(_,_,_) &
		velocity(_,SPEED) &
		SPEED == 0
	<-	thrust(on);
		!followThreat.

