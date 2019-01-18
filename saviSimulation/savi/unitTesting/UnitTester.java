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
		
		System.out.println("Running unit tester");
		
		// Test the perception class
		if (PerceptionUnitTest.unitTest(verboseTest)) {
			System.out.println("Perception test - OK");
		} else {
			System.out.println("Perception test - FAILED");
			testSuccess = false;
		}
		
		// Tests finished
		if (testSuccess) {
			System.out.println("Unit tester finished - OK");
		} else {
			System.out.println("Unit tester finished - FAILED");
		}
	}
}
