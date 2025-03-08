package com.onebyone.kindergarten.domain.sample.service;

import com.onebyone.kindergarten.domain.sample.repository.SampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SampleService {
    private final SampleRepository sampleRepository;

    public void getSample() {
        sampleRepository.findById(1L);
    }
}
