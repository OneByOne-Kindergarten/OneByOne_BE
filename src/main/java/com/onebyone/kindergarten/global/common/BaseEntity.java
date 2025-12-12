package com.onebyone.kindergarten.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {
  @Column(name = "created_at", updatable = false)
  @CreatedDate
  protected LocalDateTime createdAt;

  @Column(name = "updated_at")
  protected LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  protected LocalDateTime deletedAt;
}
