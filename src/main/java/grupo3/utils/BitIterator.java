package grupo3.utils;

/**
 * A helper type for iterating over the bits of a byte array.
 */
public class BitIterator { // WHY CANT I JUST `impl Iterator<u8>` 😭😭 I wanna go back to Rust
    int index;
    int endIndex;
    int bitIndex;
    byte[] data;

    public BitIterator(int index, int endIndex, int bitIndex, byte[] data) {
        this.index = index;
        this.endIndex = endIndex;
        this.bitIndex = bitIndex;
        this.data = data;
    }

    public BitIterator(int index, int endIndex, byte[] data) {
        this(index, endIndex, 0, data);
    }

    public BitIterator(byte[] data) {
        this(0, data.length, 0, data);
    }

    public boolean hasNextBit() {
        return index < endIndex;
    }

    public byte nextBit() {
        if (!hasNextBit()) {
            throw new IllegalStateException("This BitIterator has reached its end");
        }

        // Convert a byte to int as if it were unsigned
        // Because java is a shitty language that doesn't have the concept an unsigned integer
        int b = Byte.toUnsignedInt(data[index]);
        int result = (b >> bitIndex) & 0x01;

        if (bitIndex == 7) {
            index++;
            bitIndex = 0;
        } else {
            bitIndex++;
        }

        return (byte) result;
    }
}
