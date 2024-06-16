package grupo3.encryption;

/**
 * Lists the supported block cipher primitives.
 */
public enum EncryptionMode {

    /**
     * Indicates Electronic Code Book Mode.
     */
    ECB("ECB", true, false),

    /**
     * Indicates Cipher Block Chaining.
     */
    CBC("CBC", true, true),

    /**
     * Indicates Cipher Feedback Mode.
     */
    CFB("CFB8", false, true),

    /**
     * Indicates Output Feedback Mode.
     */
    OFB("OFB", false, true);

    private final String mode;
    private final boolean usePadding;
    private final boolean useIV;

    EncryptionMode(String mode, boolean usePadding, boolean useIV) {
        this.mode = mode;
        this.usePadding = usePadding;
        this.useIV = useIV;
    }

    public String getMode() {
        return mode;
    }

    public boolean usePadding() {
        return usePadding;
    }

    public boolean useIV() {
        return useIV;
    }
}
