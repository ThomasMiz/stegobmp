package grupo3.utils;

public class IntBitIterator implements BitIterator {
    private final int value;
    private final int byteIndex;
    private int bitIndex;

    public IntBitIterator(int value, int bitIndex, int byteIndex) {
        this.value = value;
        this.bitIndex = bitIndex;
        this.byteIndex = byteIndex;
    }

    public IntBitIterator(int value) {
        this(value, 0, 0);
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

        int result = (value >> (31 - bitIndex)) & 0x01;
        bitIndex += 1;
        return result;
    }
}