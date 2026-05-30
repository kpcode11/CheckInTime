package com.example.studentmanager.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {}

    public static String hash(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(12));
    }

    public static boolean verify(String plaintext, String hash) {
        // Guard: if stored value is plaintext (legacy), fall back to direct compare
        if (!hash.startsWith("$2")) {
            return plaintext.equals(hash);
        }
        return BCrypt.checkpw(plaintext, hash);
    }
}
