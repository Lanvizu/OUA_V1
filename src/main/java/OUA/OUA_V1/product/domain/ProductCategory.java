package OUA.OUA_V1.product.domain;

import lombok.Getter;

@Getter
public enum ProductCategory {
    DIGITAL_DEVICE(301, "디지털기기"),
    HOME_APPLIANCE(302, "생활가전"),
    FURNITURE_INTERIOR(303, "가구/인테리어"),
    LIVING_KITCHEN(304, "생활/주방"),
    CHILDREN(305, "유아동"),
    CHILDREN_BOOKS(306, "유아도서"),
    WOMEN_CLOTHING(307, "여성의류"),
    WOMEN_ACCESSORIES(308, "여성잡화"),
    MEN_FASHION_ACCESSORIES(309, "남성패션/잡화"),
    BEAUTY_CARE(310, "뷰티/미용"),
    SPORTS_LEISURE(311, "스포츠/레저"),
    HOBBY_GAME_MUSIC(312, "취미/게임/음반"),
    BOOKS(313, "도서"),
    TICKETS_VOUCHERS(314, "티켓/교환권"),
    PROCESSED_FOOD(315, "가공식품"),
    HEALTH_SUPPLEMENTS(316, "건강기능식품"),
    PET_SUPPLIES(317, "반려동물용품"),
    PLANTS(318, "식물"),
    OTHER_USED_ITEMS(319, "기타 중고물품"),
    BUYING_REQUESTS(320, "삽니다");

    private final Integer categoryId;
    private final String description;

    ProductCategory(Integer categoryId, String description) {
        this.categoryId = categoryId;
        this.description = description;
    }
    public static ProductCategory fromCode(int code) {
        for (ProductCategory category : values()) {
            if (category.getCategoryId() == code) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid ProductCategory code: " + code);//추후 예외처리
    }

}
