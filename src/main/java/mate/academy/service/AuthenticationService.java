package mate.academy.service;

import mate.academy.dto.user.UserLoginRequestDto;
import mate.academy.dto.user.UserLoginResponseDto;
import mate.academy.exception.AuthenticationException;

public interface AuthenticationService {
    UserLoginResponseDto login(UserLoginRequestDto requestDto)
            throws AuthenticationException;
}
