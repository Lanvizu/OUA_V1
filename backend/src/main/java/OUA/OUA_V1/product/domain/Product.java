package OUA.OUA_V1.product.domain;

import OUA.OUA_V1.BaseEntity;
import OUA.OUA_V1.auth.util.SecureResource;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.order.domain.Order;
import OUA.OUA_V1.product.exception.badRequest.ProductIllegalNameException;
import OUA.OUA_V1.product.exception.badRequest.ProductNameBlankException;
import OUA.OUA_V1.product.exception.badRequest.ProductNameLengthException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Product extends BaseEntity implements SecureResource {

    private static final int NAME_MIN_LENGTH = 2;
    private static final int NAME_MAX_LENGTH = 32;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[^\\\\|]*$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int initialPrice;

    private int buyNowPrice;

    private int highestOrderPrice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highest_order_id")
    private Order highestOrder;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "on_sale", nullable = false)
    private Boolean onSale;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_urls")
    private List<String> imageUrls = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    //즐겨찾기 수 추후 추가

    public Product(Member member, String name, String description, int initialPrice, int buyNowPrice,
                   LocalDateTime endDate, Integer categoryId, List<String> imageUrls) {
        this.member = member;
        validateName(name);
        this.name = name;
        this.description = description;
        this.initialPrice = initialPrice;
        this.highestOrderPrice = initialPrice;
        this.buyNowPrice = buyNowPrice;
        this.onSale = true;
        this.endDate = endDate;
        this.categoryId = categoryId;
        this.imageUrls = imageUrls;
    }

    private void validateName(String name) {
        if (name.isEmpty()) {
            throw new ProductNameBlankException();
        }
        if (name.length() < NAME_MIN_LENGTH || name.length() > NAME_MAX_LENGTH) {
            throw new ProductNameLengthException(NAME_MIN_LENGTH, NAME_MAX_LENGTH, name.length());
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new ProductIllegalNameException();
        }
    }

    public void updateHighestOrder(Order order) {
        this.highestOrderPrice = order.getOrderPrice();
        this.highestOrder = order;
    }

    public void resetHighestOrder() {
        this.highestOrder = null;
        this.highestOrderPrice = this.initialPrice;
    }

    public void cancel(){
        this.onSale = false;
    }

    @Override
    public boolean isAuthorizedBy(Member member) {
        return this.member.equals(member);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product product)) {
            return false;
        }
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", initialPrice='" + initialPrice + '\'' +
                ", buyNowPrice='" + buyNowPrice + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}