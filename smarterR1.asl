/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(r1,X,Y).

/* Initial goal */

!explore(slots).

/* Plans */

+!explore(slots) : true
	<-	moveRight(3);
		!explore(slots).
		/*moveRight(3);
		moveDown(3);
		moveDown(3);
		moveLeft(3);
		moveLeft(3);
		moveUp(3);
		moveUp(3);*/
