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
    private static final int SALT_SIZE_BYTES = 8;
    private static final byte[] FIXED_SALT = new byte[SALT_SIZE_BYTES];

    private final String algorithmName;
    private final int keySizeBits;
    private final int blockSizeBytes;

    EncryptionAlgorithm(String algorithmName, int keySizeBits, int blockSizeBytes) {
        this.algorithmName = algorithmName;
        this.keySizeBits = keySizeBits;
        this.blockSizeBytes = blockSizeBytes;
    }

    public byte[] encrypt(byte[] input, EncryptionMode mode, String password) throws Exception {

        final KeyAndIv keyAndIv = this.deriveKeyAndIV(password, this.blockSizeBytes, this.keySizeBits, this.algorithmName);
        final String transformation = this.algorithmName + "/" + mode.getMode() + (input.length % this.blockSizeBytes == 0 ? "NoPadding" : "PKCS5Padding");
        final Cipher cipher = Cipher.getInstance(transformation);

        cipher.init(Cipher.ENCRYPT_MODE, keyAndIv.key, keyAndIv.iv);

        final byte[] cipherText = cipher.doFinal(input);
        final byte[] combined = new byte[keyAndIv.iv.getIV().length + cipherText.length];
        System.arraycopy(keyAndIv.iv.getIV(), 0, combined, 0, keyAndIv.iv.getIV().length);
        System.arraycopy(cipherText, 0, combined, keyAndIv.iv.getIV().length, cipherText.length);
        return combined;
    }

    public byte[] decrypt(byte[] input, EncryptionMode mode, String password) throws Exception {

        final byte[] cipherText = Arrays.copyOfRange(input, 0, input.length);

        final KeyAndIv keyAndIv = this.deriveKeyAndIV(password, this.blockSizeBytes, this.keySizeBits, this.algorithmName);

        final String transformation = this.algorithmName + "/" + mode.getMode() + "/NoPadding";
        final Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, keyAndIv.key, keyAndIv.iv);

        // TODO: DELETE
        System.out.println("IV: " + byteToHex(cipher.getIV()));
        System.out.println("Key: " + byteToHex(keyAndIv.key.getEncoded()));

        return cipher.doFinal(cipherText);
    }

    private String byteToHex(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    private KeyAndIv deriveKeyAndIV(String password, int blockSizeBytes, int keySizeBits, String algorithm) throws Exception {
        final SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
        final int bits = blockSizeBytes * 8 + keySizeBits;
        final PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), FIXED_SALT, KEY_DERIVATION_ALGORITHM_ITERATIONS, bits);
        final SecretKey tmp = factory.generateSecret(spec);
        byte[] iv = new byte[this.blockSizeBytes];
        byte[] key = new byte[this.keySizeBits / 8];
        System.arraycopy(tmp.getEncoded(), 0, key, 0, this.keySizeBits / 8);
        System.arraycopy(tmp.getEncoded(), this.keySizeBits / 8, iv, 0, this.blockSizeBytes);
        return new KeyAndIv(new SecretKeySpec(key, algorithm), new IvParameterSpec(iv));
    }

    private record KeyAndIv(Key key, IvParameterSpec iv) { }

}

