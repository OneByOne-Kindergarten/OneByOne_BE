package com.onebyone.kindergarten.domain.userBlock.service;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.domain.userBlock.exception.AlreadyBlockException;
import com.onebyone.kindergarten.domain.userBlock.exception.SelfBlockException;
import com.onebyone.kindergarten.domain.userBlock.dto.response.BlockedUserResponseDto;
import java.util.List;
import com.onebyone.kindergarten.domain.userBlock.entity.UserBlock;
import com.onebyone.kindergarten.domain.userBlock.repository.UserBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserBlockService {
    private final UserBlockRepository userBlockRepository;
    private final UserService userService;

    @Transactional
    public void blockUser(UserDetails userDetails, String targetUserEmail) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);
        
        User targetUser = userService.getUserByEmail(targetUserEmail);
        validateBlockRequest(user.getId(), targetUser.getId());

        UserBlock userBlock = UserBlock.builder()
                .user(user)
                .blockedUser(targetUser)
                .build();

        userBlockRepository.save(userBlock);
    }

    @Transactional
    public void unblockUser(UserDetails userDetails, String targetUserEmail) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);
        User targetUser = userService.getUserByEmail(targetUserEmail);
        userBlockRepository.deleteByUserIdAndBlockedUserId(user.getId(), targetUser.getId());
    }

    private void validateBlockRequest(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new SelfBlockException("자기 자신을 차단할 수 없습니다.");
        }
        if (userBlockRepository.existsByUserIdAndBlockedUserId(userId, targetUserId)) {
            throw new AlreadyBlockException("이미 차단한 사용자입니다.");
        }
    }

    public List<BlockedUserResponseDto> getBlockedUsers(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);
        return userBlockRepository.findBlockedUsersByUserId(user.getId());
    }
}