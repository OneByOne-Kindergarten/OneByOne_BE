package com.onebyone.kindergarten.domain.websocket.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import java.util.stream.Collectors;

public final class ChatRoomHashUtil {

    private ChatRoomHashUtil() {}

    public static String generate(Set<Long> memberIds) {
        String raw = memberIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return sha256(raw);
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Hash generation failed", e);
        }
    }
}
