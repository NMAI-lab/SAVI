package savi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceManagerTest {

    private static final String FAKE_OUTPUT_FILE = "fake_output.txt";
    private static final String FAKE_RESOURCE_FILE = "fake_resource.txt";
    private static final String FAKE_RESOURCE_FILE_IN_DIRECTORY = "fake_directory/fake_resource.txt";
    private static final String RESOURCE_DOESNT_EXIST = "non_existent_resource.txt";

    @Test
    void loadFakeResourceFile() {
        assertResourceExists(FAKE_RESOURCE_FILE);
    }

    @Test
    void loadFakeResourceFileInDirectory() {
        assertResourceExists(FAKE_RESOURCE_FILE_IN_DIRECTORY);
    }

    @Test
    void throwNullPointerOnNonExistentFile() {
        assertResourceNotExists(RESOURCE_DOESNT_EXIST);
    }

    @Test
    void createOutputFile() {
        File outFile = ResourceManager.createOutputFile(FAKE_OUTPUT_FILE);
        assertTrue(outFile.exists());
        assertTrue(outFile.delete());
    }

    @Test
    void createExistingOutputFile() {
        File outFile = ResourceManager.createOutputFile(FAKE_OUTPUT_FILE);
        assertTrue(outFile.exists());

        outFile = ResourceManager.createOutputFile(FAKE_OUTPUT_FILE);
        assertTrue(outFile.exists());
        assertTrue(outFile.delete());
    }

    private void assertResourceExists(String path) {
        try {
            File fakeFile = ResourceManager.getResource(path);
            assertTrue(fakeFile.exists());
        } catch (RuntimeException rE) {
            Assertions.fail("Loading the fake resource should not result in a runtime exception: " + rE.getMessage());
        }
    }

    private void assertResourceNotExists(String path) {
        try {
            ResourceManager.getResource(path);
            Assertions.fail("Resource should not exist");
        } catch (RuntimeException rE) {

        }
    }
}
