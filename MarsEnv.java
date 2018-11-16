import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import java.util.logging.Logger;

public class MarsEnv extends Environment {

    public static final int GSize = 7; // grid size
    public static final int GARB  = 16; // garbage code in grid model

    public static final Term    ns = Literal.parseLiteral("next(slot)");
    
    public static final Term    pg = Literal.parseLiteral("pick(garb)");
    public static final Term    dg = Literal.parseLiteral("drop(garb)");
    
    public static final Term    bg = Literal.parseLiteral("burn(garb)");
    
    public static final Literal g1 = Literal.parseLiteral("garbage(r1)");
    public static final Literal g2 = Literal.parseLiteral("garbage(r2)");
    
    public static final Literal gp = Literal.parseLiteral("garbage(p)");
    public static final Literal gN = Literal.parseLiteral("garbage(n)");
    public static final Literal gS = Literal.parseLiteral("garbage(s)");
    public static final Literal gE = Literal.parseLiteral("garbage(e)");
    public static final Literal gW = Literal.parseLiteral("garbage(w)");
    
    public static final Literal dN = Literal.parseLiteral("disposal(n)");
    public static final Literal dS = Literal.parseLiteral("disposal(s)");
    public static final Literal dE = Literal.parseLiteral("disposal(e)");
    public static final Literal dW = Literal.parseLiteral("disposal(w)");
    public static final Literal dP = Literal.parseLiteral("disposal(p)");
        
	public static final Term 	poop = Literal.parseLiteral("maybePoop(garb)");

    static Logger logger = Logger.getLogger(MarsEnv.class.getName());

    private MarsModel model;
    private MarsView  view;

    @Override
    public void init(String[] args) {
        model = new MarsModel();
        view  = new MarsView(model);
        model.setView(view);
        updatePercepts();
    }
    
