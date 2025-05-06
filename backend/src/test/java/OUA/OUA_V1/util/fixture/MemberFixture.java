package OUA.OUA_V1.util.fixture;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.domain.MemberRole;

public class MemberFixture {
    public static final Member DOBBY = createDobby();
    public static final Member RUSH = createRush();
    public static final Member ADMIN = createAdmin();

    public static Member createAdmin() {
        Member member = new Member("admin@email.com", "ADMIN", "ADMIN_NICKNAME",
                "$2a$10$rG0JsflKdGcORjGFTURYb.npEgtvClK4.3P.EMr/o3SdekrVFxOvG",
                "01011111111");// password 원문: qwer1234
        member.changeRole(MemberRole.ADMIN);
        return member;
    }

    public static Member createRush() {
        return new Member("rush@email.com", "RUSH", "RUSH_NICKNAME",
                "$2a$10$RKBwn5Sa7EO0lYaZqU1zSupNbPJ5/HOKcI7gNb9c2q.TiydBHUBQK",
                "01022222222"); // password 원문: newPassword214!
    }

    public static Member createDobby() {
        return new Member("dobby@email.com", "DOBBY", "DOBBY_NICKNAME",
                "$2a$10$RKBwn5Sa7EO0lYaZqU1zSupNbPJ5/HOKcI7gNb9c2q.TiydBHUBQK",
                "01011111111"); // password 원문: newPassword214!
    }

    public static Member createTestUser(String email) {
        return new Member(email, "TEST_USER", "TEST_NICKNAME",
                "$2a$10$RKBwn5Sa7EO0lYaZqU1zSupNbPJ5/HOKcI7gNb9c2q.TiydBHUBQK",
                "01033333333"); // password 원문: newPassword214!
    }
}
