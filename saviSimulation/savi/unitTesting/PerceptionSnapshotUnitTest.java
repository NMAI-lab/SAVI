package savi.unitTesting;

import java.util.ArrayList;
import java.util.List;

import savi.jason_processing.Perception;
import savi.jason_processing.PerceptionSnapshot;

public class PerceptionSnapshotUnitTest {
	
	public static boolean unitTest(boolean verbose) {
		boolean testOK = true;
		boolean testResult = true;
		
		// Constructor
		PerceptionSnapshot testSnapshot = new PerceptionSnapshot();

		// addPerception
		testSnapshot.addPerception(null);
		
		// Check the empty perception list
		testResult = (testSnapshot.getPerceptionList().size() == 0);
		UnitTester.reportResult("PerceptSnapshot class - Empty snapshot percept list check", testResult, verbose);
		testOK = testOK && testResult;
		
		// Check the empty perception list - latest version
		testResult = (testSnapshot.getLatestVersion() == -1);
		UnitTester.reportResult("PerceptSnapshot class - Empty snapshot version check", testResult, verbose);
		testOK = testOK && testResult;
		
		// Check the empty perception list - list of literals
		testResult = (testSnapshot.getLiterals().isEmpty());
		UnitTester.reportResult("PerceptSnapshot class - Empty list of Literals", testResult, verbose);
		testOK = testOK && testResult;
		
		// Check the empty perception list - toString
		testResult = testSnapshot.toString().equals(new String("[]"));
		UnitTester.reportResult("PerceptSnapshot class - Empty String ", testResult, verbose);
		testOK = testOK && testResult;
		
		// Check for similar perception in an empty snapshot
		List<Double> parameterList = new ArrayList<Double>();
		parameterList.add(8.0);
		testResult = (testSnapshot.pullSimilarPerception(new Perception("perceptName", 0, parameterList)) == null);
		UnitTester.reportResult("PerceptSnapshot class - Empty String ", testResult, verbose);
		testOK = testOK && testResult;
		
		// Copy constructor
		
		// addPerception
		
		// addPerceptionsFromSnapshot
		
		// ArrayList<Perception> getPerceptionList
		
		// getLatestVersion
		
		// pullSimilarPerception
		
		// getLiterals
		
		return testOK;
	}
}
