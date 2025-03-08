package com.onebyone.kindergarten.domain.sample.controller;

import com.onebyone.kindergarten.domain.sample.service.SampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample/v2")
@RequiredArgsConstructor
public class SampleController{
    private final SampleService sampleService;

    @GetMapping("/sample")
    public void sample() {
        sampleService.getSample();
    }
}
