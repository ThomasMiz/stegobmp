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
import java.util.Arrays;

public abstract class EncryptionAlgorithm {

    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_DERIVATION_ALGORITHM_ITERATIONS = 10000;
    private static final byte[] NO_SALT = new byte[] {0};

    private final String algorithmName;
    private final int keySizeBits;
    private final int blockSizeBytes;
    private final int saltSizeBytes;

    EncryptionAlgorithm(String algorithmName, int keySizeBits, int blockSizeBytes, int saltSizeBytes) {
        this.algorithmName = algorithmName;
        this.keySizeBits = keySizeBits;
        this.blockSizeBytes = blockSizeBytes;
        this.saltSizeBytes = saltSizeBytes;
    }

    public byte[] encrypt(byte[] input, EncryptionMode mode, String password) throws Exception {
        final byte[] salt = generateSalt(this.saltSizeBytes);

        final Key key = this.deriveKey(password, this.keySizeBits, this.algorithmName, salt);
        final String transformation = this.algorithmName + "/" + mode.getMode() + (input.length % this.blockSizeBytes == 0 ? "NoPadding" : "PKCS5Padding");
        final Cipher cipher = Cipher.getInstance(transformation);

        final IvParameterSpec iv = this.generateIV(this.blockSizeBytes);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] cipherText = cipher.doFinal(input);
        final byte[] combined = new byte[this.saltSizeBytes + iv.getIV().length + cipherText.length];
        System.arraycopy(salt, 0, combined, 0, this.saltSizeBytes);
        System.arraycopy(iv.getIV(), 0, combined, this.saltSizeBytes, iv.getIV().length);
        System.arraycopy(cipherText, 0, combined, this.saltSizeBytes + iv.getIV().length, cipherText.length);
        return combined;
    }

    public byte[] decrypt(byte[] input, EncryptionMode mode, String password) throws Exception {

        final byte[] salt = (this.saltSizeBytes <= 0)? NO_SALT : Arrays.copyOfRange(input, 0, this.saltSizeBytes);
        final byte[] iv = Arrays.copyOfRange(input, this.saltSizeBytes, this.saltSizeBytes + this.blockSizeBytes);
        final byte[] cipherText = Arrays.copyOfRange(input, this.saltSizeBytes + this.blockSizeBytes, input.length);

        final Key key = this.deriveKey(password, this.keySizeBits, this.algorithmName, salt);
        final IvParameterSpec ivSpec = new IvParameterSpec(iv);

        final String transformation = this.algorithmName + "/" + mode.getMode() + (input.length % this.blockSizeBytes == 0 ? "NoPadding" : "PKCS5Padding");
        final Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        // TODO: DELETE
        System.out.println("IV: " + byteToHex(cipher.getIV()));
        System.out.println("Key: " + byteToHex(key.getEncoded()));

        return cipher.doFinal(cipherText);
    }

    private String byteToHex(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    private Key deriveKey(String password, int keySize, String algorithm, byte[] salt) throws Exception {
        final SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
        final PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, KEY_DERIVATION_ALGORITHM_ITERATIONS, keySize);
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