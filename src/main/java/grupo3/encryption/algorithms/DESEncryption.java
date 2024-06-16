package grupo3.encryption.algorithms;

public class DESEncryption extends EncryptionAlgorithm {

    private static final String ALGORITHM = "DESede";
    private static final int KEY_SIZE_BYTES = 24; // 192 bits;
    private static final int BLOCK_SIZE_BYTES = 8;

    private DESEncryption() {
        super(ALGORITHM, KEY_SIZE_BYTES, BLOCK_SIZE_BYTES);
    }

    private static final DESEncryption instance = new DESEncryption();

    public static EncryptionAlgorithm getInstance() {
        return instance;
    }
}
