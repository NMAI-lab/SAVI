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
	<- 	!carry_to(r2).
	
// Add the plan for delivering the garbage to r2
+!carry_to(R) :	garbage(p)
	<-	pick(garb);		// Pick it up
		!carry_to(R).	// carry garbage to r2

// If carrying and you don't see the disposal, look for it
+!carry_to(R) : not seeDisposal(Y)
	<- 	randMove;
		!carry_to(R).
		
// If carrying and you don't see the disposal, look for it
+!carry_to(R) : seeDisposal(Y) &
				Y \== p
	<- 	move(Y);
		!carry_to(R).
		
// If carrying and you are at the disposal, drop. Resume search
+!carry_to(R) : seeDisposal(p)
	<- 	drop(garb);
		!findGarbage(slots).
