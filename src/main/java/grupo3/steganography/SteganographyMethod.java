package grupo3.steganography;

/**
 * An interface that defines a steganography method for hiding a message in a carrier.
 */
public interface SteganographyMethod {
    /**
     * Calculates the minimum size required for a carrier message to be capable of sending a hidden message.
     *
     * @param messageSize The size in bytes of the hidden message.
     * @return The minimum size required for the carrier message.
     */
    int calculateCarrierSize(int messageSize);

    /**
     * Calculates the maximum size for a hidden message that can be carried by a carrier.
     *
     * @param carrierSize The size in bytes of the carrier message.
     * @return The maximum size for a hidden message.
     */
    int calculateHiddenSize(int carrierSize);

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
