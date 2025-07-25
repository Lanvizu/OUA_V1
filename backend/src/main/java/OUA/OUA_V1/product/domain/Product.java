package OUA.OUA_V1.product.domain;

import OUA.OUA_V1.BaseEntity;
import OUA.OUA_V1.auth.util.SecureResource;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.order.domain.Orders;
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

    @Column(name = "highest_order_id")
    private Long highestOrderId;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "main_image_url")
    private String mainImageUrl;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_urls")
    private List<String> imageUrls = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orders> orders = new ArrayList<>();

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
        this.status = ProductStatus.ACTIVE;
        this.endDate = endDate;
        this.categoryId = categoryId;
        setImages(imageUrls);
    }

    private void setImages(List<String> imageUrls) {
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.mainImageUrl = !this.imageUrls.isEmpty() ? this.imageUrls.getFirst() : null;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ProductNameBlankException();
        }
        if (name.length() < NAME_MIN_LENGTH || name.length() > NAME_MAX_LENGTH) {
            throw new ProductNameLengthException(NAME_MIN_LENGTH, NAME_MAX_LENGTH, name.length());
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new ProductIllegalNameException();
        }
    }

    public void soldAuction() {
        this.status = ProductStatus.SOLD;
    }

    public void unSoldAuction() {
        this.status = ProductStatus.UNSOLD;
    }

    public void updateHighestOrder(Long orderId, int orderPrice) {
        this.highestOrderId = orderId;
        this.highestOrderPrice = orderPrice;
    }

    public void resetHighestOrder() {
        this.highestOrderId = null;
        this.highestOrderPrice = this.initialPrice;
    }

    public boolean isHighestOrder(Long orderId) {
        return this.highestOrderId != null && this.highestOrderId.equals(orderId);
    }

    public void cancel() {
        this.status = ProductStatus.CANCELED;
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