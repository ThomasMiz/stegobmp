package grupo3.encryption;

import grupo3.encryption.algorithms.EncryptionAlgorithm;
import grupo3.exceptions.EncryptionException;

/**
 * Represents the encryption options specified by a user
 *
 * @param algorithm The encryption algorithm to use.
 * @param mode      The encryption mode to use.
 * @param password  The password to encrypt with.
 */
public record EncryptionOptions(
        EncryptionAlgorithm algorithm,
        EncryptionMode mode,
        String password
) {
    /**
     * The encryption algorithm to use.
     */
    @Override
    public EncryptionAlgorithm algorithm() {
        return algorithm;
    }

    /**
     * The encryption mode to use.
     */
    @Override
    public EncryptionMode mode() {
        return mode;
    }

    /**
     * The password to encrypt with.
     */
    @Override
    public String password() {
        return password;
    }

    /**
     * Encrypts the given data using the specified algorithm, mode, and password.
     *
     * @param data The data to encrypt.
     * @return The encrypted data.
     * @throws EncryptionException If an error occurs during encryption.
     */
    public byte[] encrypt(byte[] data) throws EncryptionException {
        try {
            return algorithm.encrypt(data, mode, password);
        } catch (Exception e) {
            throw new EncryptionException("Error encrypting data: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypts the given data using the specified algorithm, mode, and password.
     *
     * @param encryptedData The data to decrypt.
     * @return The decrypted data.
     * @throws EncryptionException If an error occurs during decryption.
     */
    public byte[] decrypt(byte[] encryptedData) throws EncryptionException {
        try {
            return algorithm.decrypt(encryptedData, mode, password);
        } catch (Exception e) {
            throw new EncryptionException("Error decrypting data: " + e.getMessage(), e);
        }
    }
}
