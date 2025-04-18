package OUA.OUA_V1.member.domain;

import OUA.OUA_V1.BaseEntity;
import OUA.OUA_V1.member.exception.badRequest.MemberIllegalPhoneNumberException;
import OUA.OUA_V1.order.domain.Order;
import OUA.OUA_V1.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Member extends BaseEntity {

    private static final Pattern VALID_PHONE_NUMBER_PATTERN = Pattern.compile(
            "^(010)\\d{3,4}\\d{4}$|^(02|0[3-6][1-5])\\d{3,4}\\d{4}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    private String nickName;

    private String password;

    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    public Member(String email, String name, String nickName, String password, String phone) {
        validatePhoneNumber(phone);
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.password = password;
        this.phone = phone;
        this.role = MemberRole.MEMBER;
    }

    public void changeRole(MemberRole role) {
        this.role = role;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (!VALID_PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new MemberIllegalPhoneNumberException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member member)) {
            return false;
        }
        return Objects.equals(id, member.id) && Objects.equals(email, member.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phone + '\'' +
                '}';
    }
}
