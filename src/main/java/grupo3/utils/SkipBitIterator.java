package grupo3.utils;

/**
 * An implementation of BitIterator for iterating over another BitIterator, but that after <code>count</code> bits will
 * skip <code>skip</code> bits.
 * <p>
 * Note: Skipping bits happens AFTER counting bits.
 */
public class SkipBitIterator implements BitIterator {
    private final BitIterator inner;
    private final int skip;
    private final int count;
    private int counted;

    public SkipBitIterator(BitIterator inner, int skip, int count, int counted) {
        if (count == 0) {
            throw new IllegalArgumentException("count may not be zero");
        }

        if (counted >= count) {
            throw new IllegalArgumentException("counted must be lesser than count");
        }

        if (skip < 0 || count < 0 || counted < 0) {
            throw new IllegalArgumentException("skip, count and counted must all be >= 0");
        }

        this.inner = inner;
        this.skip = skip;
        this.count = count;
        this.counted = counted;
    }

    public SkipBitIterator(BitIterator inner, int count) {
        this(inner, 8 - count, count, 0);
    }

    @Override
    public boolean hasNextBit() {
        return inner.hasNextBit();
    }

    @Override
    public int nextBitOrMinusOne() {
        int result = inner.nextBitOrMinusOne();

        counted++;
        if (counted == count) {
            counted = 0;
            for (int i = 0; i < skip; i++) {
                inner.nextBitOrMinusOne();
            }
        }

        return result;
    }
}
