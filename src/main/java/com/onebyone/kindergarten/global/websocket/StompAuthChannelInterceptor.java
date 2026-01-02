package com.onebyone.kindergarten.global.websocket;

import com.onebyone.kindergarten.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ExecutorChannelInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

//          if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                throw new BusinessException(ErrorCodes.INVALID_TOKEN_ILLEGAL);
//          }

            String token = authHeader.substring(7);

            Map<String, Object> result = jwtProvider.getAuthentication(token);
//            if (!(Boolean) result.get("isValid")) {
//                throw new BusinessException((ErrorCodes) result.get("errorCode"));
//            }

            Authentication authentication = (Authentication) result.get("authentication");

            accessor.setUser(authentication);
        }

        return message;
    }
}
