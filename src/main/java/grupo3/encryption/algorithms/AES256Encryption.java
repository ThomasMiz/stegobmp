package grupo3.encryption.algorithms;

public class AES256Encryption extends AbstractAESEncryption {

    private static final int KEY_SIZE_BYTES = 32; // 256 bits

    private static final AES256Encryption instance = new AES256Encryption();

    private AES256Encryption() {
        super(KEY_SIZE_BYTES);
    }

    public static AES256Encryption getInstance() {
        return instance;
    }
}
