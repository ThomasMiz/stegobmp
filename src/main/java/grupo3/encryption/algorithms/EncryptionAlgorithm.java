package grupo3.encryption.algorithms;

import grupo3.encryption.EncryptionMode;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Abstract base class for encryption algorithms using symmetric keys.
 */
public abstract class EncryptionAlgorithm {

    private static final String SECRET_KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SECRET_KEY_DERIVATION_ALGORITHM_ITERATIONS = 10000;
    private static final int SALT_SIZE_BYTES = 8;
    private static final byte[] FIXED_SALT = new byte[SALT_SIZE_BYTES];
    private static final byte[] NO_SALT = new byte[]{0};

    private final String algorithmName;
    private final int keySizeBytes;
    private final int blockSizeBytes;

    /**
     * Constructs an EncryptionAlgorithm instance.
     *
     * @param algorithmName  The name of the encryption algorithm.
     * @param keySizeBytes   The size of the encryption key in bytes.
     * @param blockSizeBytes The size of the encryption block in bytes.
     */
    EncryptionAlgorithm(String algorithmName, int keySizeBytes, int blockSizeBytes) {
        this.algorithmName = algorithmName;
        this.keySizeBytes = keySizeBytes;
        this.blockSizeBytes = blockSizeBytes;
    }

    /**
     * Encrypts the given input data.
     *
     * @param input    The input data to be encrypted.
     * @param mode     The encryption mode specifying encryption details.
     * @param password The password used for encryption key derivation.
     * @return The encrypted data.
     * @throws InvalidAlgorithmParameterException If invalid algorithm parameters are encountered.
     * @throws NoSuchPaddingException             If no such padding scheme is available.
     * @throws IllegalBlockSizeException          If the block size is illegal for the cipher.
     * @throws NoSuchAlgorithmException           If no such algorithm exists.
     * @throws InvalidKeySpecException            If the provided key specification is invalid.
     * @throws BadPaddingException                If invalid padding is encountered.
     * @throws InvalidKeyException                If an invalid key is encountered.
     */
    public byte[] encrypt(byte[] input, EncryptionMode mode, String password) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException,
            BadPaddingException, InvalidKeyException {
        return processCipher(Cipher.ENCRYPT_MODE, input, mode, password);
    }

    /**
     * Decrypts the given input data.
     *
     * @param input    The input data to be decrypted.
     * @param mode     The encryption mode specifying decryption details.
     * @param password The password used for decryption key derivation.
     * @return The decrypted data.
     * @throws InvalidAlgorithmParameterException If invalid algorithm parameters are encountered.
     * @throws NoSuchPaddingException             If no such padding scheme is available.
     * @throws IllegalBlockSizeException          If the block size is illegal for the cipher.
     * @throws NoSuchAlgorithmException           If no such algorithm exists.
     * @throws InvalidKeySpecException            If the provided key specification is invalid.
     * @throws BadPaddingException                If invalid padding is encountered.
     * @throws InvalidKeyException                If an invalid key is encountered.
     */
    public byte[] decrypt(byte[] input, EncryptionMode mode, String password) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException,
            BadPaddingException, InvalidKeyException {
        return processCipher(Cipher.DECRYPT_MODE, input, mode, password);
    }

    /**
     * Processes encryption or decryption based on the cipher mode.
     *
     * @param cipherMode The cipher mode (ENCRYPT_MODE or DECRYPT_MODE).
     * @param input      The input data to be processed.
     * @param mode       The encryption mode specifying details.
     * @param password   The password used for key derivation.
     * @return The processed data (encrypted or decrypted).
     * @throws NoSuchAlgorithmException           If no such algorithm exists.
     * @throws InvalidKeySpecException            If the provided key specification is invalid.
     * @throws NoSuchPaddingException             If no such padding scheme is available.
     * @throws InvalidAlgorithmParameterException If invalid algorithm parameters are encountered.
     * @throws InvalidKeyException                If an invalid key is encountered.
     * @throws IllegalBlockSizeException          If the block size is illegal for the cipher.
     * @throws BadPaddingException                If invalid padding is encountered.
     */
    private byte[] processCipher(int cipherMode, byte[] input, EncryptionMode mode, String password) throws NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        final int secretKeyLengthBits = bytesToBits(keySizeBytes + (mode.useIV() ? blockSizeBytes : 0));
        final SecretKey derivedKey = deriveSecretKey(password, secretKeyLengthBits);

        final Key key = extractKey(derivedKey, keySizeBytes, algorithmName);
        final IvParameterSpec iv = mode.useIV() ? extractIV(derivedKey, keySizeBytes, blockSizeBytes) : null;

        final String transformation = algorithmName + "/" + mode.getMode() + (mode.usePadding() ? "/PKCS5Padding" : "/NoPadding");
        final Cipher cipher = Cipher.getInstance(transformation);

        cipher.init(cipherMode, key, iv);
        return cipher.doFinal(input);
    }

    /**
     * Derives a secret key using PBKDF2 with HMAC SHA-256.
     *
     * @param password            The password used for key derivation.
     * @param secretKeyLengthBits The desired length of the secret key in bits.
     * @return The derived secret key.
     * @throws NoSuchAlgorithmException If no such algorithm exists.
     * @throws InvalidKeySpecException  If the provided key specification is invalid.
     */
    private SecretKey deriveSecretKey(String password, int secretKeyLengthBits) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_DERIVATION_ALGORITHM);
        final byte[] salt = (SALT_SIZE_BYTES == 0) ? NO_SALT : FIXED_SALT;
        final PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, SECRET_KEY_DERIVATION_ALGORITHM_ITERATIONS, secretKeyLengthBits);
        return factory.generateSecret(spec);
    }

    /**
     * Extracts a symmetric encryption key from a derived secret key.
     *
     * @param derivedKey   The derived secret key containing both key and IV.
     * @param keySizeBytes The size of the encryption key in bytes.
     * @param algorithm    The name of the encryption algorithm.
     * @return The extracted encryption key.
     */
    private Key extractKey(SecretKey derivedKey, int keySizeBytes, String algorithm) {
        byte[] keyBytes = new byte[keySizeBytes];
        System.arraycopy(derivedKey.getEncoded(), 0, keyBytes, 0, keySizeBytes);
        return new SecretKeySpec(keyBytes, algorithm);
    }

    /**
     * Extracts an initialization vector (IV) from a derived secret key.
     *
     * @param derivedKey     The derived secret key containing both key and IV.
     * @param keySizeBytes   The size of the encryption key in bytes.
     * @param blockSizeBytes The size of the encryption block in bytes.
     * @return The extracted initialization vector (IV).
     */
    private IvParameterSpec extractIV(SecretKey derivedKey, int keySizeBytes, int blockSizeBytes) {
        byte[] ivBytes = new byte[blockSizeBytes];
        System.arraycopy(derivedKey.getEncoded(), keySizeBytes, ivBytes, 0, blockSizeBytes);
        return new IvParameterSpec(ivBytes);
    }

    /**
     * Converts bytes to bits.
     *
     * @param bytes The number of bytes.
     * @return The equivalent number of bits.
     */
    private int bytesToBits(int bytes) {
        return bytes * Byte.SIZE;
    }

    /**
     * https://stackoverflow.com/questions/9655181/java-convert-a-byte-array-to-a-hex-string
     * Converts a byte array to a hexadecimal string representation.
     * This method is specifically designed for debugging purposes to display
     * the hexadecimal values of the IV (Initialization Vector) and derived KEY.
     *
     * @param bytes The byte array to be converted.
     * @return A string containing hexadecimal representation of the byte array.
     **/
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    private static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
