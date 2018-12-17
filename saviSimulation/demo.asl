/*
 * Simple agent behaviour for the SAVI project
 * @author	Patrick Gavigan
 * @date	28 November 2018
 */

/*
 * Set initial beliefs
 */
PI(3.14159265359).
TURN_ANGLE(A/16) & PI(A).
//threat(aircraft(DIR, DIST))	// Aircraft are threats
//followable(threat(X))	// Threats are folowable
//facable(followable(X))	// Followable objects are faceable
//findable(faceable(X))	// Faceable objects are findable

/*
 * Rules
 */
 
// Identify if perception is a therat, extract information
//perceivedParameter(DIR,DIST) =.. [PERCEIVE_TYPE, [DIR,DIST]].
//threat(T) :-	[PERCEIVE_TYPE, [DIR,DIST]] &
//				PERCEIVE_TYPE == aircraft.

// See a threat to the right
threatRight(T) :-	(aircraft(DIR, DIST) &
					DIR > ANGLE &
					TURN_ANGLE(ANGLE)).

// See a threat to the left
threatLeft(T) :-	(aircraft(DIR, DIST) &
					DIR < -ANGLE &
					TURN_ANGLE(ANGLE)).

// See a threat ahead				
threatAhead(T) :-	(aircraft(DIR, DIST) &
					-ANGLE <= DIR &
					DIR <= ANGLE &
					TURN_ANGLE(ANGLE)).

// No threat seen
noThreatSeen(T) :-	(not aircraft(DIR, DIST)).

// Agent is moving
agentMoving(A) :- speedData(HEADDING, SPEED) & ~SPEED == 0.

// Agent is not moving
agentStationary(A) :- speedData(HEADDING, SPEED) & SPEED == 0.

 /*
  * Initial goals
  */
// Should be to find a threat
//!see(threat).
//!face(threat).
!follow(threat).

/*
 * Plans
 */
/*
// !see threat -> agent should not be moving
+!see(threat) : agentMoving(A)
	<- 	thrust(off);
		!see(threat).

// Implement the finding of a threat
+!see(threat) :	noThreatSeen(T)
	<-	turn(left);
		!see(threat).

+!see(threat) :	true
	<-	!see(threat).


// code duplication from the see threat plans
+!face(threat) :	(not aircraft(DIR, DIST))
	<-	turn(left);
		!face(threat).
	
// Turn to face a threat to the left
+!face(threat) :	(aircraft(DIR, DIST) &
					DIR < (-3.1459/16))
	<- 	turn(left);
		!face(threat).

// Turn to face a threat to the right
+!face(threat) :	(aircraft(DIR, DIST) &
					DIR > (3.1459/16))
	<- 	turn(right);
		!face(threat).

+!face(threat) :	true
	<- !face(threat).
*/
// Chase threat

// Follow the threat if one has been found
+!follow(threat)
	:	threatAhead(T) &
		agentStationary
	<-	thrust(on);
		!follow(threat).

// !see threat -> agent should not be moving, turn to find the threat
+!follow(threat)
	:	noThreatSeen(T)
	<-	thrust(off);
		turn(left);
		!follow(threat).
	
// Turn to face a threat to the left
+!follow(threat)
	:	threatLeft(T)
	<- 	turn(left);
		!follow(threat).

// Turn to face a threat to the right
+!follow(threat)
	:	threatRight(T)
	<-	turn(right);
		!follow(threat).

//+!follow(threat) :	true
//	<- !follow(threat).

		
// Default plans.
//+!see(threat).
//+!face(threat).
//+!follow(threat).

