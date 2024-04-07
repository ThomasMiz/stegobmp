package grupo3.arguments;

/**
 * Lists the supported block cipher primitives.
 */
public enum EncryptionMode {
    /**
     * Indicates Electronic Code Book Mode.
     */
    ECB,

    /**
     * Indicates Cipher Block Chaining.
     */
    CBC,

    /**
     * Indicates Cipher Feedback Mode.
     */
    CFB,

    /**
     * Indicates Output Feedback Mode.
     */
    OFB,
}
