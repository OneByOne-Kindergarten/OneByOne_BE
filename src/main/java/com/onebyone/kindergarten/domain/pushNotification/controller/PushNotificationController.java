package com.onebyone.kindergarten.domain.pushNotification.controller;

import com.onebyone.kindergarten.domain.pushNotification.dto.PushNotificationRequestDTO;
import com.onebyone.kindergarten.domain.pushNotification.dto.PushNotificationResponseDTO;
import com.onebyone.kindergarten.domain.pushNotification.service.PushNotificationService;
import com.onebyone.kindergarten.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Tag(name = "푸시 알림", description = "푸시 알림 API")
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    @PostMapping("/save")
    @Operation(summary = "푸시 알림 저장(for 배치)", description = "알림을 전송합니다.")
    public ResponseDto<String> sendNotification(
            @RequestBody PushNotificationRequestDTO requestDTO
    ) {
        pushNotificationService.savePushNotification(requestDTO);
        return ResponseDto.success("알림이 성공적으로 전송되었습니다.");
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자의 모든 알림 조회", description = "사용자의 모든 알림을 조회합니다.")
    public ResponseDto<List<PushNotificationResponseDTO>> getUserNotifications(
            @PathVariable Long userId
    ) {
        List<PushNotificationResponseDTO> notifications = pushNotificationService.getUserNotifications(userId);
        return ResponseDto.success(notifications);
    }

    @GetMapping("/my")
    @Operation(summary = "현재 로그인한 사용자의 알림 조회", description = "현재 로그인한 사용자의 알림을 조회합니다.")
    public ResponseDto<List<PushNotificationResponseDTO>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<PushNotificationResponseDTO> notifications = pushNotificationService.getUserNotificationByUserDetails(Long.valueOf(userDetails.getUsername()));
        return ResponseDto.success(notifications);
    }

    @GetMapping("/my/unread")
    @Operation(summary = "현재 로그인한 사용자의 읽지 않은 알림 조회", description = "현재 로그인한 사용자의 읽지 않은 알림을 조회합니다.")
    public ResponseDto<List<PushNotificationResponseDTO>> getMyUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<PushNotificationResponseDTO> notifications = pushNotificationService.getUnreadNotificationsByUserDetails(Long.valueOf(userDetails.getUsername()));
        return ResponseDto.success(notifications);
    }

    @GetMapping("/my/unread/count")
    @Operation(summary = "현재 로그인한 사용자의 읽지 않은 알림 개수 조회", description = "현재 로그인한 사용자의 읽지 않은 알림 개수를 조회합니다.")
    public ResponseDto<Long> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        long count = pushNotificationService.countUnreadNotifications(Long.valueOf(userDetails.getUsername()));
        return ResponseDto.success(count);
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 표시", description = "알림을 읽음 처리합니다.")
    public ResponseDto<String> markAsRead(
            @PathVariable Long notificationId
    ) {
        pushNotificationService.markAsRead(notificationId);
        return ResponseDto.success("알림이 읽음 처리되었습니다.");
    }

    @PatchMapping("/my/read-all")
    @Operation(summary = "모든 알림 읽음 표시", description = "모든 알림을 읽음 처리합니다.")
    public ResponseDto<String> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        pushNotificationService.markAllAsRead(Long.valueOf(userDetails.getUsername()));
        return ResponseDto.success("모든 알림이 읽음 처리되었습니다.");
    }
}