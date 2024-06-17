package grupo3.steganography;

import grupo3.exceptions.CarrierNotLargeEnoughException;
import grupo3.utils.*;

import java.nio.charset.StandardCharsets;

/**
 * A Steganography method that hides information in the N lowest significant bits of the carrier.
 */
public class LsbxSteganography implements SteganographyMethod {

    /**
     * The amount of bits hidden per byte.
     */
    private final int bitCount;

    /**
     * Creates a new LsbxSteganography instance with a given bitCount.
     *
     * @param bitCount The amount of bits hidden per byte. Must be between 1 and 8 inclusive.
     */
    public LsbxSteganography(int bitCount) {
        if (bitCount <= 0 || bitCount > 8) {
            throw new IllegalArgumentException("bitCount must be between 1 and 8 inclusive");
        }

        this.bitCount = bitCount;
    }

    @Override
    public int calculateCarrierSize(int messageSize, String fileExtension) {
        // Four extra bytes for storing the length of the message
        messageSize += 4;

        // If there's a file extension in use, then count those bytes too, plus the '\0'
        if (fileExtension != null) {
            messageSize += fileExtension.getBytes(StandardCharsets.UTF_8).length + 1;
        }

        // Calculate the total amount of bits the message has
        int messageSizeBits = messageSize * 8;

        // The size of the carrier is messageSizeBits divided by bitCount, rounded upwards
        return (messageSizeBits + bitCount - 1) / bitCount;
    }

    @Override
    public int calculateHiddenSize(int carrierSize, String fileName) {
        // A carrier of that size will have up to this amount of hidden bits, and therefore bytes
        int totalSizeBits = carrierSize * bitCount;
        int totalSizeBytes = totalSizeBits / 8;

        // Remove the first four bytes which are used to indicate the length of the message
        int messageSize = totalSizeBytes - 4;

        // Remove the last few bytes used for the file extension, if one is present, plus the '\0'
        if (fileName != null) {
            messageSize -= fileName.getBytes(StandardCharsets.UTF_8).length + 1;
        }

        // Make sure we didn't go below 0
        return Math.max(0, messageSize);
    }

    @Override
    public void hideMessage(byte[] carrier, byte[] message) {
        hideMessageWithExtension(carrier, message, null);
    }

    @Override
    public void hideMessageWithExtension(byte[] carrier, byte[] message, String fileExtension) {
        byte[] fileExtensionBytes = fileExtension == null ? null : fileExtension.getBytes(StandardCharsets.UTF_8);
        int fileExtensionLengthBytes = fileExtensionBytes == null ? 0 : fileExtensionBytes.length;
        if (carrier.length < calculateCarrierSize(message.length, fileExtension) + fileExtensionLengthBytes) {
            throw new CarrierNotLargeEnoughException();
        }

        BitIterator bits = new ConcatBitIterator(new IntBitIterator(message.length), new ByteArrayBitIterator(message));
        if (fileExtensionBytes != null) {
            bits = new ConcatBitIterator(bits, new ByteArrayBitIterator(fileExtensionBytes));
            bits = new ConcatBitIterator(bits, new ByteArrayBitIterator(new byte[]{0}));
        }

        int i;
        byte carrierMask = (byte) (0b11111111 << bitCount);
        for (i = 0; i < carrier.length && bits.hasNextBit(); i++) {
            int hiddenBits = bits.nextBit() << (bitCount - 1);
            for (int b = bitCount - 2; b >= 0; b--) {
                hiddenBits |= bits.nextBitOrZero() << b;
            }

            carrier[i] = (byte) ((carrier[i] & carrierMask) | hiddenBits);
        }
    }

    private ExtractResult extract(byte[] carrier, boolean withExtension) {
        BitIterator bits = new SkipBitIterator(new ByteArrayBitIterator(carrier), bitCount);

        int b1 = Byte.toUnsignedInt((byte) bits.nextByteBe());
        int b2 = Byte.toUnsignedInt((byte) bits.nextByteBe());
        int b3 = Byte.toUnsignedInt((byte) bits.nextByteBe());
        int b4 = Byte.toUnsignedInt((byte) bits.nextByteBe());
        int messageLength = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
        if (messageLength <= 0) {
            throw new IllegalArgumentException("Invalid file format: the length must be greater than 0 but was " + messageLength);
        }

        byte[] message = new byte[messageLength];
        for (int i = 0; i < message.length; i++) {
            message[i] = (byte) bits.nextByteBe();
        }

        String fileExtension = null;
        if (withExtension) {
            StringBuilder extension = new StringBuilder();
            int b;
            while ((b = Byte.toUnsignedInt((byte) bits.nextByteBe())) != 0) {
                extension.append((char) b);
            }
            fileExtension = extension.toString();
        }

        return new ExtractResult(message, fileExtension);
    }

    @Override
    public byte[] extractMessage(byte[] carrier) {
        return extract(carrier, false).message;
    }

    @Override
    public ExtractResult extractMessageWithExtension(byte[] carrier) {
        return extract(carrier, true);
    }
}
