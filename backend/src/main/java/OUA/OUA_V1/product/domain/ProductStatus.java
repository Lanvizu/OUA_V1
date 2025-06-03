package OUA.OUA_V1.product.domain;

public enum ProductStatus {
    ACTIVE,     // 판매 중
    SOLD,       // 낙찰 완료
    UNSOLD,     // 유찰
    CANCELED,   // 판매자 취소
    DELETED     // 관리자 삭제
}
