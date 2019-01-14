package savi.unitTesting;

import java.util.ArrayList;
import java.util.List;

import jason.asSyntax.Literal;
import savi.jason_processing.Perception;

public class PerceptionUnitTest {
	/**
	 * Perform unit testing on the Perception class.
	 * @return
	 */
	public static boolean unitTest(boolean verbose) {
		boolean testOK = true;
		
		// Make a new perception with null type
		List<Double> perceptParameters = new ArrayList<Double>();
		perceptParameters.add(1.0);
		perceptParameters.add(2.0);
		String perceptName = new String("test");
		String perceptType = null;
		long versionID = 8;
		Perception perceptTest = new Perception(perceptName, perceptType, versionID, perceptParameters);
		
		// Check the parameters of the new Perception
		if (perceptTest.getPerceptionName().equals(perceptName)) {
			System.out.println("Percept class - Name test OK");
		} else {
			System.out.println("Percept class - Name test FAILED");
			testOK = false;
		}
		
		if (perceptTest.getPerceptionType() == null) {
			System.out.println("Percept class - Type null test OK");
		} else {
			System.out.println("Percept class - Type null test FAILED");
			testOK = false;
		}
		
		if (perceptTest.getVersionID() == versionID) {
			System.out.println("Percept class - VersionID test OK");
		} else {
			System.out.println("Percept class - VersionID test FAILED");
			testOK = false;
		}
		
		if (perceptTest.getParameters().equals(perceptParameters)) {
			System.out.println("Percept class - Parameter test OK");
		} else {
			System.out.println("Percept class - Parameter test FAILED");
			testOK = false;
		}
		
		if (perceptTest.getParameters().equals(perceptParameters)) {
			System.out.println("Percept class - Parameter test OK");
		} else {
			System.out.println("Percept class - Parameter test FAILED");
			testOK = false;
		}

		// Make new version of the perception, with a type
		perceptType = new String("someType");
		Perception perceptTestType = new Perception(perceptName, perceptType, versionID, perceptParameters);
		if (perceptTestType.getPerceptionType().equals(perceptType)) {
			System.out.println("Percept class - Type (not null) test OK");
		} else {
			System.out.println("Percept class - Type (not null) null test FAILED");
			testOK = false;
		}
		
		// Test copy constructor without the type specified
		Perception perceptTestCopy = new Perception(perceptTest);
		
		// Check the parameters of the new copied Perception
		if (perceptTest.getPerceptionName().equals(perceptTestCopy.getPerceptionName())) {
			System.out.println("Percept class - Name after copy test OK");
		} else {
			System.out.println("Percept class - Name after copy test FAILED");
			testOK = false;
		}
		
		if (perceptTestCopy.getPerceptionType() == null) {
			System.out.println("Percept class - Type null after copy test OK");
		} else {
			System.out.println("Percept class - Type null after copy test FAILED");
			testOK = false;
		}
		
		if (perceptTest.getVersionID() == perceptTestCopy.getVersionID()) {
			System.out.println("Percept class - VersionID after copy test OK");
		} else {
			System.out.println("Percept class - VersionID after copy test FAILED");
			testOK = false;
		}
		
		if (perceptTest.getParameters().equals(perceptTestCopy.getParameters())) {
			System.out.println("Percept class - Parameter after copy test OK");
		} else {
			System.out.println("Percept class - Parameter after copy test FAILED");
			testOK = false;
		}
		
		if (perceptTest.equals(perceptTestCopy)) {
			System.out.println("Percept class - Equals after copy with null test OK");
		} else {
			System.out.println("Percept class - Parameter after copy with null test FAILED");
			testOK = false;
		}
				
		// Test copy constructor with type specified
		Perception perceptTestCopyType = new Perception(perceptTestType);
		
		// Check the parameters of the new copied Perception
		if (perceptTestType.getPerceptionName().equals(perceptTestCopyType.getPerceptionName())) {
			System.out.println("Percept class - Name after copy with type test OK");
		} else {
			System.out.println("Percept class - Name after copy with type test FAILED");
			testOK = false;
		}
		
		if (perceptTestType.getPerceptionType().equals(perceptTestCopyType.getPerceptionType())) {
			System.out.println("Percept class - Type null after copy with type test OK");
		} else {
			System.out.println("Percept class - Type null after copy with type test FAILED");
			testOK = false;
		}
		
		if (perceptTestType.getVersionID() == perceptTestCopyType.getVersionID()) {
			System.out.println("Percept class - VersionID after copy with type test OK");
		} else {
			System.out.println("Percept class - VersionID after copy with type test FAILED");
			testOK = false;
		}
		
		if (perceptTestType.getParameters().equals(perceptTestCopyType.getParameters())) {
			System.out.println("Percept class - Parameter after copy with type test OK");
		} else {
			System.out.println("Percept class - Parameter after copy with type test FAILED");
			testOK = false;
		}
		
		if (perceptTestType.equals(perceptTestCopyType)) {
			System.out.println("Percept class - Equals after copy with type test OK");
		} else {
			System.out.println("Percept class - Parameter after copy with type test FAILED");
			testOK = false;
		}
		
		// Test compare name
		Perception perceptTestAnotherName = new Perception(perceptName + "different", perceptType, versionID, perceptParameters);
		if (perceptTest.comparePerceptionName(perceptTest) && perceptTest.comparePerceptionName(perceptTestCopy) && !perceptTest.comparePerceptionName(perceptTestAnotherName)) {
			System.out.println("Percept class - Perception name comparison test OK");
		} else {
			System.out.println("Percept class - Perception name comparison test FAILED");
			testOK = false;
		}
		
		// Test compare type - null case
		if ((perceptTest.getPerceptionType() == null) && perceptTest.comparePerceptionType(perceptTest) && perceptTest.comparePerceptionType(perceptTestCopy) && !perceptTest.comparePerceptionType(perceptTestAnotherName)) {
			System.out.println("Percept class - Perception type comparison (null case) test OK");
		} else {
			System.out.println("Percept class - Perception type comparison (null case) test FAILED");
			testOK = false;
		}
		
		// Test compare type - not null case
		Perception perceptTestTypeAnotherType = new Perception(perceptName, perceptType + "different", versionID, perceptParameters);
		if (perceptTestType.comparePerceptionType(perceptTestType) && perceptTestType.comparePerceptionType(perceptTestCopyType) && !perceptTest.comparePerceptionType(perceptTestTypeAnotherType)) {
			System.out.println("Percept class - Perception type comparison (not null) test OK");
		} else {
			System.out.println("Percept class - Perception type comparison (not null) test FAILED");
			testOK = false;
		}
		
		// Test perceptLost()
		Perception perceptLostTest = new Perception(perceptTest);
		perceptLostTest.perceptionLost();
		if(perceptLostTest.getPerceptionName().equals(perceptTest.getPerceptionName() + "lost")) {
			System.out.println("Percept class - Perception lost test OK");
		} else {
			System.out.println("Percept class - Perception lost test FAILED");
			testOK = false;
		}

		// Get Literal test
		if(perceptLostTest.getLiteral().equals(Literal.parseLiteral(new String("testlost(1.0,2.0)"))) && perceptTest.getLiteral().equals(Literal.parseLiteral(new String("test(1.0,2.0)")))) {
			System.out.println("Percept class - Perception lost test OK");
		} else {
			System.out.println("Percept class - Perception lost test FAILED");
			testOK = false;
		}
		
		// Test difference and similarity (similarity uses difference calculation)
		boolean differenceZeroWithSelf = (perceptTest.getDifference(perceptTest) == 0.0);
		boolean differenceOneFromLost = (perceptTest.getDifference(perceptLostTest) == 1.0);
		boolean differenceOneType = (perceptTest.getDifference(perceptTestTypeAnotherType) == 1.0);
		boolean similarWithSelf = perceptTest.isSimilar(perceptTest);
		boolean differentFromLost = !perceptTest.isSimilar(perceptLostTest);
		boolean differentType = !perceptTest.isSimilar(perceptTestTypeAnotherType);
				
		List<Double> similarList = new ArrayList<Double>();
		similarList.add(1.01);
		similarList.add(2.0);
		boolean slightlySimilar = perceptTest.isSimilar(new Perception(perceptTest.getPerceptionName(), perceptTest.getPerceptionType(), 0, similarList));
		
		List<Double> differentList = new ArrayList<Double>();
		differentList.add(2.0);
		differentList.add(2.0);
		boolean notSimilar = !perceptTest.isSimilar(new Perception(perceptTest.getPerceptionName(), perceptTest.getPerceptionType(), 0, differentList));

		if (differenceZeroWithSelf && differenceOneFromLost && differenceOneType && similarWithSelf && differentFromLost && differentType && slightlySimilar && notSimilar) {
			System.out.println("Percept class - Similarity test OK");
		} else {
			System.out.println("Percept class - Similarity test FAILED");
			testOK = false;
		}	
		
		// Return the final result
		return testOK;
	}
}
