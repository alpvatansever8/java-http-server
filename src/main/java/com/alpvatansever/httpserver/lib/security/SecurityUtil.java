package com.alpvatansever.httpserver.lib.security;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SecurityUtil {
    private SecurityUtil() {
    }


    public static byte[] base64Decoding(String secret) {
        return Base64.getDecoder().decode(secret);
    }

    public static String base64Encoding(byte[] inputs) {
        return Base64.getEncoder().encodeToString(inputs);
    }

    public static String decodeToString(String base64Secret) {
        return new String(base64Decoding(base64Secret), UTF_8);
    }
}
