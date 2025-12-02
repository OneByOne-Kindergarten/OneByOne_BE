package com.onebyone.kindergarten.domain.address.controller;

import com.onebyone.kindergarten.global.facade.BatchFacade;
import com.onebyone.kindergarten.global.facade.KindergartenFacade;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class AddressController {
    private final BatchFacade batchFacade;

    @PostMapping("/kindergarten-address-batch")
    @Operation(summary = "주소 등록 배치", description = "kindergarten 테이블의 subRegion 컬럼에 데이터를 매핑시킵니다")
    public void kindergartenAddressBatch(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        batchFacade.kindergartenAddressBatch(userDetails.getUsername());
    }
}
