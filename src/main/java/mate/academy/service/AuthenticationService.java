package mate.academy.service;

import mate.academy.dto.user.UserLoginRequestDto;
import mate.academy.exception.AuthenticationException;

public interface AuthenticationService {
    String login(UserLoginRequestDto requestDto)
            throws AuthenticationException;
}
