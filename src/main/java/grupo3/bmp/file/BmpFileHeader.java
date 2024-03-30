package grupo3.bmp.file;

import grupo3.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BmpFileHeader {
    /**
     * The size in bytes this header uses when written to a file.
     */
    public static final int SIZE = 14;

    /**
     * The u16 header field used to identify the BMP file, must be 0x42 0x4D in hexadecimal, same as BM in ASCII.
     */
    public short type;

    /**
     * The i32 size of the BMP file in bytes.
     */
    public int size;

    /**
     * Reserved; actual value depends on the application that creates the image, if created manually can be 0.
     */
    public short reserved1;

    /**
     * Reserved; actual value depends on the application that creates the image, if created manually can be 0.
     */
    public short reserved2;

    /**
     * The i32 offset, i.e. starting address, of the byte where the bitmap image data (pixel array) can be found.
     */
    public int offBits;

    public BmpFileHeader(short type, int size, short reserved1, short reserved2, int offBits) {
        this.type = type;
        this.size = size;
        this.reserved1 = reserved1;
        this.reserved2 = reserved2;
        this.offBits = offBits;
    }

    public BmpFileHeader(int size, int offBits) {
        type = 0x4D42;
        this.size = size;
        this.offBits = offBits;
    }

    /**
     * Writes this header to an output stream.
     */
    public void writeTo(OutputStream stream) throws IOException {
        StreamUtils.writeShort(stream, type);
        StreamUtils.writeInt(stream, size);
        StreamUtils.writeShort(stream, reserved1);
        StreamUtils.writeShort(stream, reserved2);
        StreamUtils.writeInt(stream, offBits);
    }

    /**
     * Creates an instance of this header by reading it from a stream.
     */
    public static BmpFileHeader readFrom(InputStream stream) throws IOException {
        short type = StreamUtils.readShort(stream);
        int size = StreamUtils.readInt(stream);
        short reserved1 = StreamUtils.readShort(stream);
        short reserved2 = StreamUtils.readShort(stream);
        int offBits = StreamUtils.readInt(stream);
        return new BmpFileHeader(type, size, reserved1, reserved2, offBits);
    }
}
