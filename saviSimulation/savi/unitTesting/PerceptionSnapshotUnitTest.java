package savi.unitTesting;

import java.util.ArrayList;
import java.util.List;

import jason.asSyntax.Literal;
import savi.jason_processing.Perception;
import savi.jason_processing.PerceptionSnapshot;

public class PerceptionSnapshotUnitTest {
	
	/**
	 * Test the case where there is an empty PerceptionSnapshot
	 * @param verbose
	 * @return
	 */
	public static boolean emptyTest(boolean verbose) {
		boolean testOK = true;
		boolean testResult = true;
		
		// Constructor
		PerceptionSnapshot testSnapshot = new PerceptionSnapshot();

		// addPerception
		testSnapshot.addPerception(null);
		
		// Check the empty perception list
		testResult = (testSnapshot.getPerceptionList().size() == 0);
		UnitTester.reportResult("PerceptSnapshot class - Empty snapshot percept list check", testResult, verbose);
		testOK &= testResult;
		
		// Check the empty perception list - latest version
		testResult = (testSnapshot.getLatestVersion() == -1);
		UnitTester.reportResult("PerceptSnapshot class - Empty snapshot version check", testResult, verbose);
		testOK &= testResult;
		
		// Check the empty perception list - list of literals
		testResult = (testSnapshot.getLiterals().isEmpty());
		UnitTester.reportResult("PerceptSnapshot class - Empty list of Literals", testResult, verbose);
		testOK &= testResult;
		
		// Check the empty perception list - toString
		testResult = testSnapshot.toString().equals(new String("[]"));
		UnitTester.reportResult("PerceptSnapshot class - Empty String ", testResult, verbose);
		testOK &= testResult;
		
		// Check for similar perception in an empty snapshot
		List<Double> parameterList = new ArrayList<Double>();
		parameterList.add(8.0);
		testResult = (testSnapshot.pullSimilarPerception(new Perception("perceptName", 0, parameterList)) == null);
		UnitTester.reportResult("PerceptSnapshot class - Empty String ", testResult, verbose);
		testOK &= testResult;
		
		// Use the copy constructor and check the list of perceptions
		PerceptionSnapshot copiedSnahpshot = new PerceptionSnapshot(testSnapshot);
		testResult = (copiedSnahpshot.getPerceptionList().size() == 0);
		UnitTester.reportResult("PerceptSnapshot class - Empty snapshot copy constructor percept list check", testResult, verbose);
		testOK &= testResult;
		
		// Return result
		return testOK;
	}
	
	
	/**
	 * Test the case where there is an empty PerceptionSnapshot
	 * @param verbose
	 * @return
	 */
	public static boolean contentTest(boolean verbose) {
		boolean testOK = true;
		boolean testResult = true;
		
		// Constructor
		PerceptionSnapshot testSnapshot = new PerceptionSnapshot();
		
		// Make some Perceptions for testing with
		List<Double> parameterList = new ArrayList<Double>();
		parameterList.add(1.0);
		parameterList.add(2.0);
		Perception testPerception1 = new Perception("perceptName", 1, parameterList);
	
		parameterList = new ArrayList<Double>();
		parameterList.add(5.0);
		parameterList.add(2.0);
		Perception testPerception2 = new Perception("perceptName", 2, parameterList);
		
		parameterList = new ArrayList<Double>();
		parameterList.add(1.01);
		parameterList.add(2.0);
		Perception similarPerception = new Perception("perceptName", 0, parameterList);
		
		parameterList = new ArrayList<Double>();
		parameterList.add(1.21);
		parameterList.add(2.0);
		Perception differentPerception = new Perception("perceptName", 0, parameterList);		

		// addPerceptions to the snapshot
		testSnapshot.addPerception(testPerception1);
		testSnapshot.addPerception(testPerception2);
		
		// Check the size of the perception list
		testResult = (testSnapshot.getPerceptionList().size() == 2);
		UnitTester.reportResult("PerceptSnapshot class - Perception snapshot percept list size check", testResult, verbose);
		testOK &= testResult;
		
		// Check the perception list - latest version
		testResult = (testSnapshot.getLatestVersion() == 2);
		UnitTester.reportResult("PerceptSnapshot class - Perception snapshot version check", testResult, verbose);
		testOK &= testResult;
		
		// Check the perception list - list of literals
		boolean Literal1OK = testSnapshot.getLiterals().contains(testPerception1.getLiteral());
		boolean Literal2OK = testSnapshot.getLiterals().contains(testPerception2.getLiteral());
		boolean noOtherLiteralOK = !testSnapshot.getLiterals().contains(similarPerception.getLiteral());
		testResult = (Literal1OK && Literal2OK && noOtherLiteralOK);
		UnitTester.reportResult("PerceptSnapshot class - Contains Literal check list of Literals", testResult, verbose);
		testOK &= testResult;
		
		// Check the empty perception list - toString
		testResult = testSnapshot.toString().equals(new String("[perceptname(1,2), perceptname(5,2)]"));
		UnitTester.reportResult("PerceptSnapshot class - String check", testResult, verbose);
		testOK &= testResult;
				
		// Check for the same, similar, and different perception in the snapshot
		Perception samePerception = new Perception(testPerception1);
		// Check if the same perception is there
		boolean sameTestResult = testSnapshot.pullSimilarPerception(samePerception).equals(samePerception);
		
		// Check if a similar perception is there (will fail, it was removed by previous step
		boolean similarTestResultNull = (testSnapshot.pullSimilarPerception(similarPerception) == null);
		
		// Check if a similar perception is there (will fail, it was removed by previous step
		testSnapshot.addPerception(testPerception1);
		boolean similarTestResult = testSnapshot.pullSimilarPerception(similarPerception).equals(testPerception1);
		// Make sure it was removed
		boolean similarTestResultRemoved = testSnapshot.pullSimilarPerception(similarPerception) == null;
		
		// Check if a completely different perception is in the list
		testSnapshot.addPerception(testPerception1);
		boolean differentTestResult = (testSnapshot.pullSimilarPerception(differentPerception) == null);
		testResult = sameTestResult && similarTestResultNull && similarTestResult && similarTestResultRemoved && differentTestResult;
		UnitTester.reportResult("PerceptSnapshot class - pullSimilarPerception test", testResult, verbose);
		testOK &= testResult;
		
		// Use the copy constructor and check the list of perceptions
		PerceptionSnapshot copiedSnapshot = new PerceptionSnapshot(testSnapshot);
		boolean sizeOK = (copiedSnapshot.getPerceptionList().size() == 2);
		Literal1OK = copiedSnapshot.getLiterals().contains(testPerception1.getLiteral());
		Literal2OK = copiedSnapshot.getLiterals().contains(testPerception2.getLiteral());
		 noOtherLiteralOK = !copiedSnapshot.getLiterals().contains(similarPerception.getLiteral());
		testResult = (sizeOK && Literal1OK && Literal2OK && noOtherLiteralOK);
		
		UnitTester.reportResult("PerceptSnapshot class - Snapshot copy constructor percept list check", testResult, verbose);
		testOK &= testResult;
		
		// Make a new Perception, put something different in it, then addPerceptionsFromSnapshot()
		PerceptionSnapshot snapshotToAdd = new PerceptionSnapshot();
		snapshotToAdd.addPerception(differentPerception);
		copiedSnapshot.addPerceptionsFromSnapshot(snapshotToAdd);
		sizeOK = (copiedSnapshot.getPerceptionList().size() == 3);
		Literal1OK = copiedSnapshot.getLiterals().contains(testPerception1.getLiteral());
		Literal2OK = copiedSnapshot.getLiterals().contains(testPerception2.getLiteral());
		boolean differentLiteralOK = copiedSnapshot.getLiterals().contains(differentPerception.getLiteral());
		noOtherLiteralOK = !copiedSnapshot.getLiterals().contains(similarPerception.getLiteral());
		testResult = (sizeOK && Literal1OK && Literal2OK && differentLiteralOK && noOtherLiteralOK);
		UnitTester.reportResult("PerceptSnapshot class - Snapshot copy constructor percept list check", testResult, verbose);
		testOK &= testResult;
		
		// Return result
		return testOK;
	}
	
	
	
	
	
	/**
	 * Run all of the unit tests
	 * @param verbose
	 * @return
	 */
	public static boolean unitTest(boolean verbose) {
		boolean testOK = true;
		boolean testResult = true;
		
		// Test the case where there is an empty PerceptionSnapshot involved.
		testResult = PerceptionSnapshotUnitTest.emptyTest(verbose);
		testOK &= testResult;
		
		// Test a case where the PerceptionSnapshot is not empty
		testResult = PerceptionSnapshotUnitTest.contentTest(verbose);
		testOK &= testResult;
		
		// Return result
		return testOK;
	}
}
