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
	public static final Term    rnd = Literal.parseLiteral("randMove(slot)");
	public static final Term poop = Literal.parseLiteral("maybePoop(garb)");

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

    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag+" doing: "+ action);
        try {
            if (action.equals(ns)) {
                model.nextSlot();
            } else if (action.getFunctor().equals("move_towards")) {
                int id = (int)((NumberTerm)action.getTerm(0)).solve(); //add agent id to action command
				int x = (int)((NumberTerm)action.getTerm(1)).solve();
                int y = (int)((NumberTerm)action.getTerm(2)).solve();
                model.moveTowards(id, x,y);
            } else if (action.equals(pg)) {
                model.pickGarb();
            } else if (action.equals(dg)) {
                model.dropGarb();
            } else if (action.equals(bg)) {
                model.burnGarb();
			} else if (action.equals(rnd)) {
                model.randMove();
            } else if (action.equals(poop)) {
                model.maybePoop();

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

        Literal pos1 = Literal.parseLiteral("pos(r1," + r1Loc.x + "," + r1Loc.y + ")");
        Literal pos2 = Literal.parseLiteral("pos(r2," + r2Loc.x + "," + r2Loc.y + ")");
        Literal pos3 = Literal.parseLiteral("pos(r3," + r3Loc.x + "," + r3Loc.y + ")");

        addPercept(pos1);
        addPercept(pos2);
        addPercept(pos3);

        if (model.hasObject(GARB, r1Loc)) {
            addPercept(g1);
        }
        if (model.hasObject(GARB, r2Loc)) {
            addPercept(g2);
        }
    }

    class MarsModel extends GridWorldModel {

        public static final int MErr = 2; // max error in pick garb
        int nerr; // number of tries of pick garb
        boolean r1HasGarb = false; // whether r1 is carrying garbage or not

        Random random = new Random(System.currentTimeMillis());

        private MarsModel() {
            super(GSize, GSize, 3); // 3 agents

            // initial location of agents
            try {
                setAgPos(0, 0, 0);

                Location r2Loc = new Location(GSize/2, GSize/2);
                setAgPos(1, r2Loc);

                Location r3Loc = new Location(0,1);//(GSize-1, GSize-1); //agent 3 starts bottom right
				setAgPos(2, r3Loc);
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
			int newX = position.x;
			int newY = position.y;
            if (position.x < x)
                newX++;
            else if (position.x > x)
                newX--;
            else if (position.y < y)
                newY++;
            else if (position.y > y)
                newY--;
		
			// Check for collision and move the agent
			if (movePossible(id, newX, newY)) {
				position.x = newX;
				position.y = newY;
				setAgPos(id, position);
				
				// Redraw agent 1 in case there was an overlap (so it doesn't disapear)
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
			// Get the two agent IDs, hard code agent IDs for now
			int otherID = 0;
			if (myID == 0) {
				otherID = 2;
			} else {
				otherID = 0;
			}
			// Other agent's position
			Location otherPosition = getAgPos(otherID);
			
			// Check for collision and return result
			if (otherPosition.x == x && otherPosition.y == y) {
				return true;
			} else {
				System.out.println("Move not possible due to collision!");
				return false;
			}
		}
		
		/**
		 * Checks to make sure that a proposed location is infact on the map.
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
				System.out.println("Move not possible, no map position there!");
				return false;
			}
		}

        void pickGarb() {
            // r1 location has garbage
            if (model.hasObject(GARB, getAgPos(0))) {
                // sometimes the "picking" action doesn't work
                // but never more than MErr times
                if (random.nextBoolean() || nerr == MErr) {
                    remove(GARB, getAgPos(0));
                    nerr = 0;
                    r1HasGarb = true;
                } else {
                    nerr++;
                }
            }
        }
        void dropGarb() {
            if (r1HasGarb) {
                r1HasGarb = false;
                add(GARB, getAgPos(0));
            }
        }
        void burnGarb() {
            // r2 location has garbage
            if (model.hasObject(GARB, getAgPos(1))) {
                remove(GARB, getAgPos(1));
            }
        }
		
		void randMove() throws Exception{ //agent 3 moving randomly but within bounds
			int agentID = 2;
			int numDirections = 4;
			Location r3 = getAgPos(agentID);
			int newX = r3.x;
			int newY = r3.y;
			int direction = random.nextInt(numDirections);
			switch (direction) {
            case 0: if (newX < getWidth()-1) newX++;
                    break;
            case 1:  if (newY < getHeight()-1) newY++;
                     break;
            case 2:  if (newX > 0) newX--;
                     break;
            case 3:  if (newY > 0) newY--;
                     break;
			}
			moveTowards(agentID, newX, newY);
            //setAgPos(2, r3);

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
                if (((MarsModel)model).r1HasGarb) {
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
