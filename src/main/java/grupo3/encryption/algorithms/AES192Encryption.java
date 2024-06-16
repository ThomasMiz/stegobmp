package grupo3.encryption.algorithms;

public class AES192Encryption extends AbstractAESEncryption {

    private static final int KEY_SIZE_BYTES = 24; // 192 bits

    private static final AES192Encryption instance = new AES192Encryption();

    private AES192Encryption() {
        super(KEY_SIZE_BYTES);
    }

    public static AES192Encryption getInstance() {
        return instance;
    }
}
