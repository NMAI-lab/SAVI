// mars robot 1

/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(r3,X,Y).


/* Initial goal */

!explore(slots).

/* Plans */

+!explore(slots) : true
   <- randMove(slot);
   	  maybePoop(garb);
      !explore(slots).
	  
//+!randmove(slot): true
   //<- move_towards(2,0,0).

//+!randmove(slot): true
   //<- ?pos(r1,X,Y); 
   //move_towards(2,X,Y).
   

