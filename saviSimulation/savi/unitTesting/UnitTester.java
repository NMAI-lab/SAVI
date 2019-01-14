package savi.unitTesting;

public class UnitTester {

	public static void reportResult(String message, boolean passed, boolean verbose) {
		message = message + " - ";
		if (passed) {
			message = message + "OK";
		} else {
			message = message + "FAILED";
		}
		if (verbose) {
			System.out.println(message);
		}
	}
	
	public static void main(String[] args) {
		boolean verboseTest = true;		// Print all test details
		boolean testSuccess = true;		// Test is successful so far, will be toggled to false if it isn't
		boolean testResult = true;		
		
		System.out.println("Running unit tester");
		
		// Test the Perception class
		testResult = PerceptionUnitTest.unitTest(verboseTest);
		reportResult("Perception test", testResult, verboseTest);
		testSuccess = testSuccess && testResult;
		
		// Test the PerceptionSnapshot class
		testResult = PerceptionSnapshotUnitTest.unitTest(verboseTest);
		reportResult("PerceptionSnapshot test", testResult, verboseTest);
		testSuccess = testSuccess && testResult;
		
		// Tests finished, force the result to print
		verboseTest = true;
		reportResult("Unit tester finished", testSuccess, verboseTest);
	}
}
