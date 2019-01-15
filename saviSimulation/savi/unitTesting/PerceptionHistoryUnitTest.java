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
		PerceptionSnapshot testSnapshot = new PerceptionSnapshot();

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
		//System.out.println(firstUpdateString);
		
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
		secondUpdate.addPerception(testPerception1);
		secondUpdate.addPerception(testPerception2);
		
		String secondUpdateString = history.updatePerceptions(firstUpdate).toString();
		testResult = secondUpdateString.equals("[]");
		UnitTester.reportResult("PerceptHistory class - Second update test (all same or similar)", testResult, verbose);
		testOK &= testResult;
		
		// Third update - combination of similar, missing, and different
		//System.out.println(secondUpdateString);
		
		/*
		// Make the final update (empty)
		
		
		// Check the size of the perception list
		testResult = (testSnapshot.getPerceptionList().size() == 2);
		UnitTester.reportResult("PerceptSnapshot class - Perception snapshot percept list size check", testResult, verbose);
		testOK &= testResult;
		
		// Check the perception list - latest version
		testResult = (testSnapshot.getLatestVersion() == 2);
		UnitTester.reportResult("PerceptSnapshot class - Perception snapshot version check", testResult, verbose);
		testOK &= testResult;
		
		
		
		// Test the case where there is an empty PerceptionSnapshot involved.
		//testResult = PerceptionSnapshotUnitTest.emptyTest(verbose);
		//testOK &= testResult;
		
		// Test a case where the PerceptionSnapshot is not empty
		//testResult = PerceptionSnapshotUnitTest.contentTest(verbose);
		//testOK &= testResult;
		*/
		
		// Return result
		return testOK;
	}
}
