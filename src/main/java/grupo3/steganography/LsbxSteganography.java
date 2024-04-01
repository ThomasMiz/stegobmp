package grupo3.steganography;

import grupo3.exceptions.CarrierNotLargeEnoughException;
import grupo3.utils.*;

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
    public int calculateCarrierSize(int messageSize) {
        // Four extra bytes for storing the length of the message
        messageSize += 4;

        // Calculate the total amount of bits the message has
        int messageSizeBits = messageSize * 8;

        // The size of the carrier is messageSizeBits divided by bitCount, rounded upwards
        return (messageSizeBits + bitCount - 1) / bitCount;
    }

    @Override
    public int calculateHiddenSize(int carrierSize) {
        // A carrier of that size will have up to this amount of hidden bits, and therefore bytes
        int totalSizeBits = carrierSize * bitCount;
        int totalSizeBytes = totalSizeBits / 8;

        // Remove the first four bytes which are used to indicate the length of the message
        int messageSize = totalSizeBytes - 4;

        // Make sure we didn't go below 0
        return Math.max(0, messageSize);
    }

    @Override
    public void hideMessage(byte[] carrier, byte[] message) {
        if (carrier.length < calculateCarrierSize(message.length)) {
            throw new CarrierNotLargeEnoughException();
        }

        BitIterator bits = new ConcatBitIterator(new IntBitIterator(message.length), new ByteArrayBitIterator(message));

        int i;
        byte carrierMask = (byte) (0b11111111 << bitCount);
        for (i = 0; i < carrier.length && bits.hasNextBit(); i++) {
            int hiddenBits = bits.nextBit();
            for (int b = 1; b < bitCount; b++) {
                hiddenBits |= bits.nextBitOrZero() << b;
            }

            carrier[i] = (byte) ((carrier[i] & carrierMask) | hiddenBits);
        }
    }

    @Override
    public byte[] extractMessage(byte[] carrier) {
        BitIterator bits = new SkipBitIterator(new ByteArrayBitIterator(carrier), bitCount);

        int messageLength = bits.nextByte() | (bits.nextByte() << 8) | (bits.nextByte() << 16) | (bits.nextByte() << 24);
        byte[] message = new byte[messageLength];

        for (int i = 0; i < message.length; i++) {
            message[i] = (byte) bits.nextByte();
        }

        return message;
    }
}
