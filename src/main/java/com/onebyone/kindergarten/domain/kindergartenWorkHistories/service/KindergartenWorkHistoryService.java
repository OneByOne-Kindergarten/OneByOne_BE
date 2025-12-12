package com.onebyone.kindergarten.domain.kindergartenWorkHistories.service;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto.KindergartenWorkHistoryRequest;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto.KindergartenWorkHistoryResponse;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.entity.KindergartenWorkHistory;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.repository.KindergartenWorkHistoryRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.repository.KindergartenRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KindergartenWorkHistoryService {

  private final UserService userService;
  private final KindergartenWorkHistoryRepository workHistoryRepository;
  private final KindergartenRepository kindergartenRepository;

  /// 유치원 근무 이력 추가
  public KindergartenWorkHistoryResponse addCertification(
      User user, Kindergarten kindergarten, KindergartenWorkHistoryRequest request) {
    // 유치원 근무 이력 저장
    KindergartenWorkHistory workHistory = request.toEntity(user, kindergarten);
    workHistoryRepository.save(workHistory);

    return KindergartenWorkHistoryResponse.from(workHistory);
  }

  /// 유치원 근무 이력 조회
  public List<KindergartenWorkHistoryResponse> getCertification(Long userId) {

    // 사용자 조회
    User user = userService.getUserById(userId);

    // 유치원 근무 이력 조회
    return workHistoryRepository.findDtosByUser(user);
  }

  public KindergartenWorkHistory getKindergartenWorkHistory(Long certificationId) {
    return workHistoryRepository
        .findById(certificationId)
        .orElseThrow(() -> new BusinessException(ErrorCodes.WORK_HISTORY_NOT_FOUND));
  }

  /// 유치원 근무 이력 삭제
  public void deleteCertification(KindergartenWorkHistory workHistory) {
    workHistoryRepository.delete(workHistory);
  }
}
