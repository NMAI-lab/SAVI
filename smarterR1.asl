/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(r4,X,Y).


/* Initial goal */

!findGarbage(slots).

/* Plans */

+!findGarbage(slots) 
	: 	not garbage(N) &
	  	not garbage(S) &
	  	not garbage(E) &
		not garbage(W)
   <- randMove;
      !findGarbage(slots).

+!findGarbage(slots) : garbage(N)
	<-	moveNorth;
		!findGarbage(slots).
		
+!findGarbage(slots) : garbage(S)
	<-	moveSouth;
		!findGarbage(slots).
		
+!findGarbage(slots) : garbage(E)
	<-	moveEast;
		!findGarbage(slots).
		
+!findGarbage(slots) : garbage(W)
	<-	moveWest;
		!findGarbage(slots).
	