    // This is a hack.
    private int getAgIdBasedOnName(String agName) {
    	if(agName.equalsIgnoreCase("r1")) {
    		return 0;
    	} else if(agName.equalsIgnoreCase("r2")) {
    		return 1;
    	} else if(agName.equalsIgnoreCase("r3")) {
    		return 2;
    	} else {
    		return 3;
    	}
    }
    
    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag+" doing: "+ action);
        
        // get the agent id based on its name
        int agId = getAgIdBasedOnName(ag);
        
        try {
            if (action.equals(ns)) {
                model.nextSlot();
            } else if (action.getFunctor().equals("move_towards")) {
				int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.moveTowards(agId, x,y);
            } else if (action.equals(pg)) {
                model.pickGarb(agId);
            } else if (action.equals(dg)) {
                model.dropGarb(agId);
            } else if (action.equals(bg)) {
                model.burnGarb();
			} else if (action.getFunctor().equals("randMove")) {
                model.randMove(agId);
            } else if (action.equals(poop)) {
                model.maybePoop();
			} else if (action.getFunctor().equals("move")) {
				Literal directionAsLiteral = (Literal)action.getTerm(0);
				String direction = directionAsLiteral.getFunctor();
				model.move(direction, agId);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updatePercepts();

        try {
            Thread.sleep(200);
        } catch (Exception e) {}
        informAgsEnvironmentChanged();
        return true;
    }

    /** creates the agents perception based on the MarsModel */
    void updatePercepts() {
        clearPercepts();

        Location r1Loc = model.getAgPos(0);
        Location r2Loc = model.getAgPos(1);
        Location r3Loc = model.getAgPos(2);
		Location smarterR1Loc = model.getAgPos(3);

        Literal pos1 = Literal.parseLiteral("pos(r1," + r1Loc.x + "," + r1Loc.y + ")");
        Literal pos2 = Literal.parseLiteral("pos(r2," + r2Loc.x + "," + r2Loc.y + ")");
        Literal pos3 = Literal.parseLiteral("pos(r3," + r3Loc.x + "," + r3Loc.y + ")");
		Literal posSR1 = Literal.parseLiteral("pos(smarterR1," + smarterR1Loc.x + "," + smarterR1Loc.y + ")");
		
        addPercept(pos1);
        addPercept(pos2);
        addPercept(pos3);
		addPercept(posSR1);
		
        if (model.hasObject(GARB, r1Loc)) {
            addPercept(g1);
        }
        if (model.hasObject(GARB, r2Loc)) {
            addPercept(g2);
        }
        if (model.hasObject(GARB, smarterR1Loc)) {
            addPercept(gp);
        }
        
        checkSeeGarbageNorth(3);
        checkSeeGarbageSouth(3);
        checkSeeGarbageEast(3);
        checkSeeGarbageWest(3);
        checkSeeDisposal(3);
    }
    
    void checkSeeDisposal(int id) {
    	Location myLocation = model.getAgPos(id);
    	Location disposalLocation = model.getAgPos(1);
    	
    	// Check if the disposal is at my location
    	//if ((myLocation.x == disposalLocation.x) && (myLocation.y == disposalLocation.y)) {
    	//	addPercept(dH);
    	//}
    	
    	// Check if the disposal is North
    	if ((myLocation.x == disposalLocation.x) && (myLocation.y > disposalLocation.y)) {
    		addPercept(dN);
    	}
    	
    	// Check if the disposal is South
    	if ((myLocation.x == disposalLocation.x) && (myLocation.y < disposalLocation.y)) {
    		addPercept(dS);
    	}
    	
    	// Check if the disposal is East
    	if ((myLocation.x < disposalLocation.x) && (myLocation.y == disposalLocation.y)) {
    		addPercept(dE);
    	}
    	
    	// Check if the disposal is West
    	if ((myLocation.x > disposalLocation.x) && (myLocation.y == disposalLocation.y)) {
    		addPercept(dW);
    	}
    	
    	// Check if the disposal is here
    	if ((myLocation.x == disposalLocation.x) && (myLocation.y == disposalLocation.y)) {
    		addPercept(dP);
    	}
    }
    
    void checkSeeGarbageNorth(int id) {
    	Location position = model.getAgPos(id);
    	for (int locationDim = position.y; model.mapPositionExists(position.x, locationDim); locationDim--) {
    		Location checkLocation = new Location(position.x, locationDim);
    		if (model.hasObject(GARB, checkLocation)){
    			addPercept(gN);
    			return;
    		}
    	}
    }
    
    void checkSeeGarbageSouth(int id) {
    	Location position = model.getAgPos(id);
    	for (int locationDim = position.y; model.mapPositionExists(position.x, locationDim); locationDim++) {
    		Location checkLocation = new Location(position.x, locationDim);
    		if (model.hasObject(GARB, checkLocation)){
    			addPercept(gS);
    			return;
    		}
    	}
    }
    
    void checkSeeGarbageEast(int id) {
    	Location position = model.getAgPos(id);
    	for (int locationDim = position.x; model.mapPositionExists(locationDim, position.y); locationDim++) {
    		Location checkLocation = new Location(locationDim, position.y);
    		if (model.hasObject(GARB, checkLocation)){
    			addPercept(gE);
    			return;
    		}
    	}
    }
    
    void checkSeeGarbageWest(int id) {
    	Location position = model.getAgPos(id);
    	for (int locationDim = position.x; model.mapPositionExists(locationDim, position.y); locationDim--) {
    		Location checkLocation = new Location(locationDim, position.y);
    		if (model.hasObject(GARB, checkLocation)){
    			addPercept(gW);
    			return;
    		}
    	}
    }
    

    class MarsModel extends GridWorldModel {

        public static final int MErr = 2; // max error in pick garb
        int nerr; // number of tries of pick garb
        boolean agHasGarb[] = new boolean[]{false, false, false, false}; // whether agent is carrying garbage or not

        Random random = new Random(System.currentTimeMillis());

        private MarsModel() {
            super(GSize, GSize, 4); // 4 agents

            // initial location of agents
            try {
            	// r1
                setAgPos(0, 0, 0);
                
                // r2
                Location r2Loc = new Location(GSize/2, GSize/2);
                setAgPos(1, r2Loc);

                // r3
                Location r3Loc = new Location(0,1);//(GSize-1, GSize-1); //agent 3 starts bottom right
				setAgPos(2, r3Loc);
				
				// r4, also called smarterR1
				Location smarterR1Loc = new Location(5,5);
				setAgPos(3, smarterR1Loc);
				
            } catch (Exception e) {
                e.printStackTrace();
            }

            // initial location of garbage
            add(GARB, 3, 0);
            add(GARB, GSize-1, 0);
            add(GARB, 1, 2);
            add(GARB, 0, GSize-2);
            add(GARB, GSize-1, GSize-1);
        }

        void nextSlot() throws Exception {
            Location r1 = getAgPos(0);
            r1.x++;
            if (r1.x == getWidth()) {
                r1.x = 0;
                r1.y++;
            }
            // finished searching the whole grid
            if (r1.y == getHeight()) {
                return;
            }
            setAgPos(0, r1);
            //setAgPos(1, getAgPos(1)); // just to draw it in the view
        }

        void moveTowards(int id, int x, int y) throws Exception {
            Location position = getAgPos(id);
            if (position.x < x)
                moveEast(id);
            else if (position.x > x)
                moveWest(id);
            else if (position.y < y)
                moveSouth(id);
            else if (position.y > y)
                moveNorth(id);
        }
		
        
		/**
		 * Implementation of the move action
		 */
		void move(String direction, int id) throws Exception {
			char directionFirstChar = direction.charAt(0);
			switch(directionFirstChar) {
			case 'n': 	moveNorth(id);
						break;
			case 's':	moveSouth(id);
						break;
			case 'e': 	moveEast(id);
						break;
			case 'w': 	moveWest(id);
						break;
			}
		}
        
		
		/**
		 * Implementation of the moveWest action
		 */
		void moveWest(int id) throws Exception {
			Location position = getAgPos(id);
			move(id, position.x - 1, position.y);
		}
		
		/**
		 * Implementation of the moveEast action
		 */
		void moveEast(int id) throws Exception {
			Location position = getAgPos(id);
			move(id, position.x + 1, position.y);
		}

		/**
		 * Implementation of the moveNorth action
		 */
		void moveNorth(int id) throws Exception {
			Location position = getAgPos(id);
			move(id, position.x, position.y - 1);
		}

		/**
		 * Implementation of the moveSouth action
		 */
		void moveSouth(int id) throws Exception {
			Location position = getAgPos(id);
			move(id, position.x, position.y + 1);
		}		
		
		/**
		 * Moves agent with corresponding id to specified x and y location if 
		 * possible.
		 */
		void move(int id, int x, int y) {
			// Check for collision and move the agent
			if (movePossible(id, x, y)) {
				Location position = new Location(x,y);
				setAgPos(id, position);
				
				// Redraw agent 1 in case there was an overlap (so it doesn't disappear)
				setAgPos(1, getAgPos(1));
			}
		}
		
		/**
		 * Checks to make sure that the proposed move is possible
		 */
		boolean movePossible(int myID, int x, int y) {
			return (!checkCollision(myID, x, y) && mapPositionExists(x, y));
		}
		
		/**
		 * Returns true if R1 and R3 will collide using proposed new position x and y
		 */
		boolean checkCollision(int myID, int x, int y) {
			
			// This would be better to get from the environment class elsewhere (change in the future)
			int disposalID = 1;				// We are allowed to collide with the disposal
			int numAgents = 4;
			
			// Check all the applicable other agents
			for (int otherAgID = 0; otherAgID < numAgents; otherAgID++) {
				if ((otherAgID != disposalID) && (otherAgID != myID)) {		// Collisions allowed with self and disposal
				
					// Other agent's position
					Location otherPosition = getAgPos(otherAgID);
					
					// Check for collision and return result
					if (otherPosition.x == x && otherPosition.y == y) {
						logger.info("Move not possible due to collision!");
						return true;
					}
				}
			}
			return false;
		}
		
		/**
		 * Checks to make sure that a proposed location is in fact on the map.
		 */
		boolean mapPositionExists(int x, int y) {
			// Get the max and min values for x and y
			int minX = 0;
			int maxX = getWidth()-1;
			int minY = 0;
			int maxY = getHeight()-1;
			
			// Check to make sure that x and y is within the limits
			if ((minX <= x) && (x <= maxX) && (minY <= y) && (y <= maxY)) {
				return true;
			} else {
				logger.info("Move not possible, no map position there!");
				return false;
			}
		}

        void pickGarb(int id) {
            // r1 location has garbage
            if (model.hasObject(GARB, getAgPos(id))) {
                // sometimes the "picking" action doesn't work
                // but never more than MErr times
                if (random.nextBoolean() || nerr == 0) {//MErr) {	// Disable randomness for now
                    remove(GARB, getAgPos(id));
                    nerr = 0;
                    agHasGarb[id] = true;
                } else {
                    nerr++;
                }
            }
        }
        
        void dropGarb(int id) {
            if (agHasGarb[id]) {
                agHasGarb[id] = false;
                add(GARB, getAgPos(id));
            }
        }
        void burnGarb() {
            // r2 location has garbage
            if (model.hasObject(GARB, getAgPos(1))) {
                remove(GARB, getAgPos(1));
            }
        }
		
		void randMove(int id) throws Exception{
			int numDirections = 4;
			int direction = random.nextInt(numDirections);
			if(direction == 0) {
				moveNorth(id);
			} else if(direction == 1) {
				moveSouth(id);
			} else if(direction == 2) {
				moveEast(id);
			} else {
				moveWest(id);
			}
		}
		
		void maybePoop(){ //with a probability of .1, agent 3 drops some garbage in its location
			if(!model.hasObject(GARB, getAgPos(2)) && random.nextInt(10) ==0){
				add(GARB, getAgPos(2)); 
			}
		}
    }

    class MarsView extends GridWorldView {

        public MarsView(MarsModel model) {
            super(model, "Mars World", 600);
            defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
            setVisible(true);
            repaint();
        }

        /** draw application objects */
        @Override
        public void draw(Graphics g, int x, int y, int object) {
            switch (object) {
            case MarsEnv.GARB:
                drawGarb(g, x, y);
                break;
            }
        }

        @Override
        public void drawAgent(Graphics g, int x, int y, Color c, int id) {
            String label = "R"+(id+1);
            c = Color.blue;
            if (id == 0) {
                c = Color.yellow;
                if (((MarsModel)model).agHasGarb[id]) {
                    label += " - G";
                    c = Color.orange;
                }
            } else if (id == 2) {
                c = Color.green;
            }
            super.drawAgent(g, x, y, c, -1);
            if (id == 0) {
                g.setColor(Color.black);
            } else {
                g.setColor(Color.white);
            }
            super.drawString(g, x, y, defaultFont, label);

			// To avoid flicker issue with Windows 10, don't repaint.
			String osName = System.getProperty("os.name");
			if (!(osName.equals("Windows 10") || osName.equals("Linux"))) {
				repaint();
			} 
        }

        public void drawGarb(Graphics g, int x, int y) {
            super.drawObstacle(g, x, y);
            g.setColor(Color.white);
            drawString(g, x, y, defaultFont, "G");
        }

    }
}
