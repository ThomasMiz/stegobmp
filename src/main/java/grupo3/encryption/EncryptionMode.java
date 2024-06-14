package grupo3.encryption;

/**
 * Lists the supported block cipher primitives.
 */
public enum EncryptionMode {

    /**
     * Indicates Electronic Code Book Mode.
     */
    ECB("ECB"),

    /**
     * Indicates Cipher Block Chaining.
     */
    CBC("CBC"),

    /**
     * Indicates Cipher Feedback Mode.
     */
    CFB("CFB8"),

    /**
     * Indicates Output Feedback Mode.
     */
    OFB("OFB");

    private final String mode;

    EncryptionMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
