package com.onebyone.kindergarten.domain.sample.controller;

import com.onebyone.kindergarten.domain.sample.service.SampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sample API", description = "샘플 API")
@RestController
@RequestMapping("/sample/v2")
@RequiredArgsConstructor
public class SampleController{
    private final SampleService sampleService;

    @Operation(summary = "예시-01", description = "예시입니다.")
    @GetMapping("/sample")
    public void sample() {
        sampleService.getSample();
    }
}
