/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(r1,X,Y).

/* Initial goal */

!explore(slots).

/* Plans */

+!explore(slots) : true
	<-	moveEast(3);
		moveEast(3);
		moveSouth(3);
		moveSouth(3);
		moveWest(3);
		moveWest(3);
		moveNorth(3);
		moveNorth(3);
		!explore(slots).
