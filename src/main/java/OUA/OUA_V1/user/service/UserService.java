package OUA.OUA_V1.user.service;

import OUA.OUA_V1.auth.service.PasswordValidator;
import OUA.OUA_V1.user.controller.request.UserCreateRequest;
import OUA.OUA_V1.user.domain.User;
import OUA.OUA_V1.user.exception.UserEmailDuplicationException;
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
    public User create(UserCreateRequest request) {
        boolean exists = userRepository.existsByEmail(request.email());
        if (exists) {
            throw new UserEmailDuplicationException();
        }

        String encodedPassword = generateEncoderPassword(request.password());
        User newUser = new User(request.email(), request.name(), request.nickName(), encodedPassword, request.phone());
        return userRepository.save(newUser);
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

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }
}
