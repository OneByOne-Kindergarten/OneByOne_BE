package com.onebyone.kindergarten.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @Value("${firebase.service-account}")
    private String serviceAccountBase64;

    @PostConstruct
    public void init(){
        try{
//            InputStream serviceAccount = new ClassPathResource("OnebyoneFirebaseKey.json").getInputStream();

            // 파이어베이스 인증키 파일
            InputStream serviceAccount = new ByteArrayInputStream(getBase64DecodeBytes(serviceAccountBase64));

            /// 파이어베이스 초기화
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            /// 파이어베이스 앱 초기화
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e){
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }

    private static byte[] getBase64DecodeBytes(String input) {
        return Base64.decodeBase64(input);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}