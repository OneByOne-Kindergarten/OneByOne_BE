package com.onebyone.kindergarten.admin.facade;

import com.onebyone.kindergarten.admin.dto.AdminDashboardDTO;
import com.onebyone.kindergarten.admin.dto.AdminLoginRequestDTO;
import com.onebyone.kindergarten.admin.dto.response.AdminInquiryResponseDTO;
import com.onebyone.kindergarten.admin.dto.response.AdminReportResponseDTO;
import com.onebyone.kindergarten.domain.inquires.dto.request.AnswerInquiryRequestDTO;
import com.onebyone.kindergarten.domain.inquires.dto.response.InquiryResponseDTO;
import com.onebyone.kindergarten.domain.inquires.service.InquiryService;
import com.onebyone.kindergarten.domain.reports.dto.response.ReportResponseDTO;
import com.onebyone.kindergarten.domain.reports.service.ReportService;
import com.onebyone.kindergarten.domain.user.dto.response.SignInResponseDTO;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.common.PageResponseDTO;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import com.onebyone.kindergarten.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminFacade {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final InquiryService inquiryService;
    private final ReportService reportService;


    /**
     * 관리자 로그인
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO
     */
    @Transactional
    public SignInResponseDTO login(AdminLoginRequestDTO request) {
        String email = userService.signInAdmin(request.getUsername(), request.getPassword());
        
        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        return SignInResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 관리자 대시보드 정보 조회
     *
     * @return 대시보드 정보 DTO
     */
    @Transactional(readOnly = true)
    public AdminDashboardDTO getDashboardInfo() {
        long pendingReports = reportService.countPendingReports();
        long pendingInquiries = inquiryService.countPendingInquiries();

        return AdminDashboardDTO.builder()
                .pendingReports(pendingReports)
                .pendingInquiries(pendingInquiries)
                .build();
    }

    /**
     * 관리자 신고 목록 조회
     *
     * @return 대시보드 정보 DTO
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<ReportResponseDTO> getAllReports(Pageable pageable) {
        return new PageResponseDTO<>(reportService.getAllReports(null, pageable));
    }

    /**
     * 관리자 신고 상세 조회
     *
     * @param reportId 신고 ID
     * @return 신고 상세 정보 DTO
     */
    @Transactional(readOnly = true)
    public ReportResponseDTO getReportDetail(Long reportId) {
        return reportService.getReportDetail(reportId);
    }

    /**
     * 관리자 신고 처리
     *
     * @param reportId 신고 ID
     * @param status   신고 상태
     * @return 신고 처리 결과 DTO
     */
    @Transactional
    public ReportResponseDTO processReport(Long reportId, ReportStatus status) {
        return reportService.processReport(reportId, status);
    }

    /**
     * 관리자 문의 목록 조회
     *
     * @param adminEmail 관리자 이메일
     * @param pageable   페이지 정보
     * @return 문의 목록 DTO
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<InquiryResponseDTO> getAllInquiries(String adminEmail, Pageable pageable) {
        return new PageResponseDTO<>(inquiryService.getAllInquiries(adminEmail, pageable));
    }

    /**
     * 관리자 문의 상세 조회
     *
     * @param inquiryId  문의 ID
     * @param adminEmail 관리자 이메일
     * @return 문의 상세 정보 DTO
     */
    @Transactional(readOnly = true)
    public InquiryResponseDTO getInquiryDetail(Long inquiryId, String adminEmail) {
        return inquiryService.getInquiry(inquiryId, adminEmail);
    }

    /**
     * 관리자 문의 답변
     *
     * @param inquiryId 문의 ID
     * @param dto       답변 요청 DTO
     * @param adminEmail 관리자 이메일
     * @return 문의 답변 결과 DTO
     */
    @Transactional
    public InquiryResponseDTO answerInquiry(Long inquiryId, AnswerInquiryRequestDTO dto, String adminEmail) {
        return inquiryService.answerInquiry(inquiryId, dto, adminEmail);
    }

    /**
     * 관리자 문의 종료
     *
     * @param inquiryId 문의 ID
     * @param adminEmail 관리자 이메일
     * @return 문의 종료 결과 DTO
     */
    @Transactional
    public InquiryResponseDTO closeInquiry(Long inquiryId, String adminEmail) {
        return inquiryService.closeInquiry(inquiryId, adminEmail);
    }

    @Transactional(readOnly = true)
    public Long getPendingReportsCount() {
        return reportService.countPendingReports();
    }

    @Transactional(readOnly = true)
    public Long getPendingInquiriesCount() {
        return inquiryService.countPendingInquiries();
    }

    @Transactional(readOnly = true)
    public List<AdminReportResponseDTO> getReports(String status) {
        return "ALL".equals(status) 
            ? reportService.findAllByOrderByCreatedAtDesc().stream().map(AdminReportResponseDTO::from).toList()
            : reportService.findAllByStatusOrderByCreatedAtDesc(status).stream().map(AdminReportResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AdminInquiryResponseDTO> getInquiries(String status) {
        return "ALL".equals(status)
            ? inquiryService.findAllByOrderByCreatedAtDesc().stream().map(AdminInquiryResponseDTO::from).toList()
            : inquiryService.findAllByStatusOrderByCreatedAtDesc(status).stream().map(AdminInquiryResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public AdminReportResponseDTO getReport(Long id) {
        return AdminReportResponseDTO.from(reportService.findById(id));
    }

    @Transactional(readOnly = true)
    public AdminInquiryResponseDTO getInquiry(Long id) {
        return AdminInquiryResponseDTO.from(inquiryService.findById(id));
    }

    @Transactional
    public void updateReportStatus(Long id, String status) {
        reportService.updateStatus(id, status);
    }

    @Transactional
    public void submitAnswer(Long id, AnswerInquiryRequestDTO requestDTO) {
        inquiryService.submitAnswer(id, requestDTO);
    }

    @Transactional
    public void closeInquiry(Long id) {
        inquiryService.close(id);
    }
} 