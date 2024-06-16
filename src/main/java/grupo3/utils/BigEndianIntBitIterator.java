package grupo3.utils;

public class BigEndianIntBitIterator implements BitIterator {
    private final int value;
    private int bitIndex;
    private final int byteIndex;

    public BigEndianIntBitIterator(int value, int bitIndex, int byteIndex) {
        this.value = value;
        this.bitIndex = bitIndex;
        this.byteIndex = byteIndex;
    }

    public BigEndianIntBitIterator(int value) {
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