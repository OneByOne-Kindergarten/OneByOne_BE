package com.onebyone.kindergarten.admin.controller;

import com.onebyone.kindergarten.admin.facade.AdminFacade;
import com.onebyone.kindergarten.domain.inquires.dto.request.AnswerInquiryRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "관리자", description = "관리자 페이지 API")
public class AdminController {

    private final AdminFacade adminFacade;

    @GetMapping("/login")
    @Operation(summary = "관리자 로그인 페이지", description = "관리자 로그인 페이지를 반환합니다.")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    @Operation(summary = "관리자 대시보드", description = "관리자 대시보드를 표시합니다.")
    public String dashboard(
            @RequestParam(required = false) String activeTab,
            @RequestParam(defaultValue = "ALL") String status,
            Model model) {
        
        // 대시보드 데이터
        model.addAttribute("pendingReports", adminFacade.getPendingReportsCount());
        model.addAttribute("pendingInquiries", adminFacade.getPendingInquiriesCount());
        
        // 활성 탭에 따른 데이터 로드
        if ("reports".equals(activeTab)) {
            model.addAttribute("reports", adminFacade.getReports(status));
            model.addAttribute("reportStatus", status);
        } else if ("inquiries".equals(activeTab)) {
            model.addAttribute("inquiries", adminFacade.getInquiries(status));
            model.addAttribute("inquiryStatus", status);
        } else {
            // 기본 데이터
            model.addAttribute("reports", adminFacade.getReports("ALL"));
            model.addAttribute("inquiries", adminFacade.getInquiries("ALL"));
        }
        
        model.addAttribute("activeTab", activeTab);
        return "admin/dashboard";
    }

    @GetMapping("/reports/{id}")
    @Operation(summary = "신고 상세 조회", description = "신고 상세 정보를 조회합니다.")
    public String getReportDetail(
            @PathVariable Long id,
            Model model) {
        model.addAttribute("report", adminFacade.getReport(id));
        return "admin/report-detail";
    }

    @GetMapping("/inquiries/{id}")
    @Operation(summary = "문의 상세 조회", description = "문의 상세 정보를 조회합니다.")
    public String getInquiryDetail(
            @PathVariable Long id,
            Model model) {
        model.addAttribute("inquiry", adminFacade.getInquiry(id));
        return "admin/inquiry-detail";
    }

    @PostMapping("/reports/{id}/status")
    @Operation(summary = "신고 처리", description = "신고를 처리합니다.")
    public String updateReportStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String currentStatus,
            RedirectAttributes redirectAttributes) {
        try {
            adminFacade.updateReportStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "신고가 처리되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "신고 처리 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/dashboard?activeTab=reports&status=" + (currentStatus != null ? currentStatus : "ALL");
    }

    @PostMapping("/inquiries/{id}/answer")
    @Operation(summary = "문의 답변", description = "문의에 답변을 등록합니다.")
    public String submitAnswer(
            @PathVariable Long id,
            @RequestParam String answer,
            @RequestParam(required = false) String currentStatus,
            RedirectAttributes redirectAttributes) {
        try {
            AnswerInquiryRequestDTO requestDTO = new AnswerInquiryRequestDTO(answer);
            adminFacade.submitAnswer(id, requestDTO);
            redirectAttributes.addFlashAttribute("message", "답변이 등록되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "답변 등록 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/dashboard?activeTab=inquiries&status=" + (currentStatus != null ? currentStatus : "ALL");
    }

    @PostMapping("/inquiries/{id}/close")
    @Operation(summary = "문의 마감", description = "문의를 마감 처리합니다.")
    public String closeInquiry(
            @PathVariable Long id,
            @RequestParam(required = false) String currentStatus,
            RedirectAttributes redirectAttributes) {
        try {
            adminFacade.closeInquiry(id);
            redirectAttributes.addFlashAttribute("message", "문의가 마감되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "문의 마감 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/dashboard?activeTab=inquiries&status=" + (currentStatus != null ? currentStatus : "ALL");
    }
} 