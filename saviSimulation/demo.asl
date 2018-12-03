/*
 * Simple agent behaviour for the SAVI project
 * @author	Patrick Gavigan
 * @date	28 November 2018
 */

/*
 * Set initial beliefs
 */
  // None for now
 
 /*
  * Initial goals
  */
// Should be to find a threat
!follow(threat). 
//!follow(threat).	// Follow threats

/*
 * Plans
 */

// Implement the finding of a threat
 +!find(threat) :	(not aircraft(X, Y))
	<-	turn(left);
		!find(threat).

// Finding threat successded. Swicth to facing the threat
 +!find(threat):	(aircraft(DIR, DIST))
	<-	!face(threat).
 
// Turn to face a threat to the left
+!face(threat) :	(aircraft(DIR, DIST) &
					DIR < -PI/4)
	<- 	turn(right);
		!face(threat).

// Turn to face a threat to the right
+!face(threat) :	(aircraft(DIR, DIST) &
					DIR > PI/4)
	<- 	turn(left);
		!face(threat).

// Facing the threat, follow it
+!face(threat) :	(aircraft(DIR, DIST) &
					-PI/4 < DIR &
					DIR < PI/4)
	<- 	!follow(threat).

// Trying to face a threat, can't see one
+!face(threat) :	(not aircraft(DIR, DIST))
	<- !find(threat).
	
 // Follow the threat if one has been found
+!follow(threat) :	aircraft(DIR, DIST) &
					-PI/4 < DIR &
					DIR < PI/4 //&
					//speedData(HEADDING, SPEED) &
					//SPEED == 0
   <- thrust(on);
      !follow(threat).
	  
// Can't see the threat anymore, try to find it again
+!follow(threat) :	not(aircraft(DIR, DIST)) |
					(aircraft(DIR, DIST) &
					not(-PI/4 < DIR &
					DIR < PI/4))
	<- 	thrust(off);
		!face(threat).

// Default goal - find a threat.
+!follow(threat).

