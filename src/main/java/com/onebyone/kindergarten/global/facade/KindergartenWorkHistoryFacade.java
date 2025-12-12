package com.onebyone.kindergarten.global.facade;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto.KindergartenWorkHistoryRequest;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto.KindergartenWorkHistoryResponse;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.entity.KindergartenWorkHistory;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.service.KindergartenWorkHistoryService;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.service.KindergartenService;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KindergartenWorkHistoryFacade {
  private final UserService userService;
  private final KindergartenService kindergartenService;
  private final KindergartenWorkHistoryService kindergartenWorkHistoryService;

  @Transactional
  public KindergartenWorkHistoryResponse addCertification(
      Long userId, KindergartenWorkHistoryRequest request) {
    // 사용자 조회
    User user = userService.getUserById(userId);

    // 유치원 이름으로 유치원 조회
    Kindergarten kindergarten =
        kindergartenService.getKindergartenByName(request.getKindergartenName());

    userService.addCareer(user, request.getStartDate(), request.getEndDate());
    return kindergartenWorkHistoryService.addCertification(user, kindergarten, request);
  }

  @Transactional
  public void deleteCertification(Long userId, Long certificationId) {
    User user = userService.getUserById(userId);
    KindergartenWorkHistory workHistory =
        kindergartenWorkHistoryService.getKindergartenWorkHistory(certificationId);

    // 유치원 근무 이력 소유자 확인
    if (!workHistory.getUser().equals(user)) {
      throw new BusinessException(ErrorCodes.UNAUTHORIZED_DELETE);
    }

    userService.removeCareer(user, workHistory.getStartDate(), workHistory.getEndDate());
    kindergartenWorkHistoryService.deleteCertification(workHistory);
  }
}
