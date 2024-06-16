package grupo3.encryption.algorithms;

/**
 * Abstract class for AES encryption algorithms
 */
abstract class AbstractAESEncryption extends EncryptionAlgorithm {

    private static final String ALGORITHM = "AES";
    private static final int BLOCK_SIZE_BYTES = 16;

    /**
     * Constructs an AbstractAESEncryption instance with the specified key size.
     *
     * @param keySizeBytes The size of the AES encryption key in bytes.
     */
    AbstractAESEncryption(int keySizeBytes) {
        super(ALGORITHM, keySizeBytes, BLOCK_SIZE_BYTES);
    }
}