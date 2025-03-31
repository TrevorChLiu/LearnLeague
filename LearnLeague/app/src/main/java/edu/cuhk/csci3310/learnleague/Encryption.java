package edu.cuhk.csci3310.learnleague;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {
    /**
     * Encrypt a string using sha256 utf-8
     * @param raw The secret string
     * @return Hashed string of row
     */
    public static String sha256Hash (String raw) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hashedStringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                hashedStringBuilder.append(String.format("%02x", b));
            }
            return hashedStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
