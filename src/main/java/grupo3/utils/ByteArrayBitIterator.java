package grupo3.utils;

/**
 * An implementation of BitIterator for iterating over the bits of a byte array.
 */
public class ByteArrayBitIterator implements BitIterator {
    private final int endIndex;
    private final byte[] data;
    private int index;
    private int bitIndex;

    public ByteArrayBitIterator(int index, int bitIndex, int endIndex, byte[] data) {
        this.index = index;
        this.bitIndex = bitIndex;
        this.endIndex = endIndex;
        this.data = data;
    }

    public ByteArrayBitIterator(int index, int endIndex, byte[] data) {
        this(index, 0, endIndex, data);
    }

    public ByteArrayBitIterator(byte[] data) {
        this(0, 0, data.length, data);
    }

    @Override
    public boolean hasNextBit() {
        return index < endIndex;
    }

    @Override
    public int nextBitOrMinusOne() {
        if (!hasNextBit()) {
            return -1;
        }

        // Convert a byte to int as if it were unsigned
        int b = Byte.toUnsignedInt(data[index]);
        int result = (b >> (7 - bitIndex)) & 0x01;

        if (bitIndex == 7) {
            index++;
            bitIndex = 0;
        } else {
            bitIndex++;
        }

        return (byte) result;
    }
}
