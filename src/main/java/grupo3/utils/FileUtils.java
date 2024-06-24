package grupo3.utils;

import grupo3.exceptions.FileExtensionNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for file operations.
 */
public class FileUtils {

    /**
     * Extracts the file extension from the given filename.
     *
     * @param filename The name of the file from which to extract the extension.
     * @return The file extension, including the dot (e.g., ".txt").
     * @throws FileExtensionNotFoundException If the filename is null, empty, or does not contain an extension.
     */
    public static String getFileExtension(String filename) throws FileExtensionNotFoundException {
        if (filename == null || filename.isEmpty()) {
            throw new FileExtensionNotFoundException("Output filename should not be empty");
        }

        int dotIndex = filename.lastIndexOf('.');

        // Check if the dot is found and it is not the first character
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }

        // No extension found
        throw new FileExtensionNotFoundException("Output filename should have an extension");
    }

    /**
     * Writes the given data to the specified file path using FileOutputStream.
     *
     * @param filePath The path of the file to write to.
     * @param data     The data to write to the file.
     * @throws IOException If an I/O error occurs writing to or creating the file.
     */
    public static void writeToFile(String filePath, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
    }
}
