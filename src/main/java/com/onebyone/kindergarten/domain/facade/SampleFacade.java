package com.onebyone.kindergarten.domain.facade;

import org.springframework.stereotype.Component;

@Component
public class SampleFacade {
/*
    도메인간 서비스끼리 순환참조 발생을 막기 위한 facade 패키지
    예시) boardService에서 boardReviewService를 참조하여 트랜잭션을 유발 방지
    참조해야 할 상황이 발생한다면 controller에서 facade 패키지를 의존성을 추가하여
    facade 클래스에서 각 서비스를 참조
 */
}
