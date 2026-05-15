package cloud.liang.common;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public final class PasswordUtil {
    private static final String PREFIX = "pbkdf2_sha256";
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(password, salt, ITERATIONS);
        return PREFIX + ":" + ITERATIONS + ":"
                + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean isHashedPassword(String storedPassword) {
        return storedPassword != null && storedPassword.startsWith(PREFIX + ":");
    }

    public static boolean verifyPassword(String password, String storedPassword) {
        if (!isHashedPassword(storedPassword)) {
            return false;
        }

        String[] parts = storedPassword.split(":");
        if (parts.length != 4) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
            byte[] actualHash = pbkdf2(password, salt, iterations);
            return constantTimeEquals(expectedHash, actualHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(String password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Unable to hash password", e);
        }
    }

    private static boolean constantTimeEquals(byte[] expected, byte[] actual) {
        if (expected.length != actual.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < expected.length; i++) {
            result |= expected[i] ^ actual[i];
        }
        return result == 0;
    }
}
