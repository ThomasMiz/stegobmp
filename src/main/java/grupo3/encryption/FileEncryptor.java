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

    public static void encryptFile(EncryptionOptions options, String inputFile, String outputFile) throws Exception {
        byte[] fileBytes = readFile(inputFile);
        byte[] encryptedBytes = options.algorithm().encrypt(fileBytes, options.mode(), options.password());
        writeFile(outputFile, encryptedBytes);
    }

    public static void decryptFile(EncryptionOptions options, String inputFile, String outputFile) throws Exception {
        byte[] fileBytes = readFile(inputFile);
        byte[] decryptedBytes = options.algorithm().decrypt(fileBytes, options.mode(), options.password());
        writeFile(outputFile, decryptedBytes);
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
