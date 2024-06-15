package grupo3.utils;

public class SkipByteArrayBitIterator implements BitIterator{

    private int index;
    private final int endIndex;
    private final byte[] data;
    private final byte[] inversions;

    public SkipByteArrayBitIterator(int index, int endIndex, byte[] data, byte[] inversions) {
        this.index = index;
        this.endIndex = endIndex;
        this.data = data;
        this.inversions = inversions;
    }


    public SkipByteArrayBitIterator(byte[] data, byte[] inversions) {
        this(0, data.length, data, inversions);
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

        int b = Byte.toUnsignedInt(data[index]);
        int toReturn = getHiddenBit(b);

        if (toReturn != 0 && toReturn != 1) {
            throw new IllegalStateException("a byte should be 0 or 1");
        }
        index = index % 3 == 1 ? index + 2 : index + 1;
        return toReturn;
    }

    private byte getHiddenBit(int elem) {
        int pattern = (elem >> 1) & 0b00000011;
        return (byte) ((elem ^ inversions[pattern]) & 0b00000001);
    }

    @Override
    public int nextByte() {
        int toReturn = 0;

        for (int i = 0 ; i < 8 ; ++i) {
            if (!hasNextBit()) {
                throw new IllegalStateException("This BitIterator has reached its end");
            }
            toReturn = (toReturn << 1) | this.nextBitOrMinusOne();
        }
        return toReturn;
    }

}
