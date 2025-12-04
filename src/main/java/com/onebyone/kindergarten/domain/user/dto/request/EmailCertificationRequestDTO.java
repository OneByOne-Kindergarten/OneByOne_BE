package com.onebyone.kindergarten.domain.user.dto.request;

import com.onebyone.kindergarten.domain.user.enums.EmailCertificationType;
import lombok.Data;

@Data
public class EmailCertificationRequestDTO {
    String email;
    EmailCertificationType certificationType;
}
