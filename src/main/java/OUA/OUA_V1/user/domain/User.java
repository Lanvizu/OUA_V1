package OUA.OUA_V1.user.domain;

import OUA.OUA_V1.BaseEntity;
import OUA.OUA_V1.user.exception.badRequest.UserIllegalPhoneNumberException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private static final Pattern VALID_PHONE_NUMBER_PATTERN = Pattern.compile(
            "^(010)\\d{3,4}\\d{4}$|^(02|0[3-6][1-5])\\d{3,4}\\d{4}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    private String nickName;

    private String phone;

    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    public User(Long id, String email, String name, String nickName, String password, String phone) {
        this(email, name, nickName, password, phone);
        this.id = id;
    }

    public User(String email, String name, String nickName, String password, String phone) {
        validatePhoneNumber(phone);
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.password = password;
        this.phone = phone;
        this.userType = UserType.USER;
        this.loginType = LoginType.LOCAL;
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (!VALID_PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new UserIllegalPhoneNumberException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
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
