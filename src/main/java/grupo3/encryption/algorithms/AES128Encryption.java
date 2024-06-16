package grupo3.encryption.algorithms;

public class AES128Encryption extends AbstractAESEncryption {

    private static final int KEY_SIZE_BYTES = 16; // 128 bits

    private static final AES128Encryption instance = new AES128Encryption();

    private AES128Encryption() {
        super(KEY_SIZE_BYTES);
    }

    public static AES128Encryption getInstance() {
        return instance;
    }
}
