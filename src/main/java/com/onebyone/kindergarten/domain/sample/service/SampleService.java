package com.onebyone.kindergarten.domain.sample.service;

import com.onebyone.kindergarten.domain.sample.exception.SampleException;
import com.onebyone.kindergarten.domain.sample.repository.SampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SampleService {
    private final SampleRepository sampleRepository;

    public void getSample() {
        throw new SampleException("sample");
    }
}
