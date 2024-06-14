package grupo3.encryption.algorithms;

abstract class AbstractAESEncryption extends EncryptionAlgorithm {

    private static final String ALGORITHM = "AES";
    private static final int SALT_BYTES_SIZE = 8;

    AbstractAESEncryption(int keyBitsSize) {
        super(ALGORITHM, keyBitsSize);
    }

    @Override
    protected int getSaltBytesSize() {
        return SALT_BYTES_SIZE;
    }
}