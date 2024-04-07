package grupo3.arguments;

/**
 * Represents the encryption options specified by a user
 *
 * @param algorithm The encryption algorithm to use.
 * @param mode      The encryption mode to use.
 * @param password  The password to encrypt with.
 */
public record EncryptionOptions(
        Object algorithm, // TODO: Change type once encryption is implemented.
        EncryptionMode mode,
        String password
) {
    /**
     * The encryption algorithm to use.
     */
    public Object algorithm() {
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
