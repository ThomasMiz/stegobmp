package grupo3.steganography;

import grupo3.exceptions.CarrierNotLargeEnoughException;

import java.nio.charset.StandardCharsets;

/**
 * An interface that defines a steganography method for hiding a message in a carrier.
 */
public interface SteganographyMethod {
    /**
     * Calculates the minimum size required for a carrier message to be capable of sending a hidden message.
     *
     * @param messageSize   The size in bytes of the hidden message.
     * @param fileExtension The file extension to save, or null if not required.
     * @return The minimum size required for the carrier message.
     */
    int calculateCarrierSize(int messageSize, String fileExtension);

    /**
     * Calculates the maximum size for a hidden message that can be carried by a carrier.
     *
     * @param carrierSize   The size in bytes of the carrier message.
     * @param fileExtension The file extension to save, or null if not required.
     * @return The maximum size for a hidden message.
     */
    int calculateHiddenSize(int carrierSize, String fileExtension);

    /**
     * Hides a message into a carrier message.
     * <p>
     * The operation is done in-place, modifying the carrier message, but the message is left intact.
     *
     * @param carrier The carrier in which to hide the message.
     * @param message The message to hide.
     * @throws grupo3.exceptions.CarrierNotLargeEnoughException
     */
    void hideMessage(byte[] carrier, byte[] message);


    /**
     * Hides a message and an extension into a carrier message.
     * <p>
     * The operation is done in-place, modifying the carrier message, but the message is left intact.
     *
     * @param carrier       The carrier in which to hide the message.
     * @param message       The message to hide.
     * @param fileExtension The fileExtension to hide.
     * @throws grupo3.exceptions.CarrierNotLargeEnoughException
     */
    void hideMessageWithExtension(byte[] carrier, byte[] message, String fileExtension);

    private byte[] createExtendedMessage(byte[] message, String fileExtension) {
        byte[] extensionBytes = fileExtension == null ? new byte[0] : fileExtension.getBytes(StandardCharsets.UTF_8);

        // Create a new byte array for the combined message, extension, and null terminator
        byte[] extendedMessage = new byte[message.length + extensionBytes.length + 1];
        System.arraycopy(message, 0, extendedMessage, 0, message.length);
        System.arraycopy(extensionBytes, 0, extendedMessage, message.length, extensionBytes.length);

        // Add the null terminator at the end
        extendedMessage[extendedMessage.length - 1] = 0;

        return extendedMessage;
    }

    default byte[] getExtendedMessage(byte[] carrier, byte[] message, String fileExtension) throws CarrierNotLargeEnoughException {
        byte[] extendedMessage = createExtendedMessage(message, fileExtension);

        if (carrier.length < calculateCarrierSize(message.length, fileExtension) + extendedMessage.length) {
            throw new CarrierNotLargeEnoughException();
        }

        return extendedMessage;
    }

    /**
     * Extracts a message from a carrier message.
     * <p>
     * The carrier message is not modified.
     *
     * @return A new array with the extracted message.
     */
    byte[] extractMessage(byte[] carrier);

    /**
     * Extracts a message from a carrier message, and tries to parse and store a file extension
     * <p>
     * The carrier message is not modified.
     *
     * @return A new array with the extracted message, and the file extension.
     */
    ExtractResult extractMessageWithExtension(byte[] carrier);

    /**
     * Represents a successful result of a steganography message extraction operation. Contains the extracted message
     * and a file extension.
     */
    class ExtractResult {
        public byte[] message;
        public String fileExtension;

        public ExtractResult(byte[] message, String fileExtension) {
            this.message = message;
            this.fileExtension = fileExtension;
        }

        public ExtractResult(byte[] message) {
            this.message = message;
        }
    }
}
