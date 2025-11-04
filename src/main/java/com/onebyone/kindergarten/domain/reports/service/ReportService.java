package com.onebyone.kindergarten.domain.reports.service;

import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.communityComments.repository.CommunityCommentRepository;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityRepository;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReview;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.repository.KindergartenInternshipReviewRepository;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReview;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.repository.KindergartenWorkReviewRepository;
import com.onebyone.kindergarten.domain.reports.enums.ReportTargetType;
import com.onebyone.kindergarten.domain.reports.repository.ReportRepository;
import com.onebyone.kindergarten.domain.reports.dto.request.CreateReportRequestDTO;
import com.onebyone.kindergarten.domain.reports.dto.response.ReportResponseDTO;
import com.onebyone.kindergarten.domain.reports.entity.Report;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import com.onebyone.kindergarten.domain.reports.dto.request.ReportSearchDTO;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final CommunityRepository communityRepository;
    private final CommunityCommentRepository commentRepository;
    private final KindergartenWorkReviewRepository kindergartenWorkReviewRepository;
    private final KindergartenInternshipReviewRepository kindergartenInternshipReviewRepository;

    /// 신고 생성 (사용자)
    @Transactional
    public ReportResponseDTO createReport(CreateReportRequestDTO dto, String email) {

        // 사용자 조회
        User reporter = userService.getUserByEmail(email);

        // 신고 대상 존재 여부 확인을 위한 메서드 추가
        validateReportTarget(dto.getTargetType(), dto.getTargetId());

        // 신고 저장
        Report report = Report.builder()
                .reporter(reporter)
                .targetId(dto.getTargetId())
                .targetType(dto.getTargetType())
                .reason(dto.getReason())
                .status(ReportStatus.PENDING)
                .build();

        reportRepository.save(report);
        return ReportResponseDTO.fromEntity(report);
    }

    /// 내 신고 목록 조회 (사용자)
    public Page<ReportResponseDTO> getMyReports(String email, Pageable pageable) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 신고 목록 조회
        return reportRepository.findDtosByReporter(user, pageable);
    }

    /// 신고 처리 (관리자)
    @Transactional
    public ReportResponseDTO processReport(Long reportId, ReportStatus status) {

        // 신고 존재 여부 확인
        Report report = reportRepository.findByIdWithReporter(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_REPORT));

        // 신고 상태 업데이트
        report.updateStatus(status);

        // 신고 대상 상태 업데이트
        CompletableFuture.runAsync(() ->
                _updateTargetStatus(report.getTargetType(), report.getTargetId(), status)
        );

        return ReportResponseDTO.fromEntity(report);
    }

    /// 전체 신고 목록 조회 (관리자)
    public Page<ReportResponseDTO> getAllReports(ReportSearchDTO searchDTO, Pageable pageable) {

        // 신고 목록 조회
        return reportRepository.findAllDtosByCondition(
                searchDTO.getStatus(),
                searchDTO.getTargetType(),
                pageable
        );
    }

    /// 신고 상세 조회 (관리자)
    public ReportResponseDTO getReportDetail(Long reportId) {

        // 신고 상세 조회
        return reportRepository.findByIdWithReporter(reportId)
                .map(ReportResponseDTO::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_REPORT));
    }

    /// 신고 대상 상태 업데이트
    private void _updateTargetStatus(ReportTargetType targetType, Long targetId, ReportStatus status) {
        switch (targetType) {
            case POST -> {
                CommunityPost post = communityRepository.findById(targetId)
                        .orElseThrow(() -> new BusinessException(ErrorCodes.INVALID_REPORT_POST_TARGET));
                post.updateStatus(status);
            }
            case COMMENT -> {
                CommunityComment comment = commentRepository.findById(targetId)
                        .orElseThrow(() -> new BusinessException(ErrorCodes.INVALID_REPORT_COMMENT_TARGET));
                comment.updateStatus(status);
            }
            case WORK_REVIEW -> {
                KindergartenWorkReview kindergartenWorkReview = kindergartenWorkReviewRepository.findById(targetId)
                        .orElseThrow(() -> new BusinessException(ErrorCodes.INVALID_REPORT_WORK_REVIEW_TARGET));
                kindergartenWorkReview.updateStatus(status);
            }
            case INTERNSHIP_REVIEW -> {
                KindergartenInternshipReview kindergartenInternshipReview = kindergartenInternshipReviewRepository.findById(targetId)
                        .orElseThrow(() -> new BusinessException(ErrorCodes.INVALID_REPORT_INTERNSHIP_REVIEW_TARGET));
                kindergartenInternshipReview.updateStatus(status);
            }
            default -> throw new BusinessException(ErrorCodes.INVALID_REPORT_TARGET_TYPE);
        }
    }

    /// 신고 대상 검증
    private void validateReportTarget(ReportTargetType targetType, Long targetId) {
        switch (targetType) {
            case POST -> communityRepository.findById(targetId)
                    .orElseThrow(() -> new BusinessException(ErrorCodes.INVALID_REPORT_POST_TARGET));
            case COMMENT -> commentRepository.findById(targetId)
                    .orElseThrow(() -> new BusinessException(ErrorCodes.INVALID_REPORT_COMMENT_TARGET));
            case WORK_REVIEW -> kindergartenWorkReviewRepository.findById(targetId)
                    .orElseThrow(() -> new BusinessException(ErrorCodes.INVALID_REPORT_WORK_REVIEW_TARGET));
            case INTERNSHIP_REVIEW -> kindergartenInternshipReviewRepository.findById(targetId)
                    .orElseThrow(() -> new BusinessException(ErrorCodes.INVALID_REPORT_INTERNSHIP_REVIEW_TARGET));
        }
    }
}
