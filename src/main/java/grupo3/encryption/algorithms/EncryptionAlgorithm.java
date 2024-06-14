package grupo3.encryption.algorithms;

import grupo3.encryption.EncryptionMode;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

public abstract class EncryptionAlgorithm {

    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_DERIVATION_ALGORITHM_ITERATIONS = 10000;
    private static final byte[] NO_SALT = new byte[] {0};
    private static final int BLOCK_BYTES_SIZE = 16;

    private final String algorithmName;
    private final int keyBitsSize;

    protected abstract int getSaltBytesSize();

    EncryptionAlgorithm(String algorithmName, int keyBitsSize) {
        this.algorithmName = algorithmName;
        this.keyBitsSize = keyBitsSize;
    }

    public byte[] encrypt(byte[] input, EncryptionMode mode, String password) throws Exception {
        final int saltLength = this.getSaltBytesSize();
        final byte[] salt = generateSalt(saltLength);

        final Key key = this.deriveKey(password, this.keyBitsSize, this.algorithmName, salt);
        final String transformation = this.algorithmName + "/" + mode.getMode() + (mode.usePadding() ? "/PKCS5Padding" : "/NoPadding");
        final Cipher cipher = Cipher.getInstance(transformation);

        final IvParameterSpec iv = this.generateIV(BLOCK_BYTES_SIZE);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] cipherText = cipher.doFinal(input);
        final byte[] combined = new byte[saltLength + iv.getIV().length + cipherText.length];
        System.arraycopy(salt, 0, combined, 0, saltLength);
        System.arraycopy(iv.getIV(), 0, combined, saltLength, iv.getIV().length);
        System.arraycopy(cipherText, 0, combined, saltLength + iv.getIV().length, cipherText.length);
        return combined;
    }

    public byte[] decrypt(byte[] input, EncryptionMode mode, String password) throws Exception {
        final int saltLength = this.getSaltBytesSize();
        final byte[] salt = (saltLength <= 0)? NO_SALT : Arrays.copyOfRange(input, 0, saltLength);
        final byte[] iv = Arrays.copyOfRange(input, saltLength, saltLength + BLOCK_BYTES_SIZE);
        final byte[] cipherText = Arrays.copyOfRange(input, saltLength + BLOCK_BYTES_SIZE, input.length);

        final Key key = this.deriveKey(password, this.keyBitsSize, this.algorithmName, salt);
        final IvParameterSpec ivSpec = new IvParameterSpec(iv);

        final String transformation = this.algorithmName + "/" + mode.getMode() + (mode.usePadding() ? "/PKCS5Padding" : "/NoPadding");
        final Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher.doFinal(cipherText);
    }

    private Key deriveKey(String password, int keySize, String algorithm, byte[] salt) throws Exception {
        final SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
        final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, KEY_DERIVATION_ALGORITHM_ITERATIONS, keySize);
        final SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), algorithm);
    }

    private IvParameterSpec generateIV(int blockBytesSize) {
        final byte[] iv = new byte[blockBytesSize];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private byte[] generateSalt(int saltBytesSize) {
       return (saltBytesSize <= 0)? NO_SALT : new byte[saltBytesSize];
    }
}