package OUA.OUA_V1.member.service;

import OUA.OUA_V1.auth.security.PasswordValidator;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.exception.MemberNotFoundException;
import OUA.OUA_V1.member.exception.badRequest.MemberIllegalPasswordException;
import OUA.OUA_V1.member.exception.badRequest.MemberPasswordLengthException;
import OUA.OUA_V1.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 32;
    private static final Pattern VALID_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*$");

    private final MemberRepository memberRepository;
    private final PasswordValidator passwordValidator;

    @Transactional
    public Member create(String email, String name, String nickName, String password, String phone) {
        String encodedPassword = generateEncoderPassword(password);
        Member savedMember = new Member(email, name, nickName, encodedPassword, phone);
        return memberRepository.save(savedMember);
    }

    private String generateEncoderPassword(String rawPassword) {
        validatePassword(rawPassword);
        return passwordValidator.encode(rawPassword);
    }

    private void validatePassword(String rawPassword) {
        if (rawPassword.length() < PASSWORD_MIN_LENGTH || rawPassword.length() > PASSWORD_MAX_LENGTH) {
            throw new MemberPasswordLengthException(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH, rawPassword.length());
        }
        if (!VALID_PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new MemberIllegalPasswordException();
        }
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
    }
}
