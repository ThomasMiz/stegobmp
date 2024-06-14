package grupo3.encryption;

/**
 * Lists the supported block cipher primitives.
 */
public enum EncryptionMode {

    /**
     * Indicates Electronic Code Book Mode.
     */
    ECB("ECB", true),

    /**
     * Indicates Cipher Block Chaining.
     */
    CBC("CBC", true),

    /**
     * Indicates Cipher Feedback Mode.
     */
    CFB("CFB8", false),

    /**
     * Indicates Output Feedback Mode.
     */
    OFB("OFB", false);

    private final String mode;
    private final boolean usePadding;

    EncryptionMode(String mode, boolean usePadding) {
        this.mode = mode;
        this.usePadding = usePadding;
    }

    public String getMode() {
        return mode;
    }

    public boolean usePadding() {
        return usePadding;
    }
}
