package grupo3.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class StreamUtils {
    private StreamUtils() {
        throw new RuntimeException("StreamUtils not instanciable");
    }

    public static void writeInt(OutputStream stream, int value) throws IOException {
        stream.write(value & 0b11111111);
        stream.write((value >> 8) & 0b11111111);
        stream.write((value >> 16) & 0b11111111);
        stream.write((value >> 24) & 0b11111111);
    }

    public static void writeShort(OutputStream stream, short value) throws IOException {
        stream.write(value & 0b11111111);
        stream.write((value >> 8) & 0b11111111);
    }

    public static int readByte(InputStream stream) throws IOException {
        int b = stream.read();
        if (b == -1) {
            throw new EOFException();
        }

        return b;
    }

    public static short readShort(InputStream stream) throws IOException {
        int v0 = readByte(stream);
        int v1 = readByte(stream);
        return (short) (v0 | (v1 << 8));
    }

    public static int readInt(InputStream stream) throws IOException {
        int v0 = readByte(stream);
        int v1 = readByte(stream);
        int v2 = readByte(stream);
        int v3 = readByte(stream);
        return v0 | (v1 << 8) | (v2 << 16) | (v3 << 24);
    }

    public static void readExact(InputStream stream, byte[] data, int offset, int length) throws IOException {
        int count = 0;
        while (count < length) {
            int result = stream.read(data, offset + count, length - count);
            if (result <= 0) {
                throw new EOFException("The file ended unexpectedly");
            }

            count += result;
        }
    }

    public static void readIgnore(InputStream stream, int length) throws IOException {
        for (int i = 0; i < length; i++) {
            stream.read();
        }
    }

    public static void writeZeroes(OutputStream stream, int length) throws IOException {
        for (int i = 0; i < length; i++) {
            stream.write(0);
        }
    }
}
