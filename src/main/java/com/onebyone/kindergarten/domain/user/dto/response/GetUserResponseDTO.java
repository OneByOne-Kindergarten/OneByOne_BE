package com.onebyone.kindergarten.domain.user.dto.response;

import com.onebyone.kindergarten.domain.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponseDTO {
  private UserDTO user;
}
