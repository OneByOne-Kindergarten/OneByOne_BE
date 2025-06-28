package com.onebyone.kindergarten.domain.user.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ApplePublicKeyResponse {
    private List<Key> keys;

    @Data
    public static class Key {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }
} 