package savi.util;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceManager {

    private static final String OUTPUT_PATH = "output";
    private static File outputPathFile = new File(OUTPUT_PATH);

    public static InputStream getResourceStream(String resPath) {
        if(!resPath.startsWith("/"))
            resPath = "/" + resPath;
        return ResourceManager.class.getResourceAsStream(resPath);
    }

    public static BufferedReader getResourceBufferedReader(String resPath) {
        return new BufferedReader(new InputStreamReader(getResourceStream(resPath)));
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
