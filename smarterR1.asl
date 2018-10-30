/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(H,X,Y).


/* Initial goal */

!findGarbage(slots).

/* Plans */

+!findGarbage(slots) 
	: 	not garbage(N) &
	  	not garbage(S) &
	  	not garbage(E) &
		not garbage(W) &
		not garbage(H)
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
	
@lg[atomic]
+garbage(H) : not .desire(carry_to(r2))
   <-	!carry_to(r2)
   		!find(r2).
   
+!carry_to(R)
   <- // carry garbage to r2
      !take(garb,R);

      // goes back and continue to check
      !findGarbage(slots).
