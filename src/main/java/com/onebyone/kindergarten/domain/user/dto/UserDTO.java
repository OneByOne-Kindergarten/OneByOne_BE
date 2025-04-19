package com.onebyone.kindergarten.domain.user.dto;

import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenDTO;
import com.onebyone.kindergarten.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String role;
    private String career;
    private KindergartenDTO kindergarten;

    public static UserDTO from(User user) {
        return new UserDTO(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getRole().name(),
                user.getCareer(),
                user.getKindergarten() != null ? KindergartenDTO.from(user.getKindergarten()) : null
        );
    }
}