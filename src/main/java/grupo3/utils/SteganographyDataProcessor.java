package grupo3.utils;

import java.util.Arrays;

/**
 * Utility class for extracting data from steganography byte array knowing that
 * the format is: realSize || data || ”.extension\0”
 */
public class SteganographyDataProcessor {

    private static final int REAL_SIZE_BYTES = 4;

    private final byte[] decryptedBytes;
    private final int realSize;
    private final byte[] data;
    private final String fileExtension;

    /**
     * Constructs a SteganographyDataProcessor with the given decrypted bytes.
     *
     * @param decryptedBytes The decrypted bytes containing realSize, data, and file extension.
     */
    public SteganographyDataProcessor(byte[] decryptedBytes) {
        this.decryptedBytes = decryptedBytes;
        this.realSize = extractRealSize();
        this.data = extractData();
        this.fileExtension = extractFileExtension();
    }

    /**
     * Extracts the real size of the data from the decrypted bytes.
     *
     * @return The integer value of the real size extracted from the byte array.
     */
    private int extractRealSize() {
        return bytesToInt(0, REAL_SIZE_BYTES);
    }

    /**
     * Extracts the data portion from the decrypted bytes, starting right after the real size indicator.
     *
     * @return The byte array containing the extracted data.
     */
    public byte[] extractData() {
        int dataStartIndex = REAL_SIZE_BYTES;
        int dataLength = realSize;
        return Arrays.copyOfRange(decryptedBytes, dataStartIndex, dataStartIndex + dataLength);
    }

    /**
     * Extracts the file extension from the decrypted bytes, following the data portion.
     *
     * @return The file extension extracted from the byte array as a String.
     */
    public String extractFileExtension() {
        int extensionStartIndex = REAL_SIZE_BYTES + realSize;
        byte[] extensionBytes = Arrays.copyOfRange(decryptedBytes, extensionStartIndex, decryptedBytes.length);
        return new String(extensionBytes).replace("\0", ""); // Remove null terminator
    }

    /**
     * Converts a portion of the decrypted bytes to an integer value.
     *
     * @param offset The starting index in the decrypted bytes.
     * @param length The number of bytes to convert to an integer.
     * @return The integer value represented by the specified portion of the decrypted bytes.
     */
    private int bytesToInt(int offset, int length) {
        int value = 0;
        for (int i = 0; i < length; i++) {
            value = (value << 8) | (decryptedBytes[offset + i] & 0xFF);
        }
        return value;
    }

    /**
     * Getter for the real size extracted from the decrypted bytes.
     *
     * @return The real size as an integer.
     */
    public int getRealSize() {
        return realSize;
    }

    /**
     * Getter for the extracted data from the decrypted bytes.
     *
     * @return The byte array containing the extracted data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Getter for the file extension extracted from the decrypted bytes.
     *
     * @return The file extension as a String.
     */
    public String getFileExtension() {
        return fileExtension;
    }
}
