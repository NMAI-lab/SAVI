/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(H,X,Y).


/* Initial goal */

!findGarbage(slots).

/* Plans */

// Don't see any garbage, look for some
+!findGarbage(slots) 
	: 	not garbage(X)
   <- randMove;
      !findGarbage(slots).

// See garbage, move toward it
+!findGarbage(slots) : 	garbage(X) &
						X \== h &
						X \== p
	<-	move(X);
		!findGarbage(slots).
		
// Pick up garbage at my location, add goals of holding garbage and delivering garbage
+!findGarbage(slots) : 	garbage(X) &
						X == h
	<-	pick(garb);
		!hold(garb)
		!deliver(garb).
		
// Ensure that agent is holding garbage
+!hold(garb) :	not garbage(X) |
				(garbage(X) & X \== p)
	<-	!findGarbage(slots). 	

// Have garbage, look for the disposal
+!deliver(garb) : 	garbage(X) &
					X == p &
					not seeDisposal(Y)
	<- 	randMove
		!deliver(garb).
		
// Have garbage, see the disposal, move towards it
+!deliver(garb) : 	garbage(X) &
					X == p &
					seeDisposal(Y) &
					Y \== h
	<- 	move(Y)
		!deliver(garb).
		
// Have garbage, at location of the disposal
+!deliver(garb) : 	garbage(X) &
					X == p &
					seeDisposal(Y) &
					Y == h
	<- 	drop(garb)
		!findGarbage(slots).
		
