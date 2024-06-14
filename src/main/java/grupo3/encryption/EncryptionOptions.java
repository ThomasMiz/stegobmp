package grupo3.encryption;

import grupo3.encryption.algorithms.EncryptionAlgorithm;

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
}
