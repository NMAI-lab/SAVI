/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(H,X,Y).


/* Initial goal */

!findGarbage(slots).

/* Plans */

// Don't see any garbage, look for some
+!findGarbage(slots) 
	: 	not garbage(X) |
		(garbage(Y) &
			(Y == r1 |
			 Y == r2))
   <- randMove;
      !findGarbage(slots).

// See garbage, move toward it
+!findGarbage(slots) : 	garbage(X) &
						X \== p &
						X \== r1 &
						X \== r2
	<-	move(X);
		!findGarbage(slots).
	
// Notice garbage at my location, desire to deliver to r2	
@lg[atomic]
+garbage(p) : not .desire(carry_to(r2))
	<- !carry_to(r2).
	
// Add the plan for delivering the garbage to r2
+!carry_to(R)
	<- !take(garb,R).	// carry garbage to r2
	
// If there is garbage where I am located, pick it up, unless I'm at the disposal (L), then drop it
+!take(S,L) : true
   <- !ensure_pick(S);
      !at(L);
      drop(S);
      !findGarbage(slots).

// Pickup the garbage if it is at my location
+!ensure_pick(S) : garbage(p)
   <- pick(garb);
      !ensure_pick(S).
+!ensure_pick(_).

// Have garbage, look for the disposal      
+!take(garb,R) :	not seeDisposal(Y)
	<- 	randMove
		!take(garb,R).
		
// Have garbage, see the disposal, move towards it
+!take(garb,R) :	seeDisposal(Y)
	<- 	move(Y);
		!take(garb,R).
