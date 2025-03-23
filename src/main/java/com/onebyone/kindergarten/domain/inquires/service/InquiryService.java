package com.onebyone.kindergarten.domain.inquires.service;

import com.onebyone.kindergarten.domain.inquires.dto.request.AnswerInquiryRequestDTO;
import com.onebyone.kindergarten.domain.inquires.dto.request.CreateInquiryRequestDTO;
import com.onebyone.kindergarten.domain.inquires.dto.response.InquiryResponseDTO;
import com.onebyone.kindergarten.domain.inquires.entity.Inquiry;
import com.onebyone.kindergarten.domain.inquires.enums.InquiryStatus;
import com.onebyone.kindergarten.domain.inquires.exception.InquiryNotFoundException;
import com.onebyone.kindergarten.domain.inquires.repository.InquiryRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.exception.UnauthorizedException;
import com.onebyone.kindergarten.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserService userService;

    /**
     * 문의 생성
     */
    @Transactional
    public InquiryResponseDTO createInquiry(CreateInquiryRequestDTO dto, String email) {
        User user = userService.getUserByEmail(email);
        
        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
        
        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        return InquiryResponseDTO.fromEntity(savedInquiry);
    }
    
    /**
     * 문의 조회 (단일)
     */
    public InquiryResponseDTO getInquiry(Long id, String email) {
        User user = userService.getUserByEmail(email);
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryNotFoundException("문의를 찾을 수 없습니다."));
        
        // 본인이 아니거나 관리자가 아니면 조회 불가
        if (!inquiry.getUser().getId().equals(user.getId()) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new UnauthorizedException("자신의 문의만 조회할 수 있습니다.");
        }

        return InquiryResponseDTO.fromEntity(inquiry);
    }
    
    /**
     * 사용자별 문의 목록 조회
     */
    public Page<InquiryResponseDTO> getUserInquiries(String email, Pageable pageable) {
        User user = userService.getUserByEmail(email);
        return inquiryRepository.findByUser(user, pageable)
                .map(InquiryResponseDTO::fromEntity);
    }
    
    /**
     * 모든 문의 목록 조회 (관리자 전용)
     */
    public Page<InquiryResponseDTO> getAllInquiries(String email, Pageable pageable) {
        User user = userService.getUserByEmail(email);
        
        // 관리자 권한 체크
        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new UnauthorizedException("관리자만 모든 문의를 조회할 수 있습니다.");
        }
        
        return inquiryRepository.findAllOrderByStatusAndCreatedAt(pageable)
                .map(InquiryResponseDTO::fromEntity);
    }
    
    /**
     * 상태별 문의 목록 조회 (관리자 전용)
     */
    public Page<InquiryResponseDTO> getInquiriesByStatus(InquiryStatus status, String email, Pageable pageable) {
        User user = userService.getUserByEmail(email);
        
        // 관리자 권한 체크
        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new UnauthorizedException("관리자만 상태별 문의를 조회할 수 있습니다.");
        }
        
        return inquiryRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                .map(InquiryResponseDTO::fromEntity);
    }
    
    /**
     * 문의 답변 (관리자 전용)
     */
    @Transactional
    public InquiryResponseDTO answerInquiry(Long id, AnswerInquiryRequestDTO dto, String email) {
        User user = userService.getUserByEmail(email);
        
        // 관리자 권한 체크
        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new UnauthorizedException("관리자만 문의에 답변할 수 있습니다.");
        }
        
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryNotFoundException("문의를 찾을 수 없습니다."));
        
        inquiry.answerInquiry(dto.getAnswer());
        
        return InquiryResponseDTO.fromEntity(inquiry);
    }
    
    /**
     * 문의 마감 (관리자 전용)
     */
    @Transactional
    public InquiryResponseDTO closeInquiry(Long id, String email) {
        User user = userService.getUserByEmail(email);
        
        // 관리자 권한 체크
        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new UnauthorizedException("관리자만 문의를 마감할 수 있습니다.");
        }
        
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryNotFoundException("문의를 찾을 수 없습니다."));
        
        inquiry.closeInquiry();
        
        return InquiryResponseDTO.fromEntity(inquiry);
    }
}
