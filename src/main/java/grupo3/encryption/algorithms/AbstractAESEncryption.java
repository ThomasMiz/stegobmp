package grupo3.encryption.algorithms;

abstract class AbstractAESEncryption extends EncryptionAlgorithm {

    private static final String ALGORITHM = "AES";
    private static final int BLOCK_SIZE_BYTES = 16;
    private static final int SALT_SIZE_BYTES = 0;

    AbstractAESEncryption(int keySizeBits) {
        super(ALGORITHM, keySizeBits, BLOCK_SIZE_BYTES, SALT_SIZE_BYTES);
    }
}