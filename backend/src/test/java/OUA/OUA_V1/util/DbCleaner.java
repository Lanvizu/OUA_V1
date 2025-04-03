package OUA.OUA_V1.util;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("test")
public class DbCleaner {

    // H2 데이터베이스 참조 무결성 제어용 SQL
    private static final String INTEGRITY_FALSE = "SET REFERENTIAL_INTEGRITY FALSE";
    private static final String INTEGRITY_TRUE = "SET REFERENTIAL_INTEGRITY TRUE";


    // CamelCase -> snake_case 변환 정규식
    private static final String CAMEL_CASE = "([a-z])([A-Z])";
    private static final String SNAKE_CASE = "$1_$2";

    // 테이블 초기화용 SQL 템플릿
    private static final String TRUNCATE_TABLE = "TRUNCATE TABLE %s";
    private static final String RESET_ID_SEQUENCE = "ALTER TABLE %s ALTER COLUMN %s_id RESTART WITH 1";  // H2 전용 구문

    @PersistenceContext
    private EntityManager em;  // JPA EntityManager 주입

    private List<String> tableNames;  // 관리 대상 테이블 이름 목록

    @PostConstruct
    public void findTableNames() {
        // JPA 메타모델에서 모든 엔티티 조회
        tableNames = em.getMetamodel().getEntities()
                .stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)  // @Entity가 붙은 클래스만 필터링
                .map(this::convertCamelToSnake)  // 테이블 이름 스네이크 케이스 변환
                .toList();
    }

    // 엔티티 클래스명 -> DB 테이블명 변환 (예: MemberRole -> member_role)
    private String convertCamelToSnake(final EntityType<?> e) {
        return e.getName()
                .replaceAll(CAMEL_CASE, SNAKE_CASE)
                .toLowerCase();
    }

    @Transactional
    public void truncateEveryTable() {
        em.clear();  // 영속성 컨텍스트 캐시 초기화

        disableIntegrity();  // 외래키 제약 조건 비활성화

        for (String tableName : tableNames) {
            truncateTable(tableName);  // 테이블 데이터 삭제
            resetIdColumn(tableName);  // ID 시퀀스 초기화
        }

        enableIntegrity();  // 외래키 제약 조건 재활성화
    }

    // 참조 무결성 해제 (TRUNCATE 실행 허용)
    private void disableIntegrity() {
        em.createNativeQuery(INTEGRITY_FALSE)
                .executeUpdate();
    }

    // 특정 테이블 데이터 전체 삭제
    private void truncateTable(final String tableName) {
        em.createNativeQuery(String.format(TRUNCATE_TABLE, tableName))
                .executeUpdate();
    }

    // ID 시퀀스 초기화 (H2 데이터베이스 전용 구현)
    private void resetIdColumn(final String tableName) {
        em.createNativeQuery(String.format(RESET_ID_SEQUENCE, tableName, tableName))
                .executeUpdate();
    }

    // 참조 무결성 복구
    private void enableIntegrity() {
        em.createNativeQuery(INTEGRITY_TRUE)
                .executeUpdate();
    }
}
