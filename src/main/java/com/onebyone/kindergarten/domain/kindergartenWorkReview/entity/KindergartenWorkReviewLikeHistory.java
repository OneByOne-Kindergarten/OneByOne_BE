package com.onebyone.kindergarten.domain.kindergartenWorkReview.entity;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "kindergarten_work_review_like_history")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KindergartenWorkReviewLikeHistory extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 리뷰 코드

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user", nullable = false)
  private User user; // 작성자

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "kindergarten", nullable = false)
  private KindergartenWorkReview workReview;
}
