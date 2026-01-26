package com.onebyone.kindergarten.global.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // websocket이 연결할 url 엔드포인트
                .setAllowedOriginPatterns("*") // TODO: 특정 도메인에서만 허용되도록 변경
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // 해당 path를 구독한 클라이언트에게 발신
        registry.setApplicationDestinationPrefixes("/app"); // app path 요청이라면 @Controller의 @messageMapping 메서드로 라우팅 진행
    }
}
