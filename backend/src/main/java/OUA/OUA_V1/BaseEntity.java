package OUA.OUA_V1;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // 공통 필드를 공유하기 위한 부모 엔티티임을 지정
@EntityListeners(AuditingEntityListener.class) // 생성/수정 시간 자동 기록 기능 활성화
@Getter
public class BaseEntity {

    @CreatedDate // 엔티티 생성 시 시각 자동 기록
    @Column(updatable = false) // 업데이트 시 변경되지 않음
    private LocalDateTime createdDate;

    @LastModifiedDate // 엔티티 수정 시 시각 자동 기록
    private LocalDateTime updatedDate;
}
