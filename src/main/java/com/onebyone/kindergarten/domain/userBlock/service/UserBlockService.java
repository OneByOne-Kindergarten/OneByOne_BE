package com.onebyone.kindergarten.domain.userBlock.service;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.domain.userBlock.dto.response.BlockedUserResponseDto;
import java.util.List;
import com.onebyone.kindergarten.domain.userBlock.entity.UserBlock;
import com.onebyone.kindergarten.domain.userBlock.repository.UserBlockRepository;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
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
            throw new BusinessException(ErrorCodes.SELF_BLOCK_NOT_ALLOWED);
        }
        if (userBlockRepository.existsByUserIdAndBlockedUserId(userId, targetUserId)) {
            throw new BusinessException(ErrorCodes.ALREADY_BLOCK_USER);
        }
    }

    public List<BlockedUserResponseDto> getBlockedUsers(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);
        return userBlockRepository.findBlockedUsersByUserId(user.getId());
    }
}