package OUA.OUA_V1.auth.facade;

import OUA.OUA_V1.auth.exception.LoginFailedException;
import OUA.OUA_V1.auth.controller.request.LoginRequest;
import OUA.OUA_V1.auth.service.AuthService;
import OUA.OUA_V1.user.domain.User;
import OUA.OUA_V1.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthService authService;
    private final UserService userService;

    public String login(LoginRequest request) {
        User user = userService.findByEmail(request.email());
        if (authService.isNotVerifiedPassword(request.password(), user.getPassword())) {
            throw new LoginFailedException();
        }
        return authService.createToken(user);
    }
}
