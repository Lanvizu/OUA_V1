package OUA.OUA_V1.member.controller.response;

public record AccountDetailsResponse(
        String email,
        String name,
        String nickName,
        String phone
) {
}
