package grupo3.bmp.file;

import grupo3.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BmpInfoHeader {
    /**
     * The size in bytes this header uses when written to a file.
     */
    public static final int SIZE = 40;

    /**
     * The u32 size of this header, in bytes (40).
     */
    public int size;

    /**
     * The i32 bitmap width in pixels (signed integer).
     */
    public int width;

    /**
     * The i32 bitmap height in pixels (signed integer).
     */
    public int height;

    /**
     * The u16 number of color planes (must be 1).
     */
    public short planes;

    /**
     * The u16 number of bits per pixel, which is the color depth of the image. Typical values are 1, 4, 8, 16, 24 and 32.
     */
    public short bitCount;

    /**
     * The u32 compression method being used. Set to 0 for no compression.
     */
    public int compression;

    /**
     * The u32 image size. This is the size of the raw bitmap data; a dummy 0 can be given for BI_RGB bitmaps.
     */
    public int sizeImage;

    /**
     * The i32 horizontal resolution of the image. (pixel per metre, signed integer).
     */
    public int pixelsPerMeterX;

    /**
     * The i32 vertical resolution of the image. (pixel per metre, signed integer).
     */
    public int pixelsPerMeterY;

    /**
     * The u32 number of colors in the color palette, or 0 to default to 2n.
     */
    public int colorUsed;

    /**
     * The u32 number of important colors used, or 0 when every color is important; generally ignored.
     */
    public int colorImportant;

    public BmpInfoHeader(int size, int width, int height, short planes, short bitCount, int compression, int sizeImage, int pixelsPerMeterX, int pixelsPerMeterY, int colorUsed, int colorImportant) {
        this.size = size;
        this.width = width;
        this.height = height;
        this.planes = planes;
        this.bitCount = bitCount;
        this.compression = compression;
        this.sizeImage = sizeImage;
        this.pixelsPerMeterX = pixelsPerMeterX;
        this.pixelsPerMeterY = pixelsPerMeterY;
        this.colorUsed = colorUsed;
        this.colorImportant = colorImportant;
    }

    public BmpInfoHeader(int width, int height) {
        size = 40;
        this.width = width;
        this.height = height;
        planes = 1;
        bitCount = 24;
    }

    /**
     * Creates an instance of this header by reading it from a stream.
     */
    public static BmpInfoHeader readFrom(InputStream stream) throws IOException {
        int size = StreamUtils.readInt(stream);
        int width = StreamUtils.readInt(stream);
        int height = StreamUtils.readInt(stream);
        short planes = StreamUtils.readShort(stream);
        short bitCount = StreamUtils.readShort(stream);
        int compression = StreamUtils.readInt(stream);
        int sizeImage = StreamUtils.readInt(stream);
        int pixelsPerMeterX = StreamUtils.readInt(stream);
        int pixelsPerMeterY = StreamUtils.readInt(stream);
        int colorUsed = StreamUtils.readInt(stream);
        int colorImportant = StreamUtils.readInt(stream);
        return new BmpInfoHeader(size, width, height, planes, bitCount, compression, sizeImage, pixelsPerMeterX, pixelsPerMeterY, colorUsed, colorImportant);
    }

    /**
     * Writes this header to an output stream.
     */
    public void writeTo(OutputStream stream) throws IOException {
        StreamUtils.writeInt(stream, size);
        StreamUtils.writeInt(stream, width);
        StreamUtils.writeInt(stream, height);
        StreamUtils.writeShort(stream, planes);
        StreamUtils.writeShort(stream, bitCount);
        StreamUtils.writeInt(stream, compression);
        StreamUtils.writeInt(stream, sizeImage);
        StreamUtils.writeInt(stream, pixelsPerMeterX);
        StreamUtils.writeInt(stream, pixelsPerMeterY);
        StreamUtils.writeInt(stream, colorUsed);
        StreamUtils.writeInt(stream, colorImportant);
    }
}
