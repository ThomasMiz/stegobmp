package grupo3.encryption.algorithms;

public class AES128Encryption extends AbstractAESEncryption {

    private static final int keyBitsSize = 128;

    private static final AES128Encryption instance = new AES128Encryption();

    private AES128Encryption() {
        super(keyBitsSize);
    }

    public static AES128Encryption getInstance() {
        return instance;
    }
}
