package cn.nekocode.murmur.data.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public final class CryptoUtils {
    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public static final String ENC_UTF8 = "UTF-8";

    public static final class HASH {
        private static final String MD5 = "MD5";
        private static final String SHA_1 = "SHA-1";
        private static final String SHA_256 = "SHA-256";

        public static String md5(byte[] data) {
            return new String(CryptoUtils.binToHex(md5Bytes(data)));
        }

        public static String md5(String text) {
            return new String(CryptoUtils.binToHex(md5Bytes(CryptoUtils.getRawBytes(text))));
        }

        public static byte[] md5Bytes(byte[] data) {
            return getDigest(MD5).digest(data);
        }

        public static String sha1(byte[] data) {
            return new String(CryptoUtils.binToHex(sha1Bytes(data)));
        }

        public static String sha1(String text) {
            return new String(CryptoUtils.binToHex(sha1Bytes(CryptoUtils.getRawBytes(text))));
        }

        public static byte[] sha1Bytes(byte[] data) {
            return getDigest(SHA_1).digest(data);
        }

        public static String sha256(byte[] data) {
            return new String(CryptoUtils.binToHex(sha256Bytes(data)));
        }

        public static String sha256(String text) {
            return new String(CryptoUtils.binToHex(sha256Bytes(CryptoUtils.getRawBytes(text))));
        }

        public static byte[] sha256Bytes(byte[] data) {
            return getDigest(SHA_256).digest(data);
        }

        private static MessageDigest getDigest(String algorithm) {
            try {
                return MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static class PBE {
        public static final String ALGORITHM = "PBEWITHMD5andDES";
        public static final String ENC_UTF8 = "UTF-8";
        public static final int ITERATION_COUNT = 100;
        public static final byte[] SIMPLE_SALT = new byte[]{(byte) 2, (byte) 0, (byte) 1, (byte) 5, (byte) 0, (byte) 4, (byte) 0, (byte) 1};

        public static byte[] randomSalt() throws Exception {
            return new SecureRandom().generateSeed(8);
        }

        private static Key generateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(new PBEKeySpec(password.toCharArray()));
        }

        public static String encrypt(String data, String password) {
            return encrypt(data, password, SIMPLE_SALT);
        }

        public static String decrypt(String data, String password) {
            return decrypt(data, password, SIMPLE_SALT);
        }

        public static String encrypt(String data, String password, byte[] salt) {
            try {
                return new String(CryptoUtils.binToHex(encrypt(data.getBytes("UTF-8"), password, salt)));
            } catch (Exception e) {
                return null;
            }
        }

        public static String decrypt(String data, String password, byte[] salt) {
            try {
                return new String(decrypt(CryptoUtils.hexToBin(data), password, salt), "UTF-8");
            } catch (Exception e) {
                return null;
            }
        }

        public static byte[] encrypt(byte[] data, String password, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
            Key key = generateKey(password);
            PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(1, key, paramSpec);
            return cipher.doFinal(data);
        }

        public static byte[] decrypt(byte[] data, String password, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
            Key key = generateKey(password);
            PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(2, key, paramSpec);
            return cipher.doFinal(data);
        }
    }

    private CryptoUtils() {
    }

    private static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[(l << 1)];
        int j = 0;
        for (int i = 0; i < l; i++) {
            int i2 = j + 1;
            out[j] = toDigits[(data[i] & 240) >>> 4];
            j = i2 + 1;
            out[i2] = toDigits[data[i] & 15];
        }
        return out;
    }

    private static char[] encodeHex(byte[] data, boolean lowerCase) {
        return encodeHex(data, lowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static char[] binToHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static byte[] hexToBin(String hex) {
        int len = hex.length();
        byte[] data = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    static byte[] getRawBytes(String text) {
        try {
            return text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return text.getBytes();
        }
    }

    static String getString(byte[] data) {
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new String(data);
        }
    }
}
