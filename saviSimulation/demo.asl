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
!find(threat). 
//!follow(threat).	// Follow threats

/*
 * Plans
 */

// Implement the finding of a threat
 +!find(threat) :	(not aircraft(DIR, DIST)) &
 					speedData(HEADDING, SPEED) &
					SPEED > 0
	<-	thrust(off)
		turn(left)
		!find(threat).
		
 +!find(threat) :	(not aircraft(DIR, DIST)) &
 					speedData(HEADDING, SPEED) &
					SPEED == 0
	<-	turn(left)
		!find(threat).

// Finding threat successded. Swicth to following the threat
 +!find(threat) : (aircraft(DIR, DIST))
	<-	!follow(threat).
 
 // Follow the threat if one has been found
+!follow(threat) :	aircraft(DIR, DIST) &
					speedData(HEADDING, SPEED) &
					SPEED == 0
   <- thrust(on);
      !follow(threat).
	  
// Can't see the threat anymore, try to find it again
+!follow(threat) :	not(aircraft(DIR, DIST))
	<- !find(threat).

// Default goal - find a threat.
+!find(threat).
//+!follow(threat).



