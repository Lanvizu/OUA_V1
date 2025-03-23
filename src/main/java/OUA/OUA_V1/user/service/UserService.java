package OUA.OUA_V1.user.service;

import OUA.OUA_V1.auth.security.PasswordValidator;
import OUA.OUA_V1.user.controller.request.UserCreateRequest;
import OUA.OUA_V1.user.domain.User;
import OUA.OUA_V1.user.exception.UserNotFoundException;
import OUA.OUA_V1.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 32;
    private static final Pattern VALID_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*$");

    private final UserRepository userRepository;
    private final PasswordValidator passwordValidator;

    @Transactional
    public Long create(UserCreateRequest request) {

        String encodedPassword = generateEncoderPassword(request.password());
        User savedUser = userRepository.save(new User(request.email(), request.name(), request.nickName(), encodedPassword, request.phone()));
        return savedUser.getId();
    }

    private String generateEncoderPassword(String rawPassword) {
        validatePassword(rawPassword);
        return passwordValidator.encode(rawPassword);
    }

    private void validatePassword(String rawPassword) {
        if (rawPassword.length() < PASSWORD_MIN_LENGTH || rawPassword.length() > PASSWORD_MAX_LENGTH) {
            throw new RuntimeException();// 추후 예외 처리
        }
        if (!VALID_PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new RuntimeException();// 추후 예외 처리
        }
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }
}
