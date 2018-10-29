/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(r4,X,Y).


/* Initial goal */

!explore(slots).

/* Plans */

+!explore(slots) : true
   <- randMove(3);
      !explore(slots).
