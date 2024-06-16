package grupo3.encryption;

/**
 * Lists the supported block cipher modes for encryption.
 */
public enum EncryptionMode {

    /**
     * Electronic Code Book Mode (ECB).
     * Does not use IV, supports padding.
     */
    ECB("ECB", true, false),

    /**
     * Cipher Block Chaining Mode (CBC).
     * Uses IV, supports padding.
     */
    CBC("CBC", true, true),

    /**
     * Cipher Feedback Mode (CFB).
     * Uses IV, does not support padding.
     */
    CFB("CFB8", false, true),

    /**
     * Output Feedback Mode (OFB).
     * Uses IV, does not support padding.
     */
    OFB("OFB", false, true);

    private final String mode;
    private final boolean usePadding;
    private final boolean useIV;

    /**
     * Constructs an EncryptionMode enum instance with the specified parameters.
     *
     * @param mode       The name of the encryption mode.
     * @param usePadding Indicates if padding is used in this mode.
     * @param useIV      Indicates if an initialization vector (IV) is used in this mode.
     */
    EncryptionMode(String mode, boolean usePadding, boolean useIV) {
        this.mode = mode;
        this.usePadding = usePadding;
        this.useIV = useIV;
    }

    /**
     * Retrieves the name of the encryption mode.
     *
     * @return The encryption mode name.
     */
    public String getMode() {
        return mode;
    }

    /**
     * Checks if padding is used in this encryption mode.
     *
     * @return True if padding is used, false otherwise.
     */
    public boolean usePadding() {
        return usePadding;
    }

    /**
     * Checks if an initialization vector (IV) is used in this encryption mode.
     *
     * @return True if IV is used, false otherwise.
     */
    public boolean useIV() {
        return useIV;
    }
}
