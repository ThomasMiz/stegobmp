package grupo3.encryption.algorithms;

public class DESEncryption extends EncryptionAlgorithm {

    private static final String ALGORITHM = "DESede";
    private static final int KEY_BITS_SIZE = 192;
    private static final int SALT_BYTES_SIZE = 8;

    private DESEncryption() {
        super(ALGORITHM, KEY_BITS_SIZE);
    }

    private static final DESEncryption instance = new DESEncryption();

    public static EncryptionAlgorithm getInstance() {
        return instance;
    }

    @Override
    protected int getSaltBytesSize() {
        return SALT_BYTES_SIZE;
    }
}
