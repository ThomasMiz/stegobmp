package grupo3.encryption.algorithms;

public class AES256Encryption extends AbstractAESEncryption {

    private static final int keySizeBits = 256;

    private static final AES256Encryption instance = new AES256Encryption();

    private AES256Encryption() {
        super(keySizeBits);
    }

    public static AES256Encryption getInstance() {
        return instance;
    }
}
