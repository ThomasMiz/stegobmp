package grupo3.encryption.algorithms;

public class AES192Encryption extends AbstractAESEncryption {

    private static final int keyBitsSize = 192;

    private static final AES192Encryption instance = new AES192Encryption();

    private AES192Encryption() {
        super(keyBitsSize);
    }

    public static AES192Encryption getInstance() {
        return instance;
    }
}
