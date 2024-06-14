package grupo3.encryption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class FileEncryptor {

    private FileEncryptor() {
        throw new AssertionError();
    }

    public static void encryptFile(EncryptionOptions options, String filePath) throws Exception {
        final Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new IOException("File does not exists in " + filePath);
        }

        final byte[] fileBytes = Files.readAllBytes(path);
        final byte[] encryptedBytes = options.algorithm().encrypt(fileBytes, options.mode(), options.password());
        final String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
        Files.write(path, encryptedBase64.getBytes());
    }

    public static void decryptFile(EncryptionOptions options, String filePath) throws Exception {
        final Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new IOException("File does not exists in " + filePath);
        }

        final byte[] fileBytes = Files.readAllBytes(path);
        final String encryptedBase64 = new String(fileBytes);
        final byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
        final byte[] decryptedBytes = options.algorithm().decrypt(fileBytes, options.mode(), options.password());
        Files.write(path, decryptedBytes);
    }
}
