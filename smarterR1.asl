/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(H,X,Y).


/* Initial goal */

!findGarbage(slots).

/* Plans */

+!findGarbage(slots) 
	: 	not garbage(X)
   <- randMove;
      !findGarbage(slots).

@lg[atomic]
+garbage(h) : not .desire(carry_to(r2))
   <-	!carry_to(r2)
   		!find(r2).

+!findGarbage(slots) : 	garbage(X)
	<-	move(X);
		!findGarbage(slots).     

+!carry_to(R)
   <- // carry garbage to r2
      !take(garb,R);

      // goes back and continue to check
      !findGarbage(slots).
