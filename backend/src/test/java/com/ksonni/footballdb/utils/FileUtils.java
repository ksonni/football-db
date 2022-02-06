package com.ksonni.footballdb.utils;

import java.io.File;
import java.io.IOException;

public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Loads a file resource from the resources folder.
     *
     * @param resourcePath path within the resources folder
     * @return file as a byte array
     */
    public static byte[] loadResource(final String resourcePath) throws IOException {
        final ClassLoader classLoader = FileUtils.class.getClassLoader();
        final File file = new File(classLoader.getResource(resourcePath).getFile());
        return org.apache.commons.io.FileUtils.readFileToByteArray(file);
    }

}
