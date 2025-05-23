package com.onebyone.kindergarten.domain.reports.service;

import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.communityComments.repository.CommunityCommentRepository;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityRepository;
import com.onebyone.kindergarten.domain.reports.enums.ReportTargetType;
import com.onebyone.kindergarten.domain.reports.repository.ReportRepository;
import com.onebyone.kindergarten.domain.reports.dto.request.CreateReportRequestDTO;
import com.onebyone.kindergarten.domain.reports.dto.response.ReportResponseDTO;
import com.onebyone.kindergarten.domain.reports.entity.Report;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import com.onebyone.kindergarten.domain.reports.dto.request.ReportSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.onebyone.kindergarten.domain.reports.exception.ReportNotFoundException;
import com.onebyone.kindergarten.domain.reports.exception.InvalidReportTargetException;

import java.util.concurrent.CompletableFuture;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final CommunityRepository communityRepository;
    private final CommunityCommentRepository commentRepository;

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
                .orElseThrow(() -> new ReportNotFoundException("존재하지 않는 신고입니다."));

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
                searchDTO != null ? searchDTO.getStatus() : null,
                searchDTO != null ? searchDTO.getTargetType() : null,
                pageable
        );
    }

    /// 신고 상세 조회 (관리자)
    public ReportResponseDTO getReportDetail(Long reportId) {

        // 신고 상세 조회
        return reportRepository.findByIdWithReporter(reportId)
                .map(ReportResponseDTO::fromEntity)
                .orElseThrow(() -> new ReportNotFoundException("존재하지 않는 신고입니다."));
    }

    /// 신고 대상 상태 업데이트
    private void _updateTargetStatus(ReportTargetType targetType, Long targetId, ReportStatus status) {
        switch (targetType) {
            case POST -> {
                CommunityPost post = communityRepository.findById(targetId)
                        .orElseThrow(() -> new InvalidReportTargetException("존재하지 않는 게시글입니다."));
                post.updateStatus(status);
            }
            case COMMENT -> {
                CommunityComment comment = commentRepository.findById(targetId)
                        .orElseThrow(() -> new InvalidReportTargetException("존재하지 않는 댓글입니다."));
                comment.updateStatus(status);
            }
            case REVIEW -> {
                throw new InvalidReportTargetException("리뷰 처리는 아직 구현되지 않았습니다.");
            }
            default -> throw new InvalidReportTargetException("지원하지 않는 신고 대상 타입입니다.");
        }
    }

    /// 신고 대상 검증
    private void validateReportTarget(ReportTargetType targetType, Long targetId) {
        switch (targetType) {
            case POST -> communityRepository.findById(targetId)
                    .orElseThrow(() -> new InvalidReportTargetException("존재하지 않는 게시글입니다."));
            case COMMENT -> commentRepository.findById(targetId)
                    .orElseThrow(() -> new InvalidReportTargetException("존재하지 않는 댓글입니다."));
            case REVIEW -> throw new InvalidReportTargetException("리뷰 처리는 아직 구현되지 않았습니다.");
        }
    }

    /// 대기 중인 신고 수 조회 (관리자)
    public long countPendingReports() {
        return reportRepository.countByStatusPending();
    }

    public Long countByStatus(String status) {
        return reportRepository.countByStatus(ReportStatus.valueOf(status));
    }

    public List<Report> findAllByOrderByCreatedAtDesc() {
        return reportRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Report> findAllByStatusOrderByCreatedAtDesc(String status) {
        return reportRepository.findAllByStatusOrderByCreatedAtDesc(ReportStatus.valueOf(status));
    }

    public Report findById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        Report report = findById(id);
        report.updateStatus(ReportStatus.valueOf(status));
    }
}
