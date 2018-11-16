/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(H,X,Y).


/* Initial goal */

!findGarbage(slots).

/* Plans */

// Don't see any garbage, look for some
+!findGarbage(slots) 
	: 	(not garbage(X) &
			(X \== n |
			 X \== s |
			 X \== e |
			 X \== w)
			) |
		(garbage(Y) &
			(Y == r1 |
			 Y == r2))
   <- randMove;
      !findGarbage(slots).

// See garbage, move toward it
+!findGarbage(slots) : 	garbage(X) & not garbage(p) &
						X \== p &
						X \== r1 &
						X \== r2
	<-	move(X);
		!findGarbage(slots).
+!findgarbage(slots).
	
// Notice garbage at my location, pick up and add desire to deliver to r2	
@lg[atomic]
+garbage(p) : 	not .desire(carry_to(r2)) &
				not disposal(p)
	<- 	pick(garb);
		!carry_to(r2).
	
// If carrying and you don't see the disposal, look for it
+!carry_to(R) : not disposal(Y)
	<- 	randMove;
		!carry_to(R).
		
// If carrying and you see the disposal, go toward it
+!carry_to(R) : disposal(Y) &
				Y \== p
	<- 	move(Y);
		!carry_to(R).
		
// If carrying and you are at the disposal, drop. Resume search
+!carry_to(R) : disposal(p)
	<- 	drop(garb);
		!findGarbage(slots).
