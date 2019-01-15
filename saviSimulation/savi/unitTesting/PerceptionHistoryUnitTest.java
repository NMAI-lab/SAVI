package savi.unitTesting;

import java.util.ArrayList;
import java.util.List;

import savi.jason_processing.Perception;
import savi.jason_processing.PerceptionHistory;
import savi.jason_processing.PerceptionSnapshot;

public class PerceptionHistoryUnitTest {
	
	/**
	 * Run all of the unit tests
	 * @param verbose
	 * @return
	 */
	public static boolean unitTest(boolean verbose) {
		boolean testOK = true;
		boolean testResult = true;
		
		// Make a PerceptionHistory
		PerceptionHistory history = new PerceptionHistory();
		
		// Setup the first update.
		List<Double> parameterList = new ArrayList<Double>();
		parameterList.add(1.0);
		parameterList.add(2.0);
		Perception testPerception1 = new Perception("perceptName", 1, parameterList);
	
		parameterList = new ArrayList<Double>();
		parameterList.add(5.0);
		parameterList.add(2.0);
		Perception testPerception2 = new Perception("perceptName", 2, parameterList);
		
		PerceptionSnapshot firstUpdate = new PerceptionSnapshot();
		firstUpdate.addPerception(testPerception1);
		firstUpdate.addPerception(testPerception2);
		
		String firstUpdateString = history.updatePerceptions(firstUpdate).toString();
		testResult = firstUpdateString.equals(firstUpdate.toString());
		UnitTester.reportResult("PerceptHistory class - First update test", testResult, verbose);
		testOK &= testResult;
		
		// Make the second update - Very similar, so there will be no change
		parameterList = new ArrayList<Double>();
		parameterList.add(1.01);
		parameterList.add(2.0);
		Perception similarPerception = new Perception("perceptName", 0, parameterList);
		
		// Make the third update
		parameterList = new ArrayList<Double>();
		parameterList.add(1.21);
		parameterList.add(2.0);
		Perception differentPerception = new Perception("perceptName", 0, parameterList);		

		PerceptionSnapshot secondUpdate = new PerceptionSnapshot();
		secondUpdate.addPerception(similarPerception);
		secondUpdate.addPerception(differentPerception);
		
		String secondUpdateString = history.updatePerceptions(secondUpdate).toString();
		testResult = secondUpdateString.equals("[perceptnamelost(5,2), perceptname(1.21,2)]");
		UnitTester.reportResult("PerceptHistory class - Second update test (all same or similar)", testResult, verbose);
		testOK &= testResult;
		
		return testOK;
	}
}
