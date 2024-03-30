package grupo3.bmp;

import grupo3.bmp.file.BmpFileHeader;
import grupo3.bmp.file.BmpInfoHeader;
import grupo3.exceptions.BmpException;
import grupo3.utils.StreamUtils;

import java.io.*;

public class Bitmap {
    private final int width;
    private final int height;
    private final byte[] data;

    public Bitmap(int width, int height, byte[] data) {
        if (width * height * 3 != data.length) {
            throw new IllegalArgumentException("The length of the data array must equal width*height*3");
        }

        this.width = width;
        this.height = height;
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getData() {
        return data;
    }

    public void writeToFile(String filename) throws IOException {
        try (OutputStream stream = new FileOutputStream(filename)) {
            writeToStream(stream);
        }
    }

    public void writeToStream(OutputStream stream) throws IOException {
        int widthInBytes = this.width * 3;
        int paddingSize = (4 - (widthInBytes) % 4) % 4;

        BmpFileHeader fileHeader = new BmpFileHeader(
                BmpFileHeader.SIZE + BmpInfoHeader.SIZE + widthInBytes * this.height,
                BmpFileHeader.SIZE + BmpInfoHeader.SIZE
        );
        fileHeader.writeTo(stream);

        BmpInfoHeader infoHeader = new BmpInfoHeader(this.width, this.height);
        infoHeader.writeTo(stream);

        int stride = this.width * 3;
        for (int i = 0; i < this.height; i++) {
            stream.write(this.data, i * stride, stride);
            StreamUtils.writeZeroes(stream, paddingSize);
        }
    }

    public static Bitmap readFromFile(String filename) throws IOException {
        try (InputStream stream = new FileInputStream(filename)) {
            return readFromStream(stream);
        }
    }

    public static Bitmap readFromStream(InputStream stream) throws IOException {
        BmpFileHeader fileHeader = BmpFileHeader.readFrom(stream);
        BmpInfoHeader infoHeader = BmpInfoHeader.readFrom(stream);

        if (infoHeader.width <= 0 || infoHeader.height <= 0) {
            throw new BmpException("The width and height in the file header must be greater than 0");
        }

        int widthInBytes = infoHeader.width * 3;
        int paddingSize = (4 - (widthInBytes) % 4) % 4;
        int stride = infoHeader.width * 3;

        byte[] data = new byte[infoHeader.width * infoHeader.height * 3];

        for (int i = 0; i < infoHeader.height; i++) {
            StreamUtils.readExact(stream, data, i * stride, stride);
            StreamUtils.readIgnore(stream, paddingSize);
        }

        return new Bitmap(infoHeader.width, infoHeader.height, data);
    }
}
