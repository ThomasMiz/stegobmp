package grupo3.utils;

/**
 * An implementation of BitIterator that concatenates two other BitIterators.
 */
public class ConcatBitIterator implements BitIterator {
    private final BitIterator first;
    private final BitIterator second;

    public ConcatBitIterator(BitIterator first, BitIterator second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean hasNextBit() {
        return first.hasNextBit() || second.hasNextBit();
    }

    @Override
    public int nextBitOrMinusOne() {
        int b1 = first.nextBitOrMinusOne();
        return b1 != -1 ? b1 : second.nextBitOrMinusOne();
    }
}
