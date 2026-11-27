package com.dyno.deaddrop.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class FileServiceHelper {

    public static String sanitizeFilename(String filename) {

        if (filename == null || filename.isBlank()) {
            return "download";
        }

        String normalized = filename.replace('\\', '/');

        int lastSeparator = normalized.lastIndexOf('/');

        if (lastSeparator >= 0) {
            normalized = normalized.substring(lastSeparator + 1);
        }

        normalized = normalized.trim();

        if (normalized.isBlank()) {
            return "download";
        }

        if (normalized.length() > 255) {
            normalized = normalized.substring(0, 255);
        }

        return normalized;
    }

   
    public static String sanitizeContentType(String contentType) {

        if (contentType == null
                || contentType.isBlank()
                || contentType.length() > 150
                || containsControlCharacter(contentType)) {

            return "application/octet-stream";
        }

        return contentType;
    }

    public static boolean containsControlCharacter(String value) {

        for (int i = 0; i < value.length(); i++) {

            if (Character.isISOControl(value.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    public static  String removeTrailingSlashes(String url) {

        String result = url.trim();

        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    /*
     * Deletes temporary files without hiding the original upload error.
     */
    public static void deleteTemporaryFile(Path path, Logger LOGGER) {

        if (path == null) {
            return;
        }

        try {

            Files.deleteIfExists(path);

        } catch (IOException exception) {

            LOGGER.log(
                    Level.WARNING,
                    "Failed to delete a temporary Dead Drop file",
                    exception
            );
        }
    }
    
    

}
