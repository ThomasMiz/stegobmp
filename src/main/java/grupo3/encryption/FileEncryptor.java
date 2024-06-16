package grupo3.encryption;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileEncryptor {

    private FileEncryptor() {
        throw new AssertionError();
    }

    public static byte[] encryptFile(EncryptionOptions options, String inputFile) throws Exception {
        byte[] fileBytes = readFile(inputFile);
        // tamaño || fileBytes || extension
        return options.algorithm().encrypt(fileBytes, options.mode(), options.password());
        //writeFile(outputFile, encryptedBytes);
    }

    public static void decryptFile(EncryptionOptions options, byte[] encryptedMessage, String outFileName) throws IOException {
        byte[] decryptedBytes;

        try{
           decryptedBytes = options.algorithm().decrypt(encryptedMessage, options.mode(), options.password());
       } catch (Exception e) {
           throw new RuntimeException("Error decrypting file: " + e.getMessage());
       }

        // decryptedBytes contains  realSize||data||”.extension\0”
        int realSize = (decryptedBytes[0] & 0xFF) << 24 |
                (decryptedBytes[1] & 0xFF) << 16 |
                (decryptedBytes[2] & 0xFF) << 8 |
                (decryptedBytes[3] & 0xFF);
        byte[] data = new byte[realSize];
        System.arraycopy(decryptedBytes, 4, data, 0, realSize);
        byte[] fileExtension = new byte[decryptedBytes.length - 4 - realSize-1];
        System.arraycopy(decryptedBytes, 4 + realSize, fileExtension, 0, decryptedBytes.length - 4 - realSize - 1);
        String outputFile = outFileName + new String(fileExtension);
        writeFile(outputFile, data);


    }

    private static byte[] readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File does not exists in " + filePath);
        }
        return Files.readAllBytes(path);
    }

    private static void writeFile(String filePath, byte[] data) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(Paths.get(filePath).toFile())) {
            stream.write(data);
        }
    }
}
