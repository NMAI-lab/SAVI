package savi.savimason;

import sim.engine.SimState;
import sim.util.*;
import sim.field.continuous.*;

public class Students extends SimState {

	public Continuous2D yard = new Continuous2D(1.0,100,100);
	public int numStudents = 50;

	public Students(long seed)
	{
		super(seed);
	}

	public void start()
	{
		super.start();
		// clear the yard
		yard.clear();
		// clear the buddies
		//buddies.clear();
		// add some students to the yard
		for(int i = 0; i < numStudents; i++)
		{
			Student student = new Student();
			yard.setObjectLocation(student,
					new Double2D(yard.getWidth() * (0.5 + random.nextDouble() - 0.5),
							yard.getHeight() * (0.5 + random.nextDouble() - 0.5)));
			
			schedule.scheduleRepeating(student);
		}
	}

	public static void main(String[] args)
	{
		doLoop(Students.class, args);
		System.exit(0);
	}

}
