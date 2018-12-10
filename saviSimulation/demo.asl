/*
 * Simple agent behaviour for the SAVI project
 * @author	Patrick Gavigan
 * @date	28 November 2018
 */

/*
 * Set initial beliefs
 */
//PI :- 3.14159265359.
 //TURN_ANGLE :- PI/16.
//threat(aircraft(DIR, DIST))	// Aircraft are threats
//followable(threat(X))	// Threats are folowable
//facable(followable(X))	// Followable objects are faceable
//findable(faceable(X))	// Faceable objects are findable
 
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

// !see threat -> agent should not be moving
+!see(threat) : speedData(HEADDING, SPEED) &
				~SPEED == 0
	<- 	thrust(off);
		!see(threat).

// Implement the finding of a threat
+!see(threat) :	(not aircraft(DIR, DIST))
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

// Chase threat

// Follow the threat if one has been found
+!follow(threat) :	aircraft(DIR, DIST) &
					-3.1459/16 < DIR &
					DIR < 3.1459/16 &
					speedData(HEADDING, SPEED) &
					SPEED == 0
   <- thrust(on);
      !follow(threat).

// !see threat -> agent should not be moving, turn to find the threat
+!follow(threat) :	(not aircraft(DIR, DIST))
	<-	thrust(off);
		turn(left);
		!follow(threat).
	
// Turn to face a threat to the left
+!follow(threat) :	(aircraft(DIR, DIST) &
					DIR < (-3.1459/16))
	<- 	turn(left);
		!follow(threat).

// Turn to face a threat to the right
+!follow(threat) :	(aircraft(DIR, DIST) &
					DIR > (3.1459/16))
	<- 	turn(right);
		!follow(threat).

+!follow(threat) :	true
	<- !follow(threat).

		
// Default plans.
+!see(threat).
+!face(threat).
+!follow(threat).

