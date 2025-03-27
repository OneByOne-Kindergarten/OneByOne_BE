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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.onebyone.kindergarten.domain.reports.exception.ReportNotFoundException;
import com.onebyone.kindergarten.domain.reports.exception.InvalidReportTargetException; 

@Service
@Transactional(readOnly = true)
public class ReportService {
   private final ReportRepository reportRepository;
   private final UserService userService;
   private final CommunityRepository communityRepository;
   private final CommunityCommentRepository commentRepository;

   public ReportService(ReportRepository reportRepository, 
                       UserService userService,
                       CommunityRepository communityRepository,
                       CommunityCommentRepository commentRepository) {
      this.reportRepository = reportRepository;
      this.userService = userService;
      this.communityRepository = communityRepository;
      this.commentRepository = commentRepository;
   }

   /// 신고 생성
   @Transactional
   public ReportResponseDTO createReport(CreateReportRequestDTO dto, String email) {
      User reporter = userService.getUserByEmail(email);
      
      Report report = Report.builder()
              .reporter(reporter)
              .targetId(dto.getTargetId())
              .targetType(dto.getTargetType())
              .reason(dto.getReason())
              .status(ReportStatus.PENDING)
              .build();

      Report savedReport = reportRepository.save(report);
      return ReportResponseDTO.fromEntity(savedReport);
   }

   /// 내 신고 목록 조회
   public Page<ReportResponseDTO> getMyReports(String email, Pageable pageable) {
      User user = userService.getUserByEmail(email);
      return reportRepository.findByReporter(user, pageable)
              .map(ReportResponseDTO::fromEntity);
   }

   /// 신고 처리
   @Transactional
   public ReportResponseDTO processReport(Long reportId, ReportStatus status) {
      Report report = reportRepository.findById(reportId)
              .orElseThrow(() -> new ReportNotFoundException("존재하지 않는 신고입니다."));

      /// 신고 상태 업데이트
      report.updateStatus(status);
      _updateTargetStatus(report.getTargetType(), report.getTargetId(), status);

      return ReportResponseDTO.fromEntity(report);
   }

   /// 전체 신고 목록 조회
   public Page<ReportResponseDTO> getAllReports(ReportSearchDTO searchDTO, Pageable pageable) {
      return reportRepository.findAllByCondition(
              searchDTO.getStatus(),
              searchDTO.getTargetType(),
              pageable
      ).map(ReportResponseDTO::fromEntity);
   }

   /// 신고 상세 조회
   public ReportResponseDTO getReportDetail(Long reportId) {
      Report report = reportRepository.findById(reportId)
              .orElseThrow(() -> new ReportNotFoundException("존재하지 않는 신고입니다."));
      return ReportResponseDTO.fromEntity(report);
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
}
