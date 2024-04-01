package grupo3.utils;

/**
 * An implementation of BitIterator for iterating over the bits of an int.
 */
public class IntBitIterator implements BitIterator {
    private final int value;
    private int bitIndex;

    public IntBitIterator(int value, int bitIndex) {
        this.value = value;
        this.bitIndex = bitIndex;
    }

    public IntBitIterator(int value) {
        this(value, 0);
    }

    @Override
    public boolean hasNextBit() {
        return bitIndex < 32;
    }

    @Override
    public int nextBitOrMinusOne() {
        if (!hasNextBit()) {
            return -1;
        }

        int result = (value >> bitIndex) & 0x01;
        bitIndex += 1;
        return result;
    }
}
