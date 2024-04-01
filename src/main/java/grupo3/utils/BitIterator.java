package grupo3.utils;

/**
 * A helper interface for iterating over bits.
 */
public interface BitIterator {
    boolean hasNextBit();

    int nextBitOrMinusOne();

    default byte nextBit() {
        int result = nextBitOrMinusOne();
        if (result == -1) {
            throw new IllegalStateException("This BitIterator has reached its end");
        }

        return (byte) result;
    }

    default byte nextBitOrZero() {
        int result = nextBitOrMinusOne();
        return result == -1 ? 0 : (byte) result;
    }

    default int nextByteOrMinusOne() {
        int value = 0;
        for (int i = 0; i < 8; i++) {
            int bit = nextBitOrMinusOne();
            if (bit == -1) {
                return -1;
            }

            value |= (bit << i);
        }

        return value;
    }

    default int nextByte() {
        int result = nextByteOrMinusOne();
        if (result == -1) {
            throw new IllegalStateException("This BitIterator has reached its end");
        }

        return (byte) result;
    }
}
