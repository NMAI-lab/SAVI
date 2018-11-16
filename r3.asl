// mars robot 1

/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(r3,X,Y).


/* Initial goal */

!explore(slots).

/* Plans */

+!explore(slots) : true
   <- //randMove;
   	  //maybePoop(garb);
      !explore(slots).
