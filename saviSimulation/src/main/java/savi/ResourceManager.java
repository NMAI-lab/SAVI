package savi;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceManager {

    private static final String OUTPUT_PATH = "output";
    private static File outputPathFile = new File(OUTPUT_PATH);

    public static File getResource(String resPath) {
        URL resourceUrl = ResourceManager.class.getClassLoader().getResource(resPath);

        // Throw a runtime exception when we can't load a resource
        if (resourceUrl == null)
            throw new RuntimeException("Failed to find resource: " + resPath);

        try {
            // Instantiate file from resource URI path
            File resourceFile = new File(resourceUrl.toURI().getPath());

            // Throw an exception if the file does not exist
            if (!resourceFile.exists())
                throw new RuntimeException("Resource: '" + resPath + "' does not exist");

            return resourceFile;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to resolve resource URI", e);
        }
    }

    public static File createOutputFile(String fileName) {
        if (!outputPathFile.exists()) {
            outputPathFile.mkdirs();
        }

        File outFile = new File(outputPathFile, fileName);
        try {
            outFile.createNewFile();
            return outFile;
        } catch (IOException ioE) {
            throw new RuntimeException("Failed to create the output file: " + fileName);
        }
    }

}